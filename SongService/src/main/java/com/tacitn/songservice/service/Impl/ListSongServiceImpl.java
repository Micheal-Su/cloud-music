package com.tacitn.songservice.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tacitn.songservice.domain.ListSong;
import com.tacitn.songservice.domain.Song;
import com.tacitn.songservice.domain.vo.ListSongParams;
import com.tacitn.songservice.dto.Result;
import com.tacitn.songservice.mapper.ListSongMapper;
import com.tacitn.songservice.service.ListSongService;
import com.tacitn.songservice.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author DongJiShiLiu
 * @create 2022/11/14 15:15
 */
@Service
public class ListSongServiceImpl extends ServiceImpl<ListSongMapper, ListSong> implements ListSongService {

    @Autowired
    private SongService songService;

    @Override
    public Result getSongs(ListSongParams params) {
        IPage<ListSong> page = new Page(params.getPage(),params.getSize());
        QueryWrapper<ListSong> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("song_list_id", params.getSongListId());
        ArrayList<Long> songIds = new ArrayList<>();
        page(page, queryWrapper);
        for (ListSong listSong : page.getRecords()){
            songIds.add(listSong.getSongId());
        }
        if (songIds.size() == 0){
            songIds.add(0L);
        }
        return Result.pageOk(songService.listByIds(songIds), page.getTotal());
    }
}
