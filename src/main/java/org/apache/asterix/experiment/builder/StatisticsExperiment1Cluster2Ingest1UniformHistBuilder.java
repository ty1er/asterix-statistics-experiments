package org.apache.asterix.experiment.builder;

import org.apache.asterix.experiment.builder.cluster.ICluster2Builder;
import org.apache.asterix.experiment.builder.config.IUniformHistConfigBuilder;
import org.apache.asterix.experiment.builder.counter.IRecordsCounterBuilder;
import org.apache.asterix.experiment.builder.dgen.IDgen2Builder;
import org.apache.asterix.experiment.builder.experiment.IExperiment1Builder;
import org.apache.asterix.experiment.builder.ingest.IIngest1Builder;
import org.apache.asterix.experiment.client.LSMExperimentSetRunner.LSMExperimentSetRunnerConfig;

public class StatisticsExperiment1Cluster2Ingest1UniformHistBuilder extends AbstractLSMBaseExperimentBuilder
        implements IExperiment1Builder, IDgen2Builder, ICluster2Builder, IIngest1Builder, IRecordsCounterBuilder,
        IUniformHistConfigBuilder {

    public StatisticsExperiment1Cluster2Ingest1UniformHistBuilder(LSMExperimentSetRunnerConfig config) {
        super(config);
    }

    @Override
    public String getName() {
        return "StatisticsExperiment1Cluster2Ingest1UniformHist";
    }
}
