package org.apache.asterix.experiment.builder.experiment;

public interface IExperiment2ABuilder extends IExperimentBuilder {

    @Override
    default String getExperimentDDL() {
        return "2_a.aql";
    }
}
