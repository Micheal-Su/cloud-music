package com.tacitn.songservice.utils;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class EsClientPool {

    @Resource
    private GenericObjectPool<RestHighLevelClient> genericObjectPool;

    /**
     * 获得对象
     * @return
     * @throws Exception
     */
    public RestHighLevelClient getClient() throws Exception {
        // 从池中取一个对象
        return genericObjectPool.borrowObject();
    }

    /**
     * 归还对象
     * @param client
     */
    public void returnClient(RestHighLevelClient client) {
        // 使用完毕之后，归还对象
        genericObjectPool.returnObject(client);
    }

}

