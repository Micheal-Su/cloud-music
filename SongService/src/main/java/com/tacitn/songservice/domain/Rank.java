package com.tacitn.songservice.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Rank {

    @TableId(type = IdType.AUTO)
    private Long id;

    //歌单Id
    private Long songListId;

    //评分用户的Id
    private Long consumerId;

    //用户给的分值
    private Integer score;

}