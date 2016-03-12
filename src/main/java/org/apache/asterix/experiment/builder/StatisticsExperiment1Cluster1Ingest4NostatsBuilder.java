package org.apache.asterix.experiment.builder;

import org.apache.asterix.experiment.builder.cluster.ICluster1Builder;
import org.apache.asterix.experiment.builder.config.INoStatsConfigBuilder;
import org.apache.asterix.experiment.builder.counter.IRecordsCounterBuilder;
import org.apache.asterix.experiment.builder.dgen.IDgen4Builder;
import org.apache.asterix.experiment.builder.experiment.IExperiment1Builder;
import org.apache.asterix.experiment.builder.ingest.IIngest4Builder;
import org.apache.asterix.experiment.client.LSMExperimentSetRunner.LSMExperimentSetRunnerConfig;

public class StatisticsExperiment1Cluster1Ingest4NostatsBuilder extends AbstractLSMBaseExperimentBuilder
        implements IExperiment1Builder, IDgen4Builder, ICluster1Builder, IIngest4Builder, IRecordsCounterBuilder,
        INoStatsConfigBuilder {

    public StatisticsExperiment1Cluster1Ingest4NostatsBuilder(LSMExperimentSetRunnerConfig config) {
        super(config);
    }

    @Override
    public String getName() {
        return "StatisticsExperiment1Cluster1Ingest4Nostats";
    }

}
