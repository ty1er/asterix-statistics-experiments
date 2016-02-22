package org.apache.asterix.experiment.builder.config;

public interface IWaveletConfigBuilder extends IAsterixConfigBuilder {

    @Override
    default String getAsterixConfig() {
        return "wavelet-configuration.xml";
    }
}
