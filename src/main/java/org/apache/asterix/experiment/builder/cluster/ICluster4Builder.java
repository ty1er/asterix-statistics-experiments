package org.apache.asterix.experiment.builder.cluster;

public interface ICluster4Builder extends IClusterBuilder {

    /* (non-Javadoc)
     * @see org.apache.asterix.experiment.builder.INodeConfigBuilder#getConfig()
     */
    @Override
    default String getClusterConfig() {
        return "4node.xml";
    }
}
