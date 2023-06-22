package com.tacitn.all.domain.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author DongJiShiLiu
 * @create 2023/2/14 12:50
 */
@Data
@NoArgsConstructor
public class ConsumerDTO {
    private String email;
    private String password;
    private Byte sex;
    // 用户ip地址
    private String ip;
    // 是否为重发的消息
    private Boolean resend;
}
