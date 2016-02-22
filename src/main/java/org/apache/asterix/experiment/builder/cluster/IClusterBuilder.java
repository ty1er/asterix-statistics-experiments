package org.apache.asterix.experiment.builder.cluster;

public interface IClusterBuilder {

    default String getClusterConfig() {
        return null;
    }

}