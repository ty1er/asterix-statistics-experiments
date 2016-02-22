package org.apache.asterix.experiment.builder.cluster;

public interface ICluster2Builder extends IClusterBuilder {

    /* (non-Javadoc)
     * @see org.apache.asterix.experiment.builder.INodeConfigBuilder#getConfig()
     */
    @Override
    default String getClusterConfig() {
        return "2node.xml";
    }
}
