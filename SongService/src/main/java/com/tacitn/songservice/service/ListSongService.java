package com.tacitn.songservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tacitn.songservice.domain.ListSong;
import com.tacitn.songservice.domain.Song;
import com.tacitn.songservice.domain.vo.ListSongParams;
import com.tacitn.songservice.dto.Result;

import java.util.List;

/**
 * @author DongJiShiLiu
 * @create 2022/11/14 15:14
 */

public interface ListSongService extends IService<ListSong> {
    Result getSongs(ListSongParams params);
}
