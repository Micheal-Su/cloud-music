package com.tacitn.songservice.controller;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.tacitn.songservice.domain.Collect;
import com.tacitn.songservice.domain.Singer;
import com.tacitn.songservice.domain.Song;
import com.tacitn.songservice.domain.SongList;
import com.tacitn.songservice.dto.Result;
import com.tacitn.songservice.service.CollectService;
import com.tacitn.songservice.service.SingerService;
import com.tacitn.songservice.service.SongListService;
import com.tacitn.songservice.service.SongService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author DongJiShiLiu
 * @create 2022/12/8 12:45
 */
@Slf4j
@RestController
@RequestMapping("collect")
public class CollectController {
    @Autowired
    CollectService collectService;

    @Autowired
    SongService songService;

    @Autowired
    SongListService songListService;

    @Autowired
    SingerService singerService;

    @PostMapping("add")
    Result addCollect(Collect collect) {
        return collectService.addCollect(collect);

    }

//    @ApiOperation("createTime不需要传")
    @PostMapping("delete")
    Result deleteCollect(@RequestBody Collect collect) {
        boolean flag = collectService.removeById(collect.getId());
        if (flag) {
            Result.ok("取消收藏");
        }
        return Result.fail("取消收藏失败");
    }

    @GetMapping("likedTheSong")
    public Boolean collectedTheSong(Long consumerId, Integer songId) {
        QueryWrapper<Collect> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("consumer_id", consumerId)
                .eq("song_id", songId);
        return collectService.getOne(queryWrapper) != null;
    }

    @GetMapping("collectedTheSongList")
    public Boolean collectedTheSongList(Long consumerId, Integer songListId) {
        QueryWrapper<Collect> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("consumer_id", consumerId)
                .eq("song_list_id", songListId);
        return collectService.getOne(queryWrapper) != null;
    }

    //查询用户喜欢歌曲
    //直接返回Collect列表前端处理太麻烦
    @GetMapping("getSongsByConsumerId")
    public Result getSongs(Long consumerId) {
        return collectService.getLikeSongs(consumerId);
    }


    //查询用户收藏歌单
    //直接返回Collect列表前端处理太麻烦
    @GetMapping("getCollectSongList")
    public Result getCollectSongList(Long consumerId) {
        return collectService.getCollectSongList(consumerId);
    }
}
