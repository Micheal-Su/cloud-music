package com.tacitn.songservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tacitn.songservice.domain.Singer;
import com.tacitn.songservice.dto.Result;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface SingerService extends IService<Singer> {
    boolean updateSongData(Singer singer);

    boolean deleteSinger(Long id);

    boolean deleteSelectedSingers(Long[] ids);

    Result updateSingerPic(MultipartFile avatarFile, Long id);

    List<Singer> getAll();
}
