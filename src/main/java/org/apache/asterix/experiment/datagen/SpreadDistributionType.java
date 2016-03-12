package org.apache.asterix.experiment.datagen;

import java.util.Random;

import org.apache.asterix.experiment.datagen.ZipfGenerator.ZipfSpreadCuspMaxGenerator;
import org.apache.asterix.experiment.datagen.ZipfGenerator.ZipfSpreadCuspMinGenerator;
import org.apache.asterix.experiment.datagen.ZipfGenerator.ZipfSpreadDecreasingGenerator;
import org.apache.asterix.experiment.datagen.ZipfGenerator.ZipfSpreadRegularGenerator;

public enum SpreadDistributionType {
    Uniform,
    Regular,
    Decreasing,
    Cusp_Min,
    Cusp_Max;

    public static NumberGenerator getGenerator(SpreadDistributionType distributionType, final int maxValue,
            double alpha) {
        switch (distributionType) {
            case Uniform:
                return new NumberGenerator() {
                    private Random rand = new Random();

                    @Override
                    public int getNextNumber() {
                        return rand.nextInt(maxValue);
                    }

                };
            case Regular:
                return new ZipfSpreadRegularGenerator(maxValue, alpha);
            case Decreasing:
                return new ZipfSpreadDecreasingGenerator(maxValue, alpha);
            case Cusp_Min:
                return new ZipfSpreadCuspMinGenerator(maxValue, alpha);
            case Cusp_Max:
                return new ZipfSpreadCuspMaxGenerator(maxValue, alpha);
            default:
                throw new IllegalArgumentException("Unknown distribution type " + distributionType);
        }
    }
}