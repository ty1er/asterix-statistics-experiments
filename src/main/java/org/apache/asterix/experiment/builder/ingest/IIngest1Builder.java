package org.apache.asterix.experiment.builder.ingest;

public interface IIngest1Builder extends IIngestBuilder {

    @Override
    default int getIngestFeedsNumber() {
        return 1;
    }
}
