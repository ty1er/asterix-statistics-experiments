package org.apache.asterix.experiment.builder.experiment;

public interface IExperimentBuilder {

    default String getExperimentDDL() {
        return null;
    }
}
