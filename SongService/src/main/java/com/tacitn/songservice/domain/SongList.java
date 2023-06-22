package com.tacitn.songservice.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SongList {
    @TableId(type = IdType.AUTO)//声明数据库中为自增的，这样将实体类添加进数据库表时就不需要setId()了
    private Long id;

    private String title;

    private String pic;

    private String style;

    private String introduction;

    private Integer songNum;

    private Long creatorId;

    private Date createTime;

//  是否发布公开
    private Boolean published;

 }
