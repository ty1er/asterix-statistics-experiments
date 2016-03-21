package org.apache.asterix.experiment.builder.suite;

import org.apache.asterix.experiment.builder.AbstractLSMBaseExperimentBuilder;
import org.apache.asterix.experiment.builder.cluster.ICluster1Builder;
import org.apache.asterix.experiment.builder.config.IUniformHistConfigBuilder;
import org.apache.asterix.experiment.builder.counter.IRecordsCounterBuilder;
import org.apache.asterix.experiment.builder.dgen.IDgen2Builder;
import org.apache.asterix.experiment.builder.experiment.IExperiment2CBuilder;
import org.apache.asterix.experiment.builder.ingest.IIngest2Builder;
import org.apache.asterix.experiment.client.LSMExperimentSetRunner.LSMExperimentSetRunnerConfig;

public class StatisticsExperiment2CUniformHistBuilder extends AbstractLSMBaseExperimentBuilder
        implements IExperiment2CBuilder, IDgen2Builder, ICluster1Builder, IIngest2Builder, IRecordsCounterBuilder,
        IUniformHistConfigBuilder {

    public StatisticsExperiment2CUniformHistBuilder(LSMExperimentSetRunnerConfig config) {
        super(config);
    }

}
