package org.apache.asterix.experiment.builder.suite;

import org.apache.asterix.experiment.builder.AbstractLSMBaseExperimentBuilder;
import org.apache.asterix.experiment.builder.cluster.ICluster1Builder;
import org.apache.asterix.experiment.builder.config.IContinuousHistConfigBuilder;
import org.apache.asterix.experiment.builder.counter.IRecordsCounterBuilder;
import org.apache.asterix.experiment.builder.dgen.IDgen1Builder;
import org.apache.asterix.experiment.builder.experiment.IExperiment1Builder;
import org.apache.asterix.experiment.builder.ingest.IIngest1Builder;
import org.apache.asterix.experiment.client.LSMExperimentSetRunner.LSMExperimentSetRunnerConfig;

public class StatisticsExperiment1Cluster1Ingest1ContinuousHistBuilder extends AbstractLSMBaseExperimentBuilder
        implements IExperiment1Builder, IDgen1Builder, ICluster1Builder, IIngest1Builder, IRecordsCounterBuilder,
        IContinuousHistConfigBuilder {

    public StatisticsExperiment1Cluster1Ingest1ContinuousHistBuilder(LSMExperimentSetRunnerConfig config) {
        super(config);
    }

}
