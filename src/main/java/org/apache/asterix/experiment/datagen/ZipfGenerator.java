package org.apache.asterix.experiment.datagen;

import java.util.Random;

import org.apache.commons.math3.distribution.ZipfDistribution;

public abstract class ZipfGenerator implements NumberGenerator {

    protected final ZipfDistribution distributionGen;
    protected int maxValue;

    public ZipfGenerator(int maxValue, double alpha) {
        distributionGen = new ZipfDistribution(maxValue, alpha);
        this.maxValue = maxValue;
    }

    public static class ZipfSpreadRegularGenerator extends ZipfGenerator {

        public ZipfSpreadRegularGenerator(int maxValue, double alpha) {
            super(maxValue, alpha);
        }

        @Override
        public int getNextNumber() {
            return distributionGen.sample();
        }

    }

    public static class ZipfSpreadDecreasingGenerator extends ZipfGenerator {

        public ZipfSpreadDecreasingGenerator(int maxValue, double alpha) {
            super(maxValue, alpha);
        }

        @Override
        public int getNextNumber() {
            return maxValue - distributionGen.sample();
        }

    }

    public static class ZipfSpreadCuspMinGenerator extends ZipfGenerator {

        public ZipfSpreadCuspMinGenerator(int maxValue, double alpha) {
            super(maxValue / 2, alpha);
        }

        @Override
        public int getNextNumber() {
            return maxValue - distributionGen.sample() + distributionGen.sample();
        }

    }

    public static class ZipfSpreadCuspMaxGenerator extends ZipfGenerator {

        public ZipfSpreadCuspMaxGenerator(int maxValue, double alpha) {
            super(maxValue / 2, alpha);
        }

        @Override
        public int getNextNumber() {
            //toss a coin
            if (new Random().nextInt(2) == 0) {
                return distributionGen.sample() + distributionGen.sample();
            } else {
                return 2 * maxValue - distributionGen.sample() - distributionGen.sample();
            }
        }

    }
}
