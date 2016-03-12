package org.apache.asterix.experiment.builder;

import org.apache.asterix.experiment.builder.cluster.ICluster1Builder;
import org.apache.asterix.experiment.builder.config.IContinuousHistConfigBuilder;
import org.apache.asterix.experiment.builder.counter.IRecordsCounterBuilder;
import org.apache.asterix.experiment.builder.dgen.IDgen4Builder;
import org.apache.asterix.experiment.builder.experiment.IExperiment1Builder;
import org.apache.asterix.experiment.builder.ingest.IIngest4Builder;
import org.apache.asterix.experiment.client.LSMExperimentSetRunner.LSMExperimentSetRunnerConfig;

public class StatisticsExperiment1Cluster1Ingest4ContinuousHistBuilder extends AbstractLSMBaseExperimentBuilder
        implements IExperiment1Builder, IDgen4Builder, ICluster1Builder, IIngest4Builder, IRecordsCounterBuilder,
        IContinuousHistConfigBuilder {

    public StatisticsExperiment1Cluster1Ingest4ContinuousHistBuilder(LSMExperimentSetRunnerConfig config) {
        super(config);
    }

    @Override
    public String getName() {
        return "StatisticsExperiment1Cluster1Ingest4ContinuousHist";
    }

}
