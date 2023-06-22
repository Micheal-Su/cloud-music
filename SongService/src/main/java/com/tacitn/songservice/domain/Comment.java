package com.tacitn.songservice.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@NoArgsConstructor
public class Comment{
    @TableId(type = IdType.AUTO)
    private Long id;
    //用户id
    private Long consumerId;
    private Long parentId; //父级评论id
    private Long toConId;  //回复的人的id
    private String toComContent;//回复的评论对象的内容

    @TableField(exist = false)
    private Boolean showChild;  //是否展示子级评论
    @TableField(exist = false)
    private String toConName;
    @TableField(exist = false)
    private String username;//评论的用户名，给前端用而已@TableField(exist = false)
    @TableField(exist = false)
    private String avatar;//评论的用户头像，给前端用而已

    private Long songId;

    private Long songListId;
    //评论内容
    private String content;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    //歌单评论还是歌曲评论 1歌单 0歌曲
    private Integer type;

    //点赞数
    private Integer up;

    //是否被删除
    private Boolean deleted;

}