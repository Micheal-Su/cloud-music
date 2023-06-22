package com.tacitn.all.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 管理员
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Admin{
    //@TableField 和数据库 一样，不需要命名
    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String password;

}
