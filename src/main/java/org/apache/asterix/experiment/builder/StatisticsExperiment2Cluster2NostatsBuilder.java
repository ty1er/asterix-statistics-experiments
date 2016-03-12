package org.apache.asterix.experiment.builder;

import org.apache.asterix.experiment.builder.cluster.ICluster2Builder;
import org.apache.asterix.experiment.builder.config.INoStatsConfigBuilder;
import org.apache.asterix.experiment.builder.counter.IRecordsCounterBuilder;
import org.apache.asterix.experiment.builder.dgen.IDgen2Builder;
import org.apache.asterix.experiment.builder.experiment.IExperiment2Builder;
import org.apache.asterix.experiment.builder.ingest.IIngest2Builder;
import org.apache.asterix.experiment.client.LSMExperimentSetRunner.LSMExperimentSetRunnerConfig;

public class StatisticsExperiment2Cluster2NostatsBuilder extends AbstractLSMBaseExperimentBuilder
        implements IExperiment2Builder, IDgen2Builder, ICluster2Builder, IIngest2Builder, IRecordsCounterBuilder,
        INoStatsConfigBuilder {

    public StatisticsExperiment2Cluster2NostatsBuilder(LSMExperimentSetRunnerConfig config) {
        super(config);
    }

    @Override
    public String getName() {
        return "StatisticsExperiment2Cluster2Nostats";
    }
}
