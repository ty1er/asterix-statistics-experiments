package org.apache.asterix.experiment.builder.dgen;

public interface IDgen4Builder extends IDgenBuilder {

    @Override
    default String getDgenProducers() {
        return "4_producer.dgen";
    }
}
