package org.apache.asterix.experiment.builder.ingest;

public interface IIngest2Builder extends IIngestBuilder {

    @Override
    default String getIngestConfig() {
        return "2_ingest.aql";
    }

}
