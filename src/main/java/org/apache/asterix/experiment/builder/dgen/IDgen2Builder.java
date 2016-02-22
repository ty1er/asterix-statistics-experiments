package org.apache.asterix.experiment.builder.dgen;

public interface IDgen2Builder extends IDgenBuilder {

    @Override
    default String getDgenConfig() {
        return "2.dgen";
    }
}
