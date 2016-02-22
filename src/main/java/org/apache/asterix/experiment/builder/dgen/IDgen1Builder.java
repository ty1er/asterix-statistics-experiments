package org.apache.asterix.experiment.builder.dgen;

public interface IDgen1Builder extends IDgenBuilder {

    @Override
    default String getDgenConfig() {
        return "1.dgen";
    }
}
