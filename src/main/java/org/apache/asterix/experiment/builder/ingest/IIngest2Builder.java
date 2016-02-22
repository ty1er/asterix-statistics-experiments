package org.apache.asterix.experiment.builder.ingest;

public interface IIngest2Builder extends IIngestBuilder {

    @Override
    default String getIngestConfig() {
        return "base_2_ingest.aql";
    }
}
