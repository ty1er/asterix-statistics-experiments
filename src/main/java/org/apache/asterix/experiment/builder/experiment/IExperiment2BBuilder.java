package org.apache.asterix.experiment.builder.experiment;

public interface IExperiment2BBuilder extends IExperimentBuilder {

    @Override
    default String getExperimentDDL() {
        return "2_b.aql";
    }
}
