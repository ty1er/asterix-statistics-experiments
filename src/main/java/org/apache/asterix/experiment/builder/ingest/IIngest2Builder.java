package org.apache.asterix.experiment.builder.ingest;

public interface IIngest2Builder extends IIngestBuilder {

    @Override
    default int getIngestFeedsNumber() {
        return 2;
    }

}
