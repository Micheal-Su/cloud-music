package com.tacitn.all.domain;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * 用户的各种剩余量
 */
@Data
public class ConPossession {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long consumerId;
    private Integer songListRemain;
    private Integer songRemain;
}
