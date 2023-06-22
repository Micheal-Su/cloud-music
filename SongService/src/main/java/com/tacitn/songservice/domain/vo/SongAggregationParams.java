package com.tacitn.songservice.domain.vo;

import lombok.Data;

import java.util.List;

/**
 * @author DongJiShiLiu
 * @create 2023/4/30 0:59
 * 歌曲聚合查询参数
 * terms: 聚合结果名
 * field: 聚合的字段
 * size: 最多获取多少个聚合结果的对象
 */
@Data
public class SongAggregationParams {
    private List fields;
    private Integer size;
}
