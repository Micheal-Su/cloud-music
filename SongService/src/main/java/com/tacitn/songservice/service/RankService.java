package com.tacitn.songservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tacitn.songservice.domain.Rank;

public interface RankService extends IService<Rank> {

    Double getScoreSum(Integer songListId);
}
