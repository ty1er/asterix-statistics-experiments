package org.apache.asterix.experiment.builder.ingest;

public interface IIngestBuilder {

    default String getIngestConfig() {
        return null;
    }
}
