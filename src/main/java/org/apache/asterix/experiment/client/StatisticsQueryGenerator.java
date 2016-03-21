/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.asterix.experiment.client;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.asterix.experiment.action.base.IAction;
import org.apache.asterix.experiment.action.base.SequentialActionList;
import org.apache.asterix.experiment.action.derived.RunAQLStringAction;
import org.apache.asterix.experiment.action.derived.TimedAction;
import org.apache.asterix.experiment.querygen.RangeGenerator;
import org.apache.asterix.experiment.util.ExperimentProfilerUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

public class StatisticsQueryGenerator {

    private final ExecutorService threadPool;

    private final StatisticsQueryGeneratorConfig config;

    public StatisticsQueryGenerator(StatisticsQueryGeneratorConfig config) {
        threadPool = Executors.newCachedThreadPool(new ThreadFactory() {

            private final AtomicInteger count = new AtomicInteger();

            @Override
            public Thread newThread(Runnable r) {
                int tid = count.getAndIncrement();
                Thread t = new Thread(r, "DataGeneratorThread: " + tid);
                t.setDaemon(true);
                return t;
            }
        });
        this.config = config;
    }

    public void start() throws Exception {
        final Semaphore sem = new Semaphore(0);
        threadPool.submit(new QueryGenerator(sem, config));
        sem.acquire();
    }

    public static class QueryGenerator implements Runnable {

        private static final Logger LOGGER = Logger.getLogger(QueryGenerator.class.getName());

        private final HttpClient httpClient;
        private final Semaphore sem;
        private final int[] range = new int[] { 10, 100, 1000, 10000, 100000, 1000000 };
        private int radiusIter = 0;
        private long queryCount;
        private RangeGenerator rangeGen;
        private FileOutputStream outputFos;
        private final StatisticsQueryGeneratorConfig config;

        public QueryGenerator(Semaphore sem, StatisticsQueryGeneratorConfig config) {
            httpClient = new DefaultHttpClient();
            this.sem = sem;
            this.queryCount = 0;
            this.config = config;
        }

        @Override
        public void run() {
            LOGGER.info("\nQueryGen[" + config.getPartitionRangeStart() + "] running with the following parameters: \n"
                    + "queryGenDuration : " + config.getDuration());

            try {
                outputFos = ExperimentProfilerUtils.openOutputFile(config.getOutputPath() + File.separator
                        + "QueryGenResult-" + InetAddress.getLocalHost().getHostAddress() + ".txt");
                rangeGen = RangeGenerator.getRangeGenerator(config.getQueryType(), config.getUpperBound(),
                        config.getRangeLength());
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

            try {
                try {
                    if (LOGGER.isLoggable(Level.INFO)) {
                        LOGGER.info("QueryGen[" + config.getPartitionRangeStart() + "] starts sending queries...");
                    }
                    //send queries during query duration
                    long startTS = System.currentTimeMillis();
                    long prevTS = startTS;
                    long curTS = startTS;
                    while (curTS - startTS < config.getDuration() * 1000) {
                        sendQuery();
                        queryCount++;
                        curTS = System.currentTimeMillis();
                        if (LOGGER.isLoggable(Level.INFO) && queryCount % 100 == 0) {
                            LOGGER.info("QueryGen[" + config.getPartitionRangeStart() + "][TimeToQuery100] "
                                    + (curTS - prevTS) + " in milliseconds");
                            prevTS = curTS;
                        }
                    }
                    if (LOGGER.isLoggable(Level.INFO)) {
                        LOGGER.info("QueryGen[" + config.getPartitionRangeStart() + "][QueryCount] " + queryCount
                                + " in " + config.getDuration() + " seconds");
                    }

                    if (outputFos != null) {
                        ExperimentProfilerUtils.closeOutputFile(outputFos);
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                    outputFos.write("Error during sending query\n".getBytes());
                    throw t;
                } finally {
                    if (outputFos != null) {
                        ExperimentProfilerUtils.closeOutputFile(outputFos);
                    }
                }
            } catch (Throwable t) {
                System.err.println(
                        "Error connecting to rest API server " + config.getRESTHost() + ":" + config.getRESTPort());
                t.printStackTrace();
            } finally {
                sem.release();
            }
        }

        private void sendQuery() throws IOException {
            //prepare radius and center point
            Pair<Integer, Integer> range = rangeGen.getNextRange();
            //create action
            SequentialActionList sAction = new SequentialActionList();
            IAction rangeQueryAction = new TimedAction(new RunAQLStringAction(httpClient, config.getRESTHost(),
                    config.getRESTPort(), getRangeQueryAQL(range), outputFos), outputFos);
            sAction.add(rangeQueryAction);

            //perform
            sAction.perform();
        }

        private String getRangeQueryAQL(Pair<Integer, Integer> range) {
            final String fieldName = "friends_count";
            StringBuilder sb = new StringBuilder();
            sb.append("use dataverse experiments; ");
            sb.append("count( ");
            sb.append("for $x in dataset Tweets").append(" ");
            sb.append("where $x.").append(fieldName).append(" >= int32(\"").append(range.getLeft())
                    .append("\") and $x.").append(fieldName).append(" <= int32(\"").append(range.getRight())
                    .append("\") ");
            sb.append("return $x ");
            sb.append(");");
            return sb.toString();
        }

    }

    public static void main(String[] args) throws Exception {
        StatisticsQueryGeneratorConfig queryGenConfig = new StatisticsQueryGeneratorConfig();
        CmdLineParser clp = new CmdLineParser(queryGenConfig);
        try {
            clp.parseArgument(args);
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            clp.printUsage(System.err);
            System.exit(1);
        }

        StatisticsQueryGenerator client = new StatisticsQueryGenerator(queryGenConfig);
        client.start();
    }
}
