package org.apache.asterix.experiment.builder.config;

public interface IUniformHistConfigBuilder extends IAsterixConfigBuilder {

    @Override
    default String getAsterixConfig() {
        return "uniform-hist-configuration.xml";
    }
}
