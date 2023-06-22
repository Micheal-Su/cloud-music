package com.tacitn.songservice.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tacitn.songservice.domain.Rank;
import com.tacitn.songservice.mapper.RankMapper;
import com.tacitn.songservice.service.RankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RankServiceImpl extends ServiceImpl<RankMapper, Rank>implements RankService {
    @Autowired
    RankMapper rankMapper;

    @Override
    public Double getScoreSum(Integer songListId) {
        return rankMapper.getScoreSum(songListId);
    }
}
