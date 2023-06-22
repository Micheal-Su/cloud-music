package com.tacitn.songservice.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.tacitn.songservice.domain.Singer;
import com.tacitn.songservice.domain.Song;
import com.tacitn.songservice.domain.SongList;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 进程缓存
 */
@Configuration
public class CaffeineConfig {

    /**
     * 歌手列表缓存
     * @return
     */
    @Bean
    public Cache<String, List<Singer>> singerListCache(){
        return Caffeine.newBuilder()
                .initialCapacity(5)
                .maximumSize(100)
                .build();
    }

    /**
     * 某歌手的所有歌曲列表
     * @return
     */
    @Bean
    public Cache<Long, List<Song>> songOfSingerCache(){
        return Caffeine.newBuilder()
                .initialCapacity(20)
                .maximumSize(200)
                .build();
    }

    /**
     * 用户自己创建的歌单
     * @return
     */
    @Bean
    public Cache<Long, List<SongList>> songListOfConsumerCache(){
        return Caffeine.newBuilder()
                .initialCapacity(20)
                .maximumSize(1000)
                .build();
    }

}
