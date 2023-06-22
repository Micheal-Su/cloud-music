package com.tacitn.all.domain.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author DongJiShiLiu
 * @create 2022/12/11 17:27
 */
@Data
@NoArgsConstructor
public class SignUpCon {
    @TableId(type = IdType.AUTO)
    private Integer id;
    // 注册验证码
    private String code;
    private String username;
    private String phoneNum;
    private String email;
    private String password;
    private Byte sex;
    // 用户ip地址
    private String ip;
    // 是否为重发的消息
    private Boolean resend;
}
