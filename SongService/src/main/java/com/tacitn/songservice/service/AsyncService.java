package com.tacitn.songservice.service;

import com.tacitn.songservice.domain.Song;

public interface AsyncService {

    void cacheAllPublished(String key,String value);

    void deleteCacheAllPublished();
    // 因为只需要传个id，已用MQ实现
    void addSongToES(Song song);

    // 因为只需要传个id，已用MQ实现
    void updateSongToES(Song song);

    void deleteSongFromES(Long[] ids);

}
