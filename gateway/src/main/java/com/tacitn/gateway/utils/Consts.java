package com.tacitn.gateway.utils;

import java.util.concurrent.TimeUnit;

/**
 * @author DongJiShiLiu
 * @create 2022/11/3 21:47
 */
public class Consts {
/**
 * Redis
 */
    // 登录名
    public static final String REDIS_LOGIN_TOKEN_PREFIX = "login:token:";
    public static final String REDIS_LOGIN_TOKEN_PW_SUFFIX = "@#$";
    public static final String REDIS_ADMIN_TOKEN_PREFIX = "admin:token:";
    public static final String REDIS_ADMIN_TOKEN_PW_SUFFIX = "@$%";

    public static final Long REDIS_LOGIN_TOKEN_TIME = 1L;
    public static final TimeUnit REDIS_LOGIN_TOKEN_TIME_UNIT = TimeUnit.HOURS;
    public static final Long REDIS_ADMIN_TOKEN_TIME = 1L;
    public static final TimeUnit REDIS_ADMIN_TOKEN_TIME_UNIT = TimeUnit.HOURS;

}
