package com.tacitn.songservice.service.Impl;

import com.tacitn.songservice.utils.EsClientPool;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class EsClientService {

    private static final Logger logger = LoggerFactory.getLogger(EsClientService.class);

    @Resource
    private EsClientPool esClientPool;

    public SearchResponse search(SearchSourceBuilder  sourceBuilder, String index) {
        SearchRequest searchRequest = new SearchRequest(index);
        searchRequest.source(sourceBuilder);
        RestHighLevelClient client = null;
        try {
            client = esClientPool.getClient();
            return client.search(searchRequest, RequestOptions.DEFAULT);
        } catch (Exception e) {
            logger.info("ES search fail", e);
        } finally {
            if (client != null) {
                esClientPool.returnClient(client);
            }
        }
        return null;
    }


}

