package com.tacitn.feign.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient("songservice")
public interface SongClient {
    @PostMapping("/toES/insertOrUpdateSongToES/{id}")
    void insertOrUpdateSongToES(@PathVariable("id") Long id);

}
