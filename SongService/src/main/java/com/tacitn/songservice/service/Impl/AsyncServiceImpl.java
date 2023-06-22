package com.tacitn.songservice.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tacitn.songservice.domain.Song;
import com.tacitn.songservice.domain.vo.SongDoc;
import com.tacitn.songservice.service.AsyncService;
import com.tacitn.songservice.utils.EsClientPool;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Map;

import static com.tacitn.songservice.utils.Const.*;


@Slf4j
@Service
public class AsyncServiceImpl implements AsyncService {
    @Autowired
    StringRedisTemplate stringRedisTemplate;

//    @Autowired
//    private RestHighLevelClient client;

    @Autowired
    private ObjectMapper objectMapper;

    @Resource
    private EsClientPool esClientPool;

    private static final Logger logger = LoggerFactory.getLogger(AsyncServiceImpl.class);

    @Override
    @Async("asyncServiceExecutor")
    public void cacheAllPublished(String key, String value) {
        logger.info("start cacheAllPublished");
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(value), REDIS_ALL_SONG_TIME, REDIS_ALL_SON_TIME_UNIT);
    }

    @Override
    @Async("asyncServiceExecutor")
    @Transactional
    public void deleteCacheAllPublished() {
        logger.info("start deleteCacheAllPublished");
        stringRedisTemplate.delete(REDIS_PUBLISHED_SONG_LIST_PREFIX);
        logger.info("end deleteCacheAllPublished");
    }

    @Override
    @Async("asyncServiceExecutor")
    public void addSongToES(Song song) {
        logger.info("start executeAsync");
        RestHighLevelClient client = null;
        try {
            SongDoc songDoc = new SongDoc(song);
            IndexRequest request = new IndexRequest(ES_Index).id(song.getId().toString());
            request.source(objectMapper.writeValueAsString(songDoc), XContentType.JSON);
            client = esClientPool.getClient();
            client.index(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (client != null) {
                esClientPool.returnClient(client);
            }
        }
        logger.info("end executeAsync");
    }

    @Override
    @Async("asyncServiceExecutor")
    public void updateSongToES(Song song) {
        logger.info("start updateSongToES");
        UpdateRequest request = new UpdateRequest(ES_Index, String.valueOf(song.getId()));
        SongDoc songDoc = new SongDoc(song);
        RestHighLevelClient client = null;
        try {
            Map<String, Object> map = BeanUtil.beanToMap(songDoc);
            // 将为空的值去除，以免空值覆盖原有的值
            Map<String, Object> objectMap = MapUtil.removeNullValue(map);
            request.doc(objectMap);
            client = esClientPool.getClient();
            client.update(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (client != null) {
                esClientPool.returnClient(client);
            }
        }
        logger.info("end updateSongToES");
    }

    @Override
    @Async("asyncServiceExecutor")
    public void deleteSongFromES(Long[] ids) {
        logger.info("start deleteSongFromES");
        BulkRequest bulkRequest = new BulkRequest();
        for (Long id : ids) {
            DeleteRequest request = new DeleteRequest(ES_Index, id.toString());
            bulkRequest.add(request);
        }
        RestHighLevelClient client = null;
        try {
            client = esClientPool.getClient();
            client.bulk(bulkRequest, RequestOptions.DEFAULT);
            System.out.println("delete Ok");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (client != null) {
                esClientPool.returnClient(client);
            }
        }
        logger.info("end deleteSongFromES");

    }

}
