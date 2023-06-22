package com.tacitn.songservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tacitn.songservice.domain.Song;
import com.tacitn.songservice.domain.vo.SongDoc;
import com.tacitn.songservice.service.SongService;
import com.tacitn.songservice.utils.EsClientPool;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


import static com.tacitn.songservice.utils.Const.ES_Index;

/**
 * @author DongJiShiLiu
 * @create 2023/4/30 23:32
 */
@RestController
@RequestMapping("/toES")
public class ToESController{
    @Autowired
    private SongService songService;

    @Resource
    private EsClientPool esClientPool;

    @Autowired
    private ObjectMapper objectMapper;

    // 一定要加上{id}和@PathVariable("id"),直接传参数过来是接收不到的
    @PostMapping("/insertOrUpdateSongToES/{id}")
    public void insertOrUpdateSongToES(@PathVariable("id") Long id) {
        Song song = songService.getById(id);
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
    }

}
