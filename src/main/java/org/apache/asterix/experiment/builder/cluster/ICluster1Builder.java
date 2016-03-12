package org.apache.asterix.experiment.builder.cluster;

public interface ICluster1Builder extends IClusterBuilder {

    /* (non-Javadoc)
     * @see org.apache.asterix.experiment.builder.INodeConfigBuilder#getConfig()
     */
    @Override
    default String getClusterConfig() {
        return "1node.xml";
    }

    @Override
    default String getDgenConsumers() {
        return "1_consumer.dgen";
    }
}
