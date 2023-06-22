package com.tacitn.songservice.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.tacitn.songservice.domain.*;
import com.tacitn.songservice.domain.vo.ListSongParams;
import com.tacitn.songservice.domain.vo.SearchSongParams;
import com.tacitn.songservice.domain.vo.SongDoc;
import com.tacitn.songservice.dto.Result;
import com.tacitn.songservice.service.*;
import com.tacitn.songservice.service.Impl.EsClientService;
import com.tacitn.songservice.utils.ESUtil;
//import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import static com.tacitn.songservice.utils.Const.*;

@Slf4j
@RestController
@RequestMapping("/song")
public class SongContoller {
    @Autowired
    private SongService songService;

    @Autowired
    private EsClientService esClientService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 音乐搜索自动补全
     * @param prefix
     * @return
     *
     * 限流规则：排队等待,QPS20,超时时间2秒，也就是超阈值的前40个可以被接收，
     * 如果一直超阈值发送，超过阈值的迟早会多于40个，多的就会被拒绝
     */
    @GetMapping("/getSuggest")
    public List<String> getSuggest(String prefix) {
        if (StrUtil.isBlank(prefix)){
            return null;
        }
        return songService.getSuggest(prefix);
    }

    /**
     * 聚合，根据聚合条件分类，获取的结果可作为前端的 标签栏供用户选择
     * @param params
     * @return
     */
    @GetMapping("/getAggregation")
    public Result getAggregation(SearchSongParams params) {
        return songService.getAggregation(params);
    }

    /**
     * matchAll，支持分页和按用户要求排序
     * @param param
     * @return
     */
    // warmUp,阈值QPS12，预热时长5
    @GetMapping("/matchPageAndSort")
    public Result matchPageAndSort(SearchSongParams param) {
        return songService.search(param);
    }

    @GetMapping("/matchAllFromES")
    public Result matchAllFromES(String text) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("all", text));
        SearchResponse searchResponse = esClientService.search(searchSourceBuilder, ES_Index);
        try {
            return ESUtil.handleSongResponse(searchResponse);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return Result.fail("出错");
        }
    }

    @GetMapping("/getAllFromES")
    public Result getAllFromES() {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        SearchResponse searchResponse = esClientService.search(searchSourceBuilder, ES_Index);
        try {
            return ESUtil.handleSongResponse(searchResponse);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return Result.fail("出错");
        }
    }


    // 批量导入文档
    @GetMapping("addAll")
    public boolean addAllSongToES() {
        return songService.addAllSongToES();

    }

    @PostMapping("/delete")
    @Transactional
    public boolean deleteSelectedSongs(@RequestParam("ids") Long[] ids) {
        return songService.deleteSelectedSongs(ids);

    }

    // 更新
    @PostMapping("/update")
    public Result updateSong(@RequestBody Song song) {
        song.setUpdateTime(new Date());
        boolean flag = songService.saveOrUpdate(song);//看源码可知通过主键判断是添加还是更新
        if (flag) {
            rabbitTemplate.convertAndSend(SONG_EXCHANGE_NAME, SONG_INSERT_KEY,
                    song.getId());
            return Result.ok();
        }
        return Result.fail("更新出错");
    }

    // 根据id查询
    @GetMapping(value = "/songOfId")
    public SongDoc songOfId(String id) {
        return songService.getSongOfId(id);

    }

    @PostMapping("consumer/add")
    @Transactional
    public Result consumerAdd(Song song, @RequestParam("songPic") MultipartFile songPic, @RequestParam("file") MultipartFile music) {
        return songService.consumerAdd(song,songPic,music);
    }

    @PostMapping("/add")
//    @GlobalTransactional
    public Result addSong(Song song, @RequestParam("songPic") MultipartFile
            songPic, @RequestParam("file") MultipartFile music) {
        return songService.addSong(song,songPic,music);
    }

    //    返回指定歌手ID的歌曲
    @GetMapping(value = "/singer/detail")
    public Result songOfSingerId(ListSongParams params) {
        return songService.songBySingerId(params);
    }

    //    返回指定上传用户ID的歌曲
    @GetMapping(value = "/uploader/detail")
    public List<Song> getSongByUploaderId(Integer uploaderId) {
        QueryWrapper<Song> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uploader_id", uploaderId);
        return songService.list(queryWrapper);
    }

    @GetMapping(value = "/selectAll")
    public List<Song> getAllSongs() {
        return songService.selectAll();
    }


    @GetMapping(value = "/songOfName")
    public Song songOfName(String name) {
        QueryWrapper<Song> queryWrapper = new QueryWrapper();
        queryWrapper.eq("name", name);
        return songService.getOne(queryWrapper);
    }

    //  根据歌手名模糊查询歌曲
    @GetMapping(value = "/getSongLikeSongOrSingerName")
    public List<Song> getSongLikeSongOrSingerName(String name) {
        QueryWrapper<Song> queryWrapper = new QueryWrapper();
        queryWrapper.like("name", "%" + name + "%");
        return songService.list(queryWrapper);
    }

    //  根据歌手名模糊查询歌曲
    @GetMapping(value = "/likeSongsOfSingerName")
    public List<Song> likeSongsOfSingerName(String singerName) {
        QueryWrapper<Song> queryWrapper = new QueryWrapper();
        queryWrapper.like("name", "%" + singerName + "%");
        return songService.list(queryWrapper);
    }
}
