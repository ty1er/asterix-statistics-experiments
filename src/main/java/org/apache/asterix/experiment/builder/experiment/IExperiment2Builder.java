package org.apache.asterix.experiment.builder.experiment;

public interface IExperiment2Builder extends IExperimentBuilder {

    @Override
    default String getExperimentDDL() {
        return "2.aql";
    }
}
