package org.apache.asterix.experiment.builder.ingest;

public interface IIngest4Builder extends IIngestBuilder {

    @Override
    default int getIngestFeedsNumber() {
        return 4;
    }
}
