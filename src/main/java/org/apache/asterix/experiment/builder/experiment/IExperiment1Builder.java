package org.apache.asterix.experiment.builder.experiment;

public interface IExperiment1Builder extends IExperimentBuilder {

    @Override
    default String getExperimentDDL() {
        return "1.aql";
    }
}
