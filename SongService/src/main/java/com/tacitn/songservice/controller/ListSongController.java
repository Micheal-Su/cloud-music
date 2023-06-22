package com.tacitn.songservice.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tacitn.songservice.domain.ListSong;
import com.tacitn.songservice.domain.Song;
import com.tacitn.songservice.domain.SongList;
import com.tacitn.songservice.domain.vo.ListSongParams;
import com.tacitn.songservice.dto.Result;
import com.tacitn.songservice.service.ListSongService;
import com.tacitn.songservice.service.SongListService;
import com.tacitn.songservice.service.SongService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author DongJiShiLiu
 * @create 2022/11/14 15:18
 */
@Slf4j
@RestController
@RequestMapping("/listSong")
public class ListSongController {
    @Autowired
    ListSongService listSongService;

    @Autowired
    SongService songService;

    @Autowired
    SongListService songListService;

    @PostMapping("/add")
    @Transactional
    public Result addListSong(ListSong listSong) {
        SongList songList = songListService.getById(listSong.getSongListId());
        songList.setSongNum(songList.getSongNum()+1);
        songListService.saveOrUpdate(songList);
        boolean flag = listSongService.save(listSong);
        if (flag) {
            return Result.ok("添加成功");
        }
        return Result.fail("添加失败");
    }


    @GetMapping("collectedTheSong")
    public Boolean collectedTheSong(Integer songListId, Integer songId) {
        QueryWrapper<ListSong> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("song_list_id", songListId)
                .eq("song_id", songId);
        return listSongService.getOne(queryWrapper) != null;
    }

    @PostMapping("/addSelectedSongsById")
    @Transactional
    public boolean addSelectedSongsById(@RequestParam("songListId") Long songListId,
                                        @RequestParam("ids") Long[] ids) {
        boolean flag = false;
        ListSong listSong = new ListSong();
        SongList songList = songListService.getById(songListId);
        for (Long songId:ids){
            listSong.setSongListId(songListId);
            listSong.setSongId(songId);
            QueryWrapper<ListSong> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("song_list_id", songListId)
                    .eq("song_id", songId);
            if (listSongService.getOne(queryWrapper) == null){
                songList.setSongNum(songList.getSongNum()+1);
                flag = listSongService.save(listSong);
            }
        }
        songListService.saveOrUpdate(songList);
       return flag;
    }

    @GetMapping(value = "/detail")
    public Result getSongs(ListSongParams params) {
        return listSongService.getSongs(params);

    }


    @PostMapping("/delete")
    @Transactional
    public boolean deleteListSong(@RequestParam("songListId") Integer songListId,
                                  @RequestParam("songId") Integer songId) {
        QueryWrapper<ListSong> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("song_id", songId)
        .eq("song_list_id", songListId);
        SongList songList = songListService.getById(songListId);
        songList.setSongNum(songList.getSongNum()-1);
        songListService.saveOrUpdate(songList);
        return listSongService.remove(queryWrapper);
    }
}
