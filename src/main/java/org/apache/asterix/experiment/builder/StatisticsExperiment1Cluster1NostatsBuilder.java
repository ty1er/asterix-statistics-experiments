package org.apache.asterix.experiment.builder;

import org.apache.asterix.experiment.builder.cluster.ICluster1Builder;
import org.apache.asterix.experiment.builder.config.INoStatsConfigBuilder;
import org.apache.asterix.experiment.builder.dgen.IDgen1Builder;
import org.apache.asterix.experiment.builder.experiment.IExperiment1Builder;
import org.apache.asterix.experiment.builder.ingest.IIngest1Builder;
import org.apache.asterix.experiment.client.LSMExperimentSetRunner.LSMExperimentSetRunnerConfig;

public class StatisticsExperiment1Cluster1NostatsBuilder extends AbstractLSMBaseExperimentBuilder
        implements IExperiment1Builder, IDgen1Builder, ICluster1Builder, IIngest1Builder, INoStatsConfigBuilder {

    public StatisticsExperiment1Cluster1NostatsBuilder(LSMExperimentSetRunnerConfig config) {
        super(config);
    }

    @Override
    public String getName() {
        return "StatisticsExperiment1Cluster1Nostats";
    }

}
