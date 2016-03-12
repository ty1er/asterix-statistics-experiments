package org.apache.asterix.experiment.builder;

import org.apache.asterix.experiment.builder.cluster.ICluster1Builder;
import org.apache.asterix.experiment.builder.config.IWaveletConfigBuilder;
import org.apache.asterix.experiment.builder.counter.IRecordsCounterBuilder;
import org.apache.asterix.experiment.builder.dgen.IDgen2Builder;
import org.apache.asterix.experiment.builder.experiment.IExperiment1Builder;
import org.apache.asterix.experiment.builder.ingest.IIngest2Builder;
import org.apache.asterix.experiment.client.LSMExperimentSetRunner.LSMExperimentSetRunnerConfig;

public class StatisticsExperiment1Cluster1Ingest2WaveletBuilder extends AbstractLSMBaseExperimentBuilder
        implements IExperiment1Builder, IDgen2Builder, ICluster1Builder, IIngest2Builder, IRecordsCounterBuilder,
        IWaveletConfigBuilder {

    public StatisticsExperiment1Cluster1Ingest2WaveletBuilder(LSMExperimentSetRunnerConfig config) {
        super(config);
    }

    @Override
    public String getName() {
        return "StatisticsExperiment1Cluster1Ingest2Wavelet";
    }

}
