package com.tacitn.feign.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Consumer {
    private Long id;

    private String username;

    private String password;

    private Byte sex;

    private String phoneNum;

    private String email;

    private Integer level;

    private Integer exp;

    private Date birth;

    private String introduction;

    private String location;

    private String avatar;

    private LocalDateTime lastLoginTime;

//    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date createTime;
//    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date updateTime;

    }
