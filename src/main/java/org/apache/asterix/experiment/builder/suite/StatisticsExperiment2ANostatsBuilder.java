package org.apache.asterix.experiment.builder.suite;

import org.apache.asterix.experiment.builder.AbstractLSMBaseExperimentBuilder;
import org.apache.asterix.experiment.builder.cluster.ICluster1Builder;
import org.apache.asterix.experiment.builder.config.INoStatsConfigBuilder;
import org.apache.asterix.experiment.builder.counter.IRecordsCounterBuilder;
import org.apache.asterix.experiment.builder.dgen.IDgen2Builder;
import org.apache.asterix.experiment.builder.experiment.IExperiment2ABuilder;
import org.apache.asterix.experiment.builder.ingest.IIngest2Builder;
import org.apache.asterix.experiment.client.LSMExperimentSetRunner.LSMExperimentSetRunnerConfig;

public class StatisticsExperiment2ANostatsBuilder extends AbstractLSMBaseExperimentBuilder
        implements IExperiment2ABuilder, IDgen2Builder, ICluster1Builder, IIngest2Builder, IRecordsCounterBuilder,
        INoStatsConfigBuilder {

    public StatisticsExperiment2ANostatsBuilder(LSMExperimentSetRunnerConfig config) {
        super(config);
    }

}
