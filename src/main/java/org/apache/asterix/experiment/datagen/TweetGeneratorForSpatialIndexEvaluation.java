/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.asterix.experiment.datagen;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.asterix.experiment.datagen.DataGeneratorForSpatialIndexEvaluation.InitializationInfo;
import org.apache.asterix.experiment.datagen.DataGeneratorForSpatialIndexEvaluation.TweetMessage;
import org.apache.asterix.experiment.datagen.DataGeneratorForSpatialIndexEvaluation.TweetMessageIterator;

public class TweetGeneratorForSpatialIndexEvaluation {

    private static Logger LOGGER = Logger.getLogger(TweetGeneratorForSpatialIndexEvaluation.class.getName());

    public static final String KEY_DURATION = "duration";
    public static final String KEY_TPS = "tps";
    public static final String KEY_GUID_SEED = "guid-seed";
    public static final String KEY_GUID_INCREMENT = "guid-increment";
    public static final String KEY_OPENSTREETMAP_FILEPATH = "open-street-map-filepath";
    public static final String KEY_LOCATION_SAMPLE_INTERVAL = "location-sample-interval";

    public static final String OUTPUT_FORMAT = "output-format";
    public static final String OUTPUT_FORMAT_ARECORD = "arecord";
    public static final String OUTPUT_FORMAT_ADM_STRING = "adm-string";

    private static final int DEFAULT_DURATION = 60; //seconds
    private static final int DEFAULT_GUID_SEED = 0;
    private static final int DEFAULT_SAMPLE_INTERVAL = 1;

    private int duration;
    private TweetMessageIterator tweetIterator = null;
    private int partition;
    private int tweetCount = 0;
    private int frameTweetCount = 0;
    private int numFlushedTweets = 0;
    private OutputStream os;
    private DataGeneratorForSpatialIndexEvaluation dataGenerator = null;
    private ByteBuffer outputBuffer = ByteBuffer.allocate(32 * 1024);
    private String openStreetMapFilePath;
    private int locationSampleInterval;

    public int getTweetCount() {
        return tweetCount;
    }

    public TweetGeneratorForSpatialIndexEvaluation(Map<String, String> configuration, int partition, String format,
            OutputStream os) throws Exception {
        this.partition = partition;
        String value = configuration.get(KEY_DURATION);
        this.duration = value != null ? Integer.parseInt(value) : DEFAULT_DURATION;
        openStreetMapFilePath = configuration.get(KEY_OPENSTREETMAP_FILEPATH);
        String lsi = configuration.get(KEY_LOCATION_SAMPLE_INTERVAL);
        this.locationSampleInterval = lsi != null ? Integer.parseInt(lsi) : DEFAULT_SAMPLE_INTERVAL;
        int idSeed = configuration.get(KEY_GUID_SEED) != null ? Integer.parseInt(configuration.get(KEY_GUID_SEED))
                : DEFAULT_GUID_SEED;
        int idIncrement = configuration.get(KEY_GUID_INCREMENT) != null
                ? Integer.parseInt(configuration.get(KEY_GUID_INCREMENT)) : 1;
        dataGenerator = new DataGeneratorForSpatialIndexEvaluation(new InitializationInfo(), openStreetMapFilePath,
                locationSampleInterval);
        tweetIterator = dataGenerator.new TweetMessageIterator(duration, idSeed, idIncrement);
        this.os = os;
    }

    private void writeTweetString(TweetMessage tweetMessage) throws IOException {
        String tweet = tweetMessage.toString() + "\n";
        tweetCount++;
        byte[] b = tweet.getBytes();
        if (outputBuffer.position() + b.length > outputBuffer.limit()) {
            flush();
            numFlushedTweets += frameTweetCount;
            frameTweetCount = 0;
            outputBuffer.put(b);
        } else {
            outputBuffer.put(b);
        }
        frameTweetCount++;
    }

    public int getNumFlushedTweets() {
        return numFlushedTweets;
    }

    private void flush() throws IOException {
        outputBuffer.flip();
        os.write(outputBuffer.array(), 0, outputBuffer.limit());
        outputBuffer.position(0);
        outputBuffer.limit(32 * 1024);
    }

    public boolean setNextRecordBatch(int numTweetsInBatch) throws Exception {
        boolean moreData = tweetIterator.hasNext();
        if (!moreData) {
            if (outputBuffer.position() > 0) {
                flush();
                numFlushedTweets += frameTweetCount;
                frameTweetCount = 0;
            }
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.info("Reached end of batch. Tweet Count: [" + partition + "]" + tweetCount);
            }
            return false;
        } else {
            int count = 0;
            while (count < numTweetsInBatch) {
                writeTweetString(tweetIterator.next());
                count++;
            }
            return true;
        }
    }

    public void resetDurationAndFlushedTweetCount(int duration) {
        tweetIterator.resetDuration(duration);
        numFlushedTweets = 0;
        tweetCount = 0;

    }
}