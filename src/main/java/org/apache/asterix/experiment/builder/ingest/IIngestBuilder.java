package org.apache.asterix.experiment.builder.ingest;

public interface IIngestBuilder {

    public static String INGEST_SUBSTITUTE_MARKER = "INGEST_PORT";

    public static String INGEST_FEED_NAME = "TweetFeed";

    default int getIngestFeedsNumber() {
        return -1;
    }
}
