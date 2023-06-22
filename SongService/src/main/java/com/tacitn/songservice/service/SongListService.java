package com.tacitn.songservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tacitn.songservice.domain.SongList;
import com.tacitn.songservice.dto.Result;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface SongListService extends IService<SongList> {
    List<SongList> selectAllPublished();

    Boolean updateSongListMsg(SongList songList);

    Result updateSongListPic(MultipartFile avatarFile, Long id);

    boolean deleteSongList(Long[] ids);

    Result addSongList(SongList songList);

    List<SongList> getSongListByCreator(Long consumerId);
}
