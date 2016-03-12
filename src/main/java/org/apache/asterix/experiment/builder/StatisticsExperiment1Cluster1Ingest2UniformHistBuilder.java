package org.apache.asterix.experiment.builder;

import org.apache.asterix.experiment.builder.cluster.ICluster1Builder;
import org.apache.asterix.experiment.builder.config.IUniformHistConfigBuilder;
import org.apache.asterix.experiment.builder.counter.IRecordsCounterBuilder;
import org.apache.asterix.experiment.builder.dgen.IDgen2Builder;
import org.apache.asterix.experiment.builder.experiment.IExperiment1Builder;
import org.apache.asterix.experiment.builder.ingest.IIngest2Builder;
import org.apache.asterix.experiment.client.LSMExperimentSetRunner.LSMExperimentSetRunnerConfig;

public class StatisticsExperiment1Cluster1Ingest2UniformHistBuilder extends AbstractLSMBaseExperimentBuilder
        implements IExperiment1Builder, IDgen2Builder, ICluster1Builder, IIngest2Builder, IRecordsCounterBuilder,
        IUniformHistConfigBuilder {

    public StatisticsExperiment1Cluster1Ingest2UniformHistBuilder(LSMExperimentSetRunnerConfig config) {
        super(config);
    }

    @Override
    public String getName() {
        return "StatisticsExperiment1Cluster1Ingest2UniformHist";
    }

}
