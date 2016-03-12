package org.apache.asterix.experiment.builder.ingest;

public interface IIngest4Builder extends IIngestBuilder {

    @Override
    default String getIngestConfig() {
        return "4_ingest.aql";
    }
}
