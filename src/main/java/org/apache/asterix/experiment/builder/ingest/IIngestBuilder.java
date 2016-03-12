package org.apache.asterix.experiment.builder.ingest;

public interface IIngestBuilder {

    public static String INGEST_SUBSTITUTE_MARKER = "INGEST_PORT";

    default String getIngestConfig() {
        return null;
    }
}
