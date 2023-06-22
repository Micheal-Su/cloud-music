package com.tacitn.songservice.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor

/**
 * 某个用户播放某首歌的详细次数
 */
public class ConPlayTimes {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long songId;
    private Long consumerId;
    private Integer dayTimes;
    private Integer weekTimes;
    private Integer monthTimes;
    private Integer yearTimes;
    private Integer playTimes;
}