package com.tacitn.songservice.controller;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.tacitn.songservice.domain.Rank;
import com.tacitn.songservice.dto.Result;
import com.tacitn.songservice.service.RankService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@Slf4j
@RestController
@RequestMapping("/rank")
public class RankController {
    @Autowired
    private RankService rankService;

    //添加或修改评分
    @PostMapping("/mark")
    Result mark(Rank rank) {
        UpdateWrapper<Rank> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("score", rank.getScore());
        updateWrapper.eq("song_list_id", rank.getSongListId())
                .eq("consumer_id", rank.getConsumerId());
        boolean flag = rankService.saveOrUpdate(rank, updateWrapper);
        if (flag) {
            return Result.ok("评分成功");
        }
        return Result.fail("评分失败");

    }

    //获取我的评分
    @GetMapping("getMyRank")
    Result getMyRank(Integer songListId, Long consumerId) {
        QueryWrapper<Rank> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("song_list_id", songListId).eq("consumer_id", consumerId);
        Rank rank = rankService.getOne(queryWrapper);
        if (rank != null) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.set("score", rank.getScore());
            return Result.ok(jsonObject);
        }
        return Result.warning("暂无评分");
    }

    //获取歌单分值
    @GetMapping("getAvgScore")
//    @TargetDataSource(value = DataSourceType.MYSQL_DATASOURCE2)
    Double getAvgScore(Integer songListId) {
        Double score = rankService.getScoreSum(songListId);
        if (score == null) {
            score = 0.0;
        }
        BigDecimal decimal = new BigDecimal(score);
        return decimal.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    //评分人数
    @GetMapping("getRankNum")
    Integer getRankNum(String songListId) {
        QueryWrapper<Rank> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("songListId", Integer.parseInt(songListId));
        return rankService.count(queryWrapper);
    }
}
