package com.yahoo.ads.pb;
import org.apache.commons.configuration.Configuration;
import com.yahoo.ads.pb.util.ConfigurationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultPartitioner implements Partitioner {
	private Configuration conf = ConfigurationManager.getConfiguration();
	private static Logger logger = LoggerFactory.getLogger(DefaultPartitioner.class);
    Partitioner customizedPartitioner = null;
    boolean checkedCustomizedPartitioner = false;

    public long getPartition(long id, long totalParition) {

        String customizedPartitionerClassName = ConfigurationManager.getConfiguration().getString("pistachio.partitioner");
        logger.debug("found customized partitioner {}",customizedPartitionerClassName);
        if (!checkedCustomizedPartitioner && customizedPartitionerClassName != null) {
            checkedCustomizedPartitioner = true;
            logger.info("found customized partitioner {}",customizedPartitionerClassName); 
            try {
                customizedPartitioner = (Partitioner) Class.forName(customizedPartitionerClassName).newInstance();
            } catch (Exception e) {
                logger.debug("error partitioning with customized partitioner, fall back to default", e);
            }
        }

        if (customizedPartitioner != null) {
           return customizedPartitioner.getPartition(id,totalParition);
        }


        long shard = id % totalParition;
        shard = shard < 0 ? shard + totalParition: shard;
        return shard;
    }
}

