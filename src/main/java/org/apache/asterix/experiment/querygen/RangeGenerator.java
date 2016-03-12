package org.apache.asterix.experiment.querygen;

import java.util.Random;

import org.apache.commons.lang3.tuple.Pair;

public abstract class RangeGenerator {

    protected Random randGen;
    protected int upperBound;
    protected int length;

    public abstract Pair<Integer, Integer> getNextRange();

    public RangeGenerator(int upperBound, int length) {
        randGen = new Random();
        this.upperBound = upperBound;
        this.length = length;
    }

    public static RangeGenerator getRangeGenerator(StatisticsQueryType queryType, int upperBound, int length) {
        switch (queryType) {
            case Random:
                return new RandomRangeGenerator(upperBound, length);
            case Fixed_length:
                return new FixedLengthRangeGenerator(upperBound, length);
            case Half_open:
                return new HalfOpenRangeGenerator(upperBound, length);
            case Point:
                return new PointGenerator(upperBound, length);
            default:
                throw new IllegalArgumentException("Cannot create query generator of the type " + queryType);
        }
    }

    static class RandomRangeGenerator extends RangeGenerator {

        public RandomRangeGenerator(int upperBound, int length) {
            super(upperBound, length);
        }

        @Override
        public Pair<Integer, Integer> getNextRange() {
            int start = randGen.nextInt(upperBound);
            int end = randGen.nextInt(upperBound - start);
            return Pair.<Integer, Integer> of(start, end);
        }

    }

    static class FixedLengthRangeGenerator extends RangeGenerator {

        public FixedLengthRangeGenerator(int upperBound, int length) {
            super(upperBound, length);
        }

        @Override
        public Pair<Integer, Integer> getNextRange() {
            int start = randGen.nextInt(upperBound);
            return Pair.<Integer, Integer> of(start, start + length);
        }

    }

    static class HalfOpenRangeGenerator extends RangeGenerator {

        public HalfOpenRangeGenerator(int upperBound, int length) {
            super(upperBound, length);
        }

        @Override
        public Pair<Integer, Integer> getNextRange() {
            int start = randGen.nextInt(upperBound);
            return Pair.<Integer, Integer> of(start, upperBound);
        }

    }

    static class PointGenerator extends RangeGenerator {

        public PointGenerator(int upperBound, int length) {
            super(upperBound, length);
        }

        @Override
        public Pair<Integer, Integer> getNextRange() {
            int start = randGen.nextInt(upperBound);
            return Pair.<Integer, Integer> of(start, start);
        }

    }
}
