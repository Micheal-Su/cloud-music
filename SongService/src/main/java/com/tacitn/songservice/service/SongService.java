package com.tacitn.songservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tacitn.songservice.domain.Song;
import com.tacitn.songservice.domain.vo.ListSongParams;
import com.tacitn.songservice.domain.vo.SongDoc;
import com.tacitn.songservice.domain.vo.SearchSongParams;
import com.tacitn.songservice.dto.Result;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface SongService extends IService<Song> {
    List<Song> selectAll();

    Result search(SearchSongParams params);

    Result getAggregation(SearchSongParams params);

    List<String> getSuggest(String prefix);

    boolean deleteSelectedSongs(Long[] ids);

    boolean addAllSongToES();

    SongDoc getSongOfId(String id);

    Result consumerAdd(Song song, MultipartFile songPic, MultipartFile music);

    Result addSong(Song song, MultipartFile songPic, MultipartFile music);

    Result songBySingerId(ListSongParams params);
}
