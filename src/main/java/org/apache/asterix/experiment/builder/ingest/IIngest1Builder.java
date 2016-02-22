package org.apache.asterix.experiment.builder.ingest;

public interface IIngest1Builder extends IIngestBuilder {

    @Override
    default String getIngestConfig() {
        return "base_1_ingest.aql";
    }
}
