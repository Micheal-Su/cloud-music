package com.tacitn.songservice.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.tacitn.songservice.domain.SongList;
import com.tacitn.songservice.dto.Result;
import com.tacitn.songservice.service.AsyncService;
import com.tacitn.songservice.service.CollectService;
import com.tacitn.songservice.service.ListSongService;
import com.tacitn.songservice.service.SongListService;
import com.tacitn.songservice.utils.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Date;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("/songList")
public class SongListController {
    @Autowired
    SongListService songListService;

    @Autowired
    ListSongService listSongService;

    @Autowired
    AsyncService asyncService;

    @Autowired
    CollectService collectService;

    @PostMapping("/add")
    public Result addSongList(@RequestBody SongList songList) {//@RequestBody接收Json格式的数据
        return songListService.addSongList(songList);


    }

    @PostMapping("/update")
    public Result updateSongList(@RequestBody SongList songList) {//@RequestBody接收Json格式的数据
        boolean flag = songListService.updateSongListMsg(songList);

        if (flag) {
            if (songList.getPublished()) {
                asyncService.deleteCacheAllPublished();
            }
            return Result.ok("更新成功");
        }
        return Result.fail("更新失败");

    }

    @PostMapping(value = "/updateSongListPic")
    @Transactional
    public Result updateSongListPic(@RequestParam("file") MultipartFile avatarFile, @RequestParam("id") Long id) {
        return songListService.updateSongListPic(avatarFile,id);
    }


    @PostMapping("/changePublished")
    public boolean changePublished(Integer id) {
        UpdateWrapper<SongList> updateWrapper = new UpdateWrapper<>();
        updateWrapper.setSql("published = !published").eq("id", id);
        asyncService.deleteCacheAllPublished();
        return songListService.update(updateWrapper);
    }


    @PostMapping("/delete")
    public boolean deleteSongList(@RequestParam("ids") Long[] ids) {
        return songListService.deleteSongList(ids);
    }

    @GetMapping("/selectAll")
    public List<SongList> allSongList() {
        return songListService.list();
    }

    @GetMapping("/selectPublished")
    public List<SongList> Published() {
        return songListService.selectAllPublished();
    }


    @GetMapping("/getMySongList")
    public List<SongList> getSongListByCreator(Long consumerId) {
        if (consumerId > 0){
            return songListService.getSongListByCreator(consumerId);
        }
        return null;

    }

    @GetMapping("/getMyPublishedSongList")
    public List<SongList> getMyPublishedSongList(Long consumerId) {
        QueryWrapper<SongList> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("creator_id", consumerId).eq("published", 1);
        return songListService.list(queryWrapper);
    }


    //    根据标题模糊查询
    @GetMapping("/likeTitle")
    public List<SongList> likeTitle(String title) {
        QueryWrapper<SongList> queryWrapper = new QueryWrapper();
        queryWrapper.like("title", "%" + title + "%");
        List<SongList> songListList = songListService.list(queryWrapper);
        return songListList;
    }

    //    根据风格查询
    @GetMapping("/byStyle")
    public List<SongList> byStyle(String style) {
        QueryWrapper<SongList> queryWrapper = new QueryWrapper();
        queryWrapper.eq("style", style);
        return songListService.list(queryWrapper);
    }

}
