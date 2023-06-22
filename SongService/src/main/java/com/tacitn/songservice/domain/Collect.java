package com.tacitn.songservice.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class Collect{
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long consumerId;

    private Long songId;
    //收藏的可能是歌曲也可能是歌单
    private Long songListId;

    private LocalDateTime createTime;
}