package com.tacitn.songservice.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Song {
    @TableId(type = IdType.AUTO)//声明数据库中为自增的，这样将实体类添加进数据库表时就不需要setId()了
    private Long id;

    private Long singerId;

    // 歌手名，虽然和singerId有点冲突，但是利大于弊
    private String singerName;
    // 不包含歌手的歌名
    private String songName;
    // 歌曲介绍
    private String introduction;

    // 专辑
    private String album;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date updateTime;

    private String pic;

    private String lyric;

    private String url;

    private String cloudUrl;

    private String beyondApp;

    private String musicUid;

    private Long uploaderId;

    private Long likedCount;

}
