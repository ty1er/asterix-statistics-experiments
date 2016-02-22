package org.apache.asterix.experiment.builder.config;

public interface INoStatsConfigBuilder extends IAsterixConfigBuilder {

    @Override
    default String getAsterixConfig() {
        return "asterix-configuration.xml";
    }
}
