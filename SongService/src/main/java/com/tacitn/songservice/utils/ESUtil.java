package com.tacitn.songservice.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tacitn.songservice.domain.vo.SongDoc;
import com.tacitn.songservice.dto.Result;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Map;

public class ESUtil {

    public static Result handleSongResponse(SearchResponse response) throws JsonProcessingException {
        if (response == null){
            return Result.ok();
        }
        ArrayList<SongDoc> songDocList = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        SearchHits searchHits = response.getHits();
        // 4.1.总条数
        Long total = searchHits.getTotalHits().value;
        // 4.2.获取文档数组
        SearchHit[] hits = searchHits.getHits();
        // 4.3.遍历

        for (SearchHit hit : hits) {
            // 4.4.获取source
            String json = hit.getSourceAsString();
            // 4.5.反序列化，非高亮的
//            HotelDoc hotelDoc = JSON.parseObject(json, HotelDoc.class);
            SongDoc songDoc = objectMapper.readValue(json, SongDoc.class);
            // 4.6.处理高亮结果
            // 1)获取高亮map
            Map<String, HighlightField> map = hit.getHighlightFields();
            // 判断是否存在高亮查询结果
            if (!CollectionUtils.isEmpty(map)){
                // 2）根据字段名，获取高亮结果
                HighlightField highlightFieldName = map.get("songName");
                // 判断name字段是否存在匹配查询条件的数据
                if (highlightFieldName != null){
                    // 3）获取高亮结果字符串数组中的第1个元素
                    String hName = highlightFieldName.getFragments()[0].string();
                    // 4）把高亮结果放到HotelDoc中
                    songDoc.setSongName(hName);
                }
                HighlightField highlightSingerName = map.get("singerName");
                // 判断name字段是否存在匹配查询条件的数据
                if (highlightSingerName != null){
                    // 3）获取高亮结果字符串数组中的第1个元素
                    String hName = highlightSingerName.getFragments()[0].string();
                    // 4）把高亮结果放到HotelDoc中
                    songDoc.setSingerName(hName);
                }
            }
            // 4.7.打印
//            System.out.println(songDoc);
            songDocList.add(songDoc);
        }

        return Result.pageOk(songDocList, total.intValue());
    }
}
