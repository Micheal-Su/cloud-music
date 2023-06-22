package com.tacitn.songservice.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PlayTimes {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long songId;
    private Long dayTimes;
    private Long weekTimes;
    private Long monthTimes;
    private Long yearTimes;
    private Long playTimes;
}
