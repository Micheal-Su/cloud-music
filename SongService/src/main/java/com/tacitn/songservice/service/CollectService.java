package com.tacitn.songservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tacitn.songservice.domain.Collect;
import com.tacitn.songservice.dto.Result;

/**
 * @author DongJiShiLiu
 * @create 2022/12/8 12:44
 */

public interface CollectService extends IService<Collect> {
    Result addCollect(Collect collect);

    Result getLikeSongs(Long consumerId);

    Result getCollectSongList(Long consumerId);
}
