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

import org.apache.asterix.experiment.querygen.StatisticsQueryType;
import org.apache.commons.lang3.tuple.Pair;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.OptionDef;
import org.kohsuke.args4j.spi.OptionHandler;
import org.kohsuke.args4j.spi.Parameters;
import org.kohsuke.args4j.spi.Setter;

public class StatisticsQueryGeneratorConfig {

    @Option(name = "-p", aliases = "--partition-range-start", usage = "Starting partition number for the set of data generators (default = 0)")
    private int partitionRangeStart = 0;

    public int getPartitionRangeStart() {
        return partitionRangeStart;
    }

    @Option(name = "-qd", aliases = { "--duration" }, usage = "Duration in seconds to run guery generation")
    private int queryGenDuration = -1;

    public int getDuration() {
        return queryGenDuration;
    }

    @Option(name = "-qc", aliases = { "--query-count" }, usage = "The number of queries to generate")
    private int queryCount = -1;

    public int getQueryCount() {
        return queryCount;
    }

    @Option(name = "-rh", aliases = "--rest-host", usage = "Asterix REST API host address", required = true, metaVar = "HOST")
    private String restHost;

    public String getRESTHost() {
        return restHost;
    }

    @Option(name = "-rp", aliases = "--rest-port", usage = "Asterix REST API port", required = true, metaVar = "PORT")
    private int restPort;

    public int getRESTPort() {
        return restPort;
    }

    @Option(name = "-o", aliases = "--output-filepath", usage = "The directiry to store query output files")
    private String outputPath;

    public String getOutputPath() {
        return outputPath;
    }

    @Argument(required = true, usage = "Query type for the range generators [] (default = Random)")
    private StatisticsQueryType queryType = StatisticsQueryType.Random;

    public StatisticsQueryType getQueryType() {
        return queryType;
    }

    @Option(name = "-u", aliases = "--upper-bound", usage = "The maximum possible value for range borders")
    private int upperBound;

    public int getUpperBound() {
        return upperBound;
    }

    @Option(name = "-l", aliases = "--range-length", usage = "The range for fix-sized range queries")
    private int rangeLength;

    public int getRangeLength() {
        return rangeLength;
    }

    public static class AddressOptionHandler extends OptionHandler<Pair<String, Integer>> {

        public AddressOptionHandler(CmdLineParser parser, OptionDef option,
                Setter<? super Pair<String, Integer>> setter) {
            super(parser, option, setter);
        }

        @Override
        public int parseArguments(Parameters params) throws CmdLineException {
            int counter = 0;
            while (true) {
                String param;
                try {
                    param = params.getParameter(counter);
                } catch (CmdLineException ex) {
                    break;
                }

                String[] hostPort = param.split(":");
                if (hostPort.length != 2) {
                    throw new CmdLineException("Invalid address: " + param + ". Expected <host>:<port>");
                }
                Integer port = null;
                try {
                    port = Integer.parseInt(hostPort[1]);
                } catch (NumberFormatException e) {
                    throw new CmdLineException("Invalid port " + hostPort[1] + " for address " + param + ".");
                }
                setter.addValue(Pair.of(hostPort[0], port));
                counter++;
            }
            return counter;
        }

        @Override
        public String getDefaultMetaVariable() {
            return "addresses";
        }

    }
}
