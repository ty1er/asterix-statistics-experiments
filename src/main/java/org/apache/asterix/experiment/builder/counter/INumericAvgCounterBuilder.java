package org.apache.asterix.experiment.builder.counter;

public interface INumericAvgCounterBuilder extends ICounterBuilder {

    @Override
    default String getCounter() {
        return "count.aql";
    }
}
