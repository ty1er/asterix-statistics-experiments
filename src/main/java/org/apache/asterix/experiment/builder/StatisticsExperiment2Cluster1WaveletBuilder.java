package org.apache.asterix.experiment.builder;

import org.apache.asterix.experiment.builder.cluster.ICluster1Builder;
import org.apache.asterix.experiment.builder.config.IWaveletConfigBuilder;
import org.apache.asterix.experiment.builder.counter.IRecordsCounterBuilder;
import org.apache.asterix.experiment.builder.dgen.IDgen1Builder;
import org.apache.asterix.experiment.builder.experiment.IExperiment2Builder;
import org.apache.asterix.experiment.builder.ingest.IIngest1Builder;
import org.apache.asterix.experiment.client.LSMExperimentSetRunner.LSMExperimentSetRunnerConfig;

public class StatisticsExperiment2Cluster1WaveletBuilder extends AbstractLSMBaseExperimentBuilder
        implements IExperiment2Builder, IDgen1Builder, ICluster1Builder, IIngest1Builder, IRecordsCounterBuilder,
        IWaveletConfigBuilder {

    public StatisticsExperiment2Cluster1WaveletBuilder(LSMExperimentSetRunnerConfig config) {
        super(config);
    }

    @Override
    public String getName() {
        return "StatisticsExperiment2Cluster1Wavelet";
    }
}
