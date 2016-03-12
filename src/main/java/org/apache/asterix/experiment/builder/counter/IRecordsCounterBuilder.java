package org.apache.asterix.experiment.builder.counter;

public interface IRecordsCounterBuilder extends ICounterBuilder {

    @Override
    default String getCounter() {
        return "count.aql";
    }
}
