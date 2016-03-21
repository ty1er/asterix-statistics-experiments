package org.apache.asterix.experiment.builder.experiment;

public interface IExperiment2CBuilder extends IExperimentBuilder {

    @Override
    default String getExperimentDDL() {
        return "2_c.aql";
    }
}
