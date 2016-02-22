package org.apache.asterix.experiment.builder.ingest;

public interface IIngest4Builder extends IIngestBuilder {

    @Override
    default String getIngestConfig() {
        return "base_4_ingest.aql";
    }
}
