package com.tacitn.songservice.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tacitn.songservice.domain.Singer;
import com.tacitn.songservice.domain.Song;
import com.tacitn.songservice.dto.Result;
import com.tacitn.songservice.service.SingerService;
import com.tacitn.songservice.service.SongService;
import com.tacitn.songservice.utils.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author DongJiShiLiu
 * @create 2022/11/5 19:05
 */
@Slf4j
@RestController
@RequestMapping("/singer")
public class SingerContoller {
    @Autowired
    SingerService singerService;

    @Autowired
    SongService songService;

    @PostMapping("/add")
    public Result addSinger(@RequestBody Singer singer) {//@RequestBody接收Json格式的数据
        singer.setPic(FileUtil.getSingerPicInProject());
        if ("".equals(singer.getLocation())){
            singer.setLocation("未知");
        }
        boolean flag = singerService.save(singer);

        if (flag) {
            return Result.ok();
        }
        return Result.fail("添加失败");

    }


    @PostMapping("/update")
    @Transactional
    public Result updateSinger(@RequestBody Singer singer) {
        if ("".equals(singer.getLocation())){
            singer.setLocation("未知");//数据库null默认值为“未知”：
        }
        boolean flag = singerService.saveOrUpdate(singer);//看源码可知通过主键判断是添加还是更新
        if (flag) {
            return Result.ok();
        }
        return Result.fail("修改失败");
    }

    @GetMapping("/delete")
    @Transactional
    public boolean deleteSinger(Long id) {
        return singerService.deleteSinger(id);

    }

    @GetMapping("/deleteSelectedSingers")
    @Transactional
    public boolean deleteSelectedSingers(Long[] ids) {
        return singerService.deleteSelectedSingers(ids);

    }

    @GetMapping("/selectByPrimaryKey")
    public Singer selectByPrimaryKey(Integer id) {

        return singerService.getById(id);
    }

    @GetMapping("/selectAll")
    public List<Singer> selectAll() {
        return singerService.getAll();
    }

    @GetMapping("/getSingerNameById")
    public String getSingerNameById(Long id) {
        Singer singer = singerService.getById(id);
        return singer.getName();
    }


    @GetMapping("/getRecmdSong")
    public String getRecmdSong(Integer singerId) {
        Singer singer = selectByPrimaryKey(singerId);
        Song song = songService.getById(singer.getRecmdSongId());
        if (song!=null){
            return song.getSongName();
        }
        return "";

    }


    @GetMapping("/selectOfName")
    public List<Singer> selectOfName(String name) {
        QueryWrapper<Singer> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("name", "%" + name + "%");
        return singerService.list(queryWrapper);
    }

    @GetMapping("/getSingersBySex")
    public List<Singer> selectBySex(String sex) {//传过来的都是String
        QueryWrapper<Singer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("sex", Integer.parseInt(sex));
        return singerService.list(queryWrapper);
    }

    @PostMapping(value = "/updateSingerPic")
    public Result updateSingerPic(@RequestParam("file") MultipartFile avatarFile, @RequestParam("id") Long id) {
        return singerService.updateSingerPic(avatarFile,id);
    }
}
