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

package org.apache.asterix.experiment.builder;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.apache.asterix.event.schema.cluster.Cluster;
import org.apache.asterix.experiment.action.base.IAction;
import org.apache.asterix.experiment.action.base.ParallelActionSet;
import org.apache.asterix.experiment.action.base.SequentialActionList;
import org.apache.asterix.experiment.action.derived.AbstractRemoteExecutableAction;
import org.apache.asterix.experiment.action.derived.ManagixActions.CreateAsterixManagixAction;
import org.apache.asterix.experiment.action.derived.ManagixActions.DeleteAsterixManagixAction;
import org.apache.asterix.experiment.action.derived.ManagixActions.StopAsterixManagixAction;
import org.apache.asterix.experiment.action.derived.RemoteAsterixDriverKill;
import org.apache.asterix.experiment.action.derived.RunAQLFileAction;
import org.apache.asterix.experiment.action.derived.SleepAction;
import org.apache.asterix.experiment.builder.cluster.IClusterBuilder;
import org.apache.asterix.experiment.builder.config.IAsterixConfigBuilder;
import org.apache.asterix.experiment.builder.counter.ICounterBuilder;
import org.apache.asterix.experiment.builder.dgen.IDgenBuilder;
import org.apache.asterix.experiment.builder.experiment.IExperimentBuilder;
import org.apache.asterix.experiment.builder.ingest.IIngestBuilder;
import org.apache.asterix.experiment.client.LSMExperimentConstants;
import org.apache.asterix.experiment.client.LSMExperimentSetRunner.LSMExperimentSetRunnerConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

public abstract class AbstractLSMBaseExperimentBuilder extends AbstractExperimentBuilder implements IClusterBuilder,
        IDgenBuilder, IExperimentBuilder, ICounterBuilder, IIngestBuilder, IAsterixConfigBuilder {

    private static final String ASTERIX_INSTANCE_NAME = "a1";

    private final int queryRunsNum;

    private final String logDirSuffix;

    protected final CloseableHttpClient httpClient;

    protected final String restHost;

    protected final int restPort;

    private final String managixHomePath;

    protected final String javaHomePath;

    protected final Path localExperimentRoot;

    protected final String username;

    protected final String sshKeyLocation;

    private final int duration;

    private final String clusterConfigFileName;

    private final String ingestFileName;

    protected final String dgenFileName;

    private final String countFileName;

    private final String asterixConfigFileName;

    private final String statFile;

    protected final SequentialActionList lsAction;

    protected final String openStreetMapFilePath;
    protected final int locationSampleInterval;

    protected final int recordCountPerBatchDuringIngestionOnly;
    protected final int recordCountPerBatchDuringQuery;
    protected final long dataGenSleepTimeDuringIngestionOnly;
    protected final long dataGenSleepTimeDuringQuery;

    private static final Logger LOGGER = Logger.getLogger(AbstractLSMBaseExperimentBuilder.class.getName());

    public AbstractLSMBaseExperimentBuilder(LSMExperimentSetRunnerConfig config) {
        this.logDirSuffix = config.getLogDirSuffix();
        this.queryRunsNum = config.getNQueryRuns();
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setDefaultMaxPerRoute(10);
        cm.setMaxTotal(100);
        this.httpClient = HttpClients.custom().setConnectionManager(cm).setMaxConnPerRoute(100).build();
        this.restHost = config.getRESTHost();
        this.restPort = config.getRESTPort();
        this.managixHomePath = config.getManagixHome();
        this.javaHomePath = config.getJavaHome();
        this.localExperimentRoot = Paths.get(config.getLocalExperimentRoot());
        this.username = config.getUsername();
        this.sshKeyLocation = config.getSSHKeyLocation();
        this.duration = config.getDuration();
        this.clusterConfigFileName = getClusterConfig();
        this.ingestFileName = getIngestConfig();
        this.dgenFileName = getDgenConfig();
        this.countFileName = getCounter();
        this.asterixConfigFileName = getAsterixConfig();
        this.statFile = config.getStatFile();
        this.lsAction = new SequentialActionList();
        this.openStreetMapFilePath = config.getOpenStreetMapFilePath();
        this.locationSampleInterval = config.getLocationSampleInterval();
        recordCountPerBatchDuringIngestionOnly = config.getRecordCountPerBatchDuringIngestionOnly();
        recordCountPerBatchDuringQuery = config.getRecordCountPerBatchDuringQuery();
        dataGenSleepTimeDuringIngestionOnly = config.getDataGenSleepTimeDuringIngestionOnly();
        dataGenSleepTimeDuringQuery = config.getDataGenSleepTimeDuringQuery();
    }

    protected void doBuildDDL(SequentialActionList seq) {
        seq.add(new RunAQLFileAction(httpClient, restHost, restPort,
                localExperimentRoot.resolve(LSMExperimentConstants.AQL_DIR).resolve(getExperimentDDL())));
    }

    protected void doPost(SequentialActionList seq) {
    }

    protected void doBuildDataGen(SequentialActionList seq, final Map<String, List<String>> dgenPairs,
            DataInputStream dgenSeedStream) throws Exception {

        //start datagen
        ParallelActionSet dgenActions = new ParallelActionSet();
        int partition = 0;
        for (String dgenHost : dgenPairs.keySet()) {
            final List<String> rcvrs = dgenPairs.get(dgenHost);
            final int p = partition;
            dgenActions.add(new AbstractRemoteExecutableAction(dgenHost, username, sshKeyLocation) {

                @Override
                protected String getCommand() {
                    long dgenSeed = 0;
                    try {
                        dgenSeed = dgenSeedStream.readLong();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    String ipPortPairs = StringUtils.join(rcvrs.iterator(), " ");
                    String binary = "JAVA_HOME=" + javaHomePath + " "
                            + localExperimentRoot.resolve("bin").resolve("datagenrunner").toString();
                    if (openStreetMapFilePath == null) {
                        return StringUtils.join(new String[] { binary, "-rcbi",
                                "" + recordCountPerBatchDuringIngestionOnly, "-rcbq",
                                "" + recordCountPerBatchDuringQuery, "-dsti", "" + dataGenSleepTimeDuringIngestionOnly,
                                "-dstq", "" + dataGenSleepTimeDuringQuery, "-si", "" + locationSampleInterval, "-p",
                                "" + p, "-d", "" + duration, "-s", "" + dgenSeed, ipPortPairs }, " ");
                    } else {
                        return StringUtils.join(new String[] { binary, "-rcbi",
                                "" + recordCountPerBatchDuringIngestionOnly, "-rcbq",
                                "" + recordCountPerBatchDuringQuery, "-dsti", "" + dataGenSleepTimeDuringIngestionOnly,
                                "-dstq", "" + dataGenSleepTimeDuringQuery, "-si", "" + locationSampleInterval, "-of",
                                openStreetMapFilePath, "-p", "" + p, "-d", "" + duration, ipPortPairs }, " ");
                    }
                }
            });
            partition += rcvrs.size();
        }
        seq.add(dgenActions);
    }

    @Override
    protected void doBuild(Experiment e) throws Exception {
        SequentialActionList execs = new SequentialActionList();

        String clusterConfigPath = localExperimentRoot.resolve(LSMExperimentConstants.CONFIG_DIR)
                .resolve(clusterConfigFileName).toString();
        String asterixConfigPath = localExperimentRoot.resolve(LSMExperimentConstants.CONFIG_DIR)
                .resolve(LSMExperimentConstants.ASTERIX_CONFIGURATION_DIR).resolve(asterixConfigFileName).toString();

        //create instance
        execs.add(new StopAsterixManagixAction(managixHomePath, ASTERIX_INSTANCE_NAME));
        execs.add(new DeleteAsterixManagixAction(managixHomePath, ASTERIX_INSTANCE_NAME));
        execs.add(new SleepAction(3000));
        execs.add(new CreateAsterixManagixAction(managixHomePath, ASTERIX_INSTANCE_NAME, clusterConfigPath,
                asterixConfigPath));

        Map<String, List<String>> dgenPairs = readDatagenPairs(
                localExperimentRoot.resolve(LSMExperimentConstants.DGEN_DIR).resolve(dgenFileName));
        final Set<String> ncHosts = new HashSet<>();
        for (List<String> ncHostList : dgenPairs.values()) {
            for (String ncHost : ncHostList) {
                ncHosts.add(ncHost.split(":")[0]);
            }
        }

        execs.add(new SleepAction(2000));
        //run ddl statements
        // TODO: implement retry handler
        execs.add(new RunAQLFileAction(httpClient, restHost, restPort, localExperimentRoot
                .resolve(LSMExperimentConstants.AQL_DIR).resolve(LSMExperimentConstants.BASE_TYPES)));
        doBuildDDL(execs);
        execs.add(new RunAQLFileAction(httpClient, restHost, restPort,
                localExperimentRoot.resolve(LSMExperimentConstants.AQL_DIR).resolve(LSMExperimentConstants.BASE_DIR)
                        .resolve(ingestFileName)));

        if (statFile != null) {
            ParallelActionSet ioCountActions = new ParallelActionSet();
            for (String ncHost : ncHosts) {
                ioCountActions.add(new AbstractRemoteExecutableAction(ncHost, username, sshKeyLocation) {

                    @Override
                    protected String getCommand() {
                        String cmd = "screen -d -m sh -c \"sar -b -u 1 > " + statFile + "\"";
                        return cmd;
                    }
                });
            }
            execs.add(ioCountActions);
        }

        //        SequentialActionList postLSAction = new SequentialActionList();
        //        String[] storageRoots = cluster.getIodevices().split(",");
        //        for (String ncHost : ncHosts) {
        //            for (final String sRoot : storageRoots) {
        //                lsAction.add(new AbstractRemoteExecutableAction(ncHost, username, sshKeyLocation) {
        //                    @Override
        //                    protected String getCommand() {
        //                        return "ls -Rl " + sRoot;
        //                    }
        //                });
        //                postLSAction.add(new AbstractRemoteExecutableAction(ncHost, username, sshKeyLocation) {
        //                    @Override
        //                    protected String getCommand() {
        //                        return "ls -Rl " + sRoot;
        //                    }
        //                });
        //
        //            }
        //        }

        PipedOutputStream pos = new PipedOutputStream();
        PipedInputStream pis = new PipedInputStream(pos);
        DataOutputStream dos = new DataOutputStream(pos);
        DataInputStream dis = new DataInputStream(pis);
        dos.writeLong(0l);
        for (int runNum = 0; runNum < queryRunsNum; runNum++) {
            final int finalRunNum = runNum + 1;
            execs.add(new IAction() {
                @Override
                public void perform() {
                    if (LOGGER.isLoggable(Level.INFO)) {
                        LOGGER.info("Executing experiment #" + finalRunNum + " ...");
                    }
                }
            });
            // main exp
            doBuildDataGen(execs, dgenPairs, dis);

            execs.add(new SleepAction(5000));
            if (countFileName != null) {
                execs.add(new RunAQLFileAction(httpClient, restHost, restPort,
                        localExperimentRoot.resolve(LSMExperimentConstants.AQL_DIR).resolve(countFileName), dos));
            }
        }

        //        if (statFile != null) {
        //            ParallelActionSet ioCountKillActions = new ParallelActionSet();
        //            for (String ncHost : ncHosts) {
        //                ioCountKillActions.add(new AbstractRemoteExecutableAction(ncHost, username, sshKeyLocation) {
        //
        //                    @Override
        //                    protected String getCommand() {
        //                        String cmd = "screen -X -S `screen -list | grep Detached | awk '{print $1}'` quit";
        //                        return cmd;
        //                    }
        //                });
        //            }
        //            execs.add(ioCountKillActions);
        //        }

        doPost(execs);

        //        execs.add(postLSAction);
        ParallelActionSet killCmds = new ParallelActionSet();
        for (String ncHost : ncHosts) {
            killCmds.add(new RemoteAsterixDriverKill(ncHost, username, sshKeyLocation));
        }
        killCmds.add(new RemoteAsterixDriverKill(restHost, username, sshKeyLocation));
        execs.add(killCmds);
        execs.add(new StopAsterixManagixAction(managixHomePath, ASTERIX_INSTANCE_NAME));
        if (statFile != null) {
            File file = new File(clusterConfigPath);
            JAXBContext ctx = JAXBContext.newInstance(Cluster.class);
            Unmarshaller unmarshaller = ctx.createUnmarshaller();
            final Cluster cluster = (Cluster) unmarshaller.unmarshal(file);

            ParallelActionSet collectIOActions = new ParallelActionSet();
            for (String ncHost : ncHosts) {
                collectIOActions.add(new AbstractRemoteExecutableAction(ncHost, username, sshKeyLocation) {

                    @Override
                    protected String getCommand() {
                        String cmd = "cp " + statFile + " " + cluster.getLogDir();
                        return cmd;
                    }
                });
            }
            execs.add(collectIOActions);

        }

        //collect profile information
        //        if (ExperimentProfiler.PROFILE_MODE) {
        //            if (!SpatialIndexProfiler.PROFILE_HOME_DIR.contentEquals(cluster.getLogDir())) {
        //                ParallelActionSet collectProfileInfo = new ParallelActionSet();
        //                for (String ncHost : ncHosts) {
        //                    collectProfileInfo.add(new AbstractRemoteExecutableAction(ncHost, username, sshKeyLocation) {
        //                        @Override
        //                        protected String getCommand() {
        //                            String cmd = "mv " + SpatialIndexProfiler.PROFILE_HOME_DIR + "*.txt " + cluster.getLogDir();
        //                            return cmd;
        //                        }
        //                    });
        //                }
        //                execs.add(collectProfileInfo);
        //            }
        //        }

        //        execs.add(new LogAsterixManagixAction(managixHomePath, ASTERIX_INSTANCE_NAME, localExperimentRoot
        //                .resolve(LSMExperimentConstants.LOG_DIR + "-" + logDirSuffix).resolve(getName()).toString()));

        if (getName().contains("SpatialIndexExperiment2") || getName().contains("SpatialIndexExperiment5")) {

            //get query result file
            SequentialActionList getQueryResultFileActions = new SequentialActionList();
            final String queryResultFilePath = openStreetMapFilePath.substring(0,
                    openStreetMapFilePath.lastIndexOf(File.separator)) + File.separator + "QueryGenResult-*.txt";
            for (final String qgenHost : dgenPairs.keySet())

            {
                getQueryResultFileActions.add(new AbstractRemoteExecutableAction(restHost, username, sshKeyLocation) {

                    @Override
                    protected String getCommand() {
                        String cmd = "scp " + username + "@" + qgenHost + ":" + queryResultFilePath + " "
                                + localExperimentRoot.resolve(LSMExperimentConstants.LOG_DIR + "-" + logDirSuffix)
                                        .resolve(getName()).toString();
                        return cmd;
                    }
                });
            }
            execs.add(getQueryResultFileActions);

        }

        e.addBody(execs);
    }

    protected Map<String, List<String>> readDatagenPairs(Path p) throws IOException {
        Map<String, List<String>> dgenPairs = new HashMap<>();
        Scanner s = new Scanner(p, StandardCharsets.UTF_8.name());
        try {
            while (s.hasNextLine()) {
                String line = s.nextLine();
                String[] pair = line.split("\\s+");
                List<String> vals = dgenPairs.get(pair[0]);
                if (vals == null) {
                    vals = new ArrayList<>();
                    dgenPairs.put(pair[0], vals);
                }
                vals.add(pair[1]);
            }
        } finally {
            s.close();
        }
        return dgenPairs;
    }
}
