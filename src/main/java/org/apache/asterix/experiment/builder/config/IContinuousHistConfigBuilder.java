package org.apache.asterix.experiment.builder.config;

public interface IContinuousHistConfigBuilder extends IAsterixConfigBuilder {

    @Override
    default String getAsterixConfig() {
        return "continuous-hist-configuration.xml";
    }
}
