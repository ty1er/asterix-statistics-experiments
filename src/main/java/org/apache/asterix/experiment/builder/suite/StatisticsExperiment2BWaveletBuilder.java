package org.apache.asterix.experiment.builder.suite;

import org.apache.asterix.experiment.builder.AbstractLSMBaseExperimentBuilder;
import org.apache.asterix.experiment.builder.cluster.ICluster1Builder;
import org.apache.asterix.experiment.builder.config.IWaveletConfigBuilder;
import org.apache.asterix.experiment.builder.counter.IRecordsCounterBuilder;
import org.apache.asterix.experiment.builder.dgen.IDgen2Builder;
import org.apache.asterix.experiment.builder.experiment.IExperiment2BBuilder;
import org.apache.asterix.experiment.builder.ingest.IIngest2Builder;
import org.apache.asterix.experiment.client.LSMExperimentSetRunner.LSMExperimentSetRunnerConfig;

public class StatisticsExperiment2BWaveletBuilder extends AbstractLSMBaseExperimentBuilder
        implements IExperiment2BBuilder, IDgen2Builder, ICluster1Builder, IIngest2Builder, IRecordsCounterBuilder,
        IWaveletConfigBuilder {

    public StatisticsExperiment2BWaveletBuilder(LSMExperimentSetRunnerConfig config) {
        super(config);
    }

}
