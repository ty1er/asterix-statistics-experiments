package org.apache.asterix.experiment.datagen;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;

public class TweetGeneratorStatisticsConfig {

    @Argument(required = false, usage = "Distribution type for the number generators [] (default = Uniform)")
    private SpreadDistributionType distributionType = SpreadDistributionType.Uniform;

    public SpreadDistributionType getDistributionType() {
        return distributionType;
    }

    @Option(name = "-s", aliases = "--skew-exponent", usage = "Starting partition number for the set of data generators (default = 1.0)")
    private double skewExponent;

    public double getSkewExponent() {
        return skewExponent;
    }
}
