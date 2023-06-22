package com.tacitn.songservice.service.Impl;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.tacitn.feign.clients.AllClient;
import com.tacitn.songservice.domain.*;
import com.tacitn.songservice.domain.vo.ListSongParams;
import com.tacitn.songservice.domain.vo.SearchSongParams;
import com.tacitn.songservice.domain.vo.SongDoc;
import com.tacitn.songservice.dto.Result;
import com.tacitn.songservice.mapper.SongMapper;
import com.tacitn.songservice.service.*;
import com.tacitn.songservice.utils.ESUtil;
import com.tacitn.songservice.utils.EsClientPool;
import com.tacitn.songservice.utils.FileUtil;
//import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

import static com.tacitn.songservice.utils.Const.*;

@Service
@Slf4j
public class SongServiceImpl extends ServiceImpl<SongMapper, Song> implements SongService {

    @Autowired
    private AllClient allClient;

    @Autowired
    private SongMapper songMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private Cache<Long, List<Song>> songOfSingerCache;

    @Autowired
    private PlayTimesService playTimesService;

    @Autowired
    private SingerService singerService;

    @Autowired
    private EsClientPool esClientPool;

    @Autowired
    private CollectService collectService;

    @Autowired
    private ListSongService listSongService;

    @Autowired
    private AsyncService asyncService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    private EsClientService esClientService;

    @Override
    public List<Song> selectAll() {
        return list();
    }

    /**
     * @param prefix: 搜索前缀
     * @return 搜索时的自动补全
     */
    @Override
    public List<String> getSuggest(String prefix) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        String suggestionsName = "suggestions";
        searchSourceBuilder.suggest(new SuggestBuilder().addSuggestion(
                suggestionsName,
                SuggestBuilders.completionSuggestion("suggestion")
                        .prefix(prefix)
                        .skipDuplicates(true) // 跳过重复的
                        .size(10)
        ));
        SearchResponse response = esClientService.search(searchSourceBuilder, ES_Index);
        if (response != null) {
            ArrayList<String> texts = new ArrayList<>();
            Suggest suggest = response.getSuggest();
            CompletionSuggestion suggestion = suggest.getSuggestion(suggestionsName);
            List<CompletionSuggestion.Entry.Option> options = suggestion.getOptions();
            for (CompletionSuggestion.Entry.Option option : options) {
                String text = option.getText().toString();
                texts.add(text);
            }
            return texts;
        }

        return null;
    }

    @Override
    public boolean deleteSelectedSongs(Long[] ids) {
        QueryWrapper<Collect> clQueryWrapper = new QueryWrapper<>();
        clQueryWrapper.in("song_id", Arrays.asList(ids));
        collectService.remove(clQueryWrapper);

        QueryWrapper<ListSong> listSongQueryWrapper = new QueryWrapper<>();
        listSongQueryWrapper.in("song_id", ids);
        listSongService.remove(listSongQueryWrapper);

        QueryWrapper<Song> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", ids);
        List<Song> songList = query().in("id", ids).list();
        FileUtil.deleteSongFile(songList);
        boolean remove = removeByIds(Arrays.asList(ids));
        if (remove) {
            asyncService.deleteSongFromES(ids);
            return true;
        }
        return false;
    }

    @Override
    public boolean addAllSongToES() {
        List<Song> songList = list();
        BulkRequest request = new BulkRequest();
        RestHighLevelClient client = null;
        try {
            for (Song song : songList) {
                SongDoc songDoc = new SongDoc(song);
                request.add(new IndexRequest(ES_Index).
                        id(songDoc.getId().toString())
                        .source(objectMapper.writeValueAsString(songDoc),
                                XContentType.JSON)
                );
            }
            client = esClientPool.getClient();
            client.bulk(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (client != null) {
                esClientPool.returnClient(client);
            }
        }
        return true;
    }

    @Override
    public SongDoc getSongOfId(String id) {
        GetRequest request = new GetRequest(ES_Index, id);
        GetResponse response = null;
        RestHighLevelClient client = null;
        try {
            client = esClientPool.getClient();
            response = client.get(request, RequestOptions.DEFAULT);
            if (response.isExists()) {
                String source = response.getSourceAsString();
                SongDoc songDoc = objectMapper.readValue(source, SongDoc.class);
                return songDoc;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (client != null) {
                esClientPool.returnClient(client);
            }
        }

        Song song = getById(Integer.parseInt(id));
        SongDoc songDoc = new SongDoc(song);
        return songDoc;
    }

    @Override
    public Result consumerAdd(Song song, MultipartFile songPic, MultipartFile music) {
        //        int size = 0;
        QueryWrapper<Singer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", song.getSingerName());
        Singer one = singerService.getOne(queryWrapper);
        if (one != null) {
            song.setSingerId(one.getId());
        } else {
            return Result.warning("暂无该歌手，请先上传歌手信息");
        }
        return saveSong(song, songPic, music);

    }


    private Result saveSong(Song song, MultipartFile songPic, MultipartFile music) {
        song.setCreateTime(new Date());
        song.setUpdateTime(new Date());
        if (songPic.isEmpty()) {
            song.setPic(FileUtil.getSongPicInProject());
        } else {
            try {
                FileUtil.savePic(song, songPic);
//                size += Math.round(songPic.getSize() / 1024);
            } catch (IOException e) {

                return Result.fail("图片上传失败");
            }
        }
        if (song.getCloudUrl() == null) {
            if (music.isEmpty()) {
                return Result.warning("请上传音乐文件");
            }
            try {
                FileUtil.saveOrUpdateSongFile(song, music);
//                size += Math.round(music.getSize() / 1024);
            } catch (IOException e) {
                return Result.fail("上传音乐文件失败！");
            }
        }

        boolean res = save(song);
        PlayTimes playTimes = new PlayTimes();
        playTimes.setSongId(song.getId());
        boolean savePlayTimes = playTimesService.save(playTimes);

        boolean setSong = allClient.setSong(song.getUploaderId(), -1);
        /*
        QueryWrapper<StorageSize> sizeQueryWrapper = new QueryWrapper<>();
        sizeQueryWrapper.eq("consumer_id", song.getUploaderId());
        StorageSize storageOne = storageSizeService.getOne(sizeQueryWrapper);

        QueryWrapper<StorageSize> totalQueryWrapper = new QueryWrapper<>();
        totalQueryWrapper.eq("consumer_id", 0);
        StorageSize total = storageSizeService.getOne(totalQueryWrapper);
        if (storageOne.getSize() / 1024  > 400 || total.getSize() / 1024 / 1024 > 22){//数据库的size单位是k

            responseResult.setCode(0);
            responseResult.setMsg("存储量已达上限");
            return responseResult;
        }
        storageOne.setSize(storageOne.getSize() + size);
        total.setSize(total.getSize() + size);
        boolean update = storageSizeService.saveOrUpdate(storageOne);
        boolean update1 = storageSizeService.saveOrUpdate(total);
        if (res && save && update&& update1) {

         */


        if (res && savePlayTimes && setSong) {
            // 发送至队列，监听者接收后存至ES
            rabbitTemplate.convertAndSend(SONG_EXCHANGE_NAME, SONG_INSERT_KEY,
                    song.getId());
            // 删除该歌手的歌单的本地缓存
            songOfSingerCache.invalidate(song.getSingerId());
            return Result.ok();
        }
        return Result.fail("上传失败");
    }

    @Override
    public Result addSong(Song song, MultipartFile songPic, MultipartFile music) {
        return saveSong(song, songPic, music);
    }

    /**
     * 结合本地缓存查询某歌手的所有歌曲，存至缓存，之后再分页返回前端
     * 记得在数据改变操作时，删除本地缓存
     *
     * @param params
     * @return
     */
    @Override
    public Result songBySingerId(ListSongParams params) {
        List<Song> songList = songOfSingerCache.get(params.getSingerId(),
                key -> query().eq("singer_id", params.getSingerId()).list());
        int start = (params.getPage() - 1) * params.getSize();
        int end = start + params.getSize();
        // 截取当前页
        List<Song> pageList = ListUtil.sub(songList, start, end);
        return Result.pageOk(pageList, songList.size());
    }

    @Override
    public Result search(SearchSongParams params) {
        if (params.getSize() <= 0 || params.getPage() <= 0) {
            return null;
        }
        IPage<Song> page = new Page(params.getPage(), params.getSize());
        QueryWrapper<Song> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("song_name", "%" + params.getText() + "%")
                .or().like("singer_name", "%" + params.getText() + "%");
        page(page, queryWrapper);// 结果会赋值给page这个对象

        return Result.pageOk(page.getRecords(), page.getTotal());
    }

//    这个ES查找
    /*
        public Result search(SearchSongParams params) {
            if (params.getSize() <= 0 || params.getPage() <= 0) {
                return null;
            }
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            buildBasicQuery(params,searchSourceBuilder);

            searchSourceBuilder.from((params.getPage() - 1) * params.getSize()).size(params.getSize());
            // 没传排序字段则不排序，默认根据匹配度score值排序
            if (!StrUtil.isBlank(params.getSortBy())){
                if (params.getSortOrder() >= 0) {
                    searchSourceBuilder.sort(params.getSortBy(), SortOrder.ASC);
                } else {
                    searchSourceBuilder.sort(params.getSortBy(), SortOrder.DESC);
                }
            }else {
                // 算分控制,若根据其他条件排序则算分控制无效（ES本身性质）
                FunctionScoreQueryBuilder queryBuilder = QueryBuilders.functionScoreQuery(
                        QueryBuilders.matchQuery("all", params.getText()),// 原始查询
                        new FunctionScoreQueryBuilder.FilterFunctionBuilder[]{
                                // filter 可以有多个
                                new FunctionScoreQueryBuilder.FilterFunctionBuilder(
                                        // 过滤条件
                                        QueryBuilders.termQuery("toTop", true),
                                        // 满足者的权重
                                        ScoreFunctionBuilders.weightFactorFunction(10)
                                )
                        });
                searchSourceBuilder.query(queryBuilder);
            }

            // 高亮
            searchSourceBuilder.highlighter(new HighlightBuilder().field("name")
                    .requireFieldMatch(false));



            SearchResponse searchResponse = esClientService.search(searchSourceBuilder, ES_Index);
            try {
                Result result = ESUtil.handleSongResponse(searchResponse);
                // ES查不到就查数据库,不过结果肯定没有ES查到的多样
                if (result.getData() == null ){
                    IPage<Song> page = new Page(params.getPage(),params.getSize());
                    QueryWrapper<Song> queryWrapper = new QueryWrapper<>();
                    queryWrapper.like("song_name", "%" + params.getText() + "%")
                            .or().like("singer_name", "%" + params.getText() + "%");
                    page(page, queryWrapper);// 结果会赋值给page这个对象

                    return Result.pageOk(page.getRecords(), page.getTotal());
                }
                return result;
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return Result.fail("出错了");
            }
        }


     */
    private void buildBasicQuery(SearchSongParams params, SearchSourceBuilder searchSourceBuilder) {
        if (StrUtil.isBlank(params.getText())) {
            searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        } else {
            searchSourceBuilder.query(QueryBuilders.matchQuery("all", params.getText()));
        }
    }

    @Override//获取的结果可作为前端的标签栏供用户选择
    public Result getAggregation(SearchSongParams params) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        // 聚合前的查询条件，要和客户搜索数据时的参数一致，避免比如输入框搜了七里香，标签栏却还有其他歌手的标签可以选
        buildBasicQuery(params, searchSourceBuilder);
        // 只要聚合结果，不需要文档具体内容
        searchSourceBuilder.size(0);
        // 聚合条件
        // 遍历对每个传过来的字段进行聚合
        for (String field : ES_SONG_AGG_FIELDS) {
            searchSourceBuilder.aggregation(AggregationBuilders
                    .terms(field + "Agg")
                    .field(field)
                    .size(ES_SONG_AGG_TABS_SIZE)
            );
        }

        SearchResponse response = esClientService.search(searchSourceBuilder, ES_Index);
        // 保存最后所有标签栏
        HashMap<String, List> resultMap = new HashMap<>();

        // 解析结果
        Aggregations aggregations = response.getAggregations();
        // .1.根据聚合名称，获取聚合结果
        for (String field : ES_SONG_AGG_FIELDS) {
            // 保存每一行标签栏
            ArrayList<HashMap<String, Object>> bucketsList = new ArrayList<>();
            Terms brandAgg = aggregations.get(field + "Agg");
            // .2.获取buckets
            List<? extends Terms.Bucket> buckets = brandAgg.getBuckets();
            // .3.遍历
            for (Terms.Bucket bucket : buckets) {
                String key = bucket.getKeyAsString();
                long count = bucket.getDocCount();
                HashMap<String, Object> map = new HashMap<>();
                map.put("count", count);
                map.put("key", key);
                bucketsList.add(map);
            }
            resultMap.put(field, bucketsList);
        }

        return Result.ok(resultMap);
    }

}
