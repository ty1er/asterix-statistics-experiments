package org.apache.asterix.experiment.builder.dgen;

public interface IDgen1Builder extends IDgenBuilder {

    @Override
    default String getDgenProducers() {
        return "1_producer.dgen";
    }
}
