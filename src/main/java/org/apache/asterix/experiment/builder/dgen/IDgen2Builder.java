package org.apache.asterix.experiment.builder.dgen;

public interface IDgen2Builder extends IDgenBuilder {

    @Override
    default String getDgenProducers() {
        return "2_producer.dgen";
    }
}
