package com.tacitn.all.utils;

import java.util.concurrent.TimeUnit;

/**
 * @author DongJiShiLiu
 * @create 2022/11/3 21:47
 */
public class Consts {
/**
 * RabbitMQ
 */
    public static final String QUEUE_NAME = "djsl.voucher.order"; //队列名称
    public static final String EXCHANGE_NAME = "djsl.topic"; //交换器名称
    public static final String ROUTING_KEY = "djslRTKey"; //路由键

    /**
     * 邮箱相关的交换机
     */
    public static final String EMAIL_EXCHANGE_NAME = "djsl.email.topic";
    /**
     * 邮箱相关的交换机
     */
    public static final String EMAIL_SEND_QUEUE = "email.send.queue";
    /**
     * RoutingKey
     */
    public static final String EMAIL_SEND_KEY = "email.send";
/**
 * Redis
 */

public static final String REDIS_SIGNUP_CODE_PREFIX = "signUp:code:";
    public static final String REDIS_SIGNUP_USER_PREFIX = "signUp:user:";
    public static final Long REDIS_SIGNUP_CODE_TIME = 2L;
    public static final TimeUnit REDIS_SIGNUP_CODE_TIME_UNIT = TimeUnit.MINUTES;
    public static final Long REDIS_SIGNUP_USER_TIME = 2L;
    public static final TimeUnit REDIS_SIGNUP_USER_TIME_UNIT = TimeUnit.MINUTES;
    public static final String REDIS_LOGIN_TOKEN_PREFIX = "login:token:";
    public static final Long REDIS_LOGIN_TOKEN_TIME = 1L;
    public static final TimeUnit REDIS_LOGIN_TOKEN_TIME_UNIT = TimeUnit.HOURS;
    public static final String REDIS_LOGIN_TOKEN_PW_SUFFIX = "@#$";

    public static final String REDIS_ADMIN_TOKEN_PREFIX = "admin:token:";
    public static final Long REDIS_ADMIN_TOKEN_TIME = 1L;
    public static final TimeUnit REDIS_ADMIN_TOKEN_TIME_UNIT = TimeUnit.HOURS;
    public static final String REDIS_ADMIN_TOKEN_PW_SUFFIX = "@$%";

    public static final String REDIS_ALL_SONG_PREFIX = "cache:song:all";
    public static final Long REDIS_ALL_SONG_TIME = 1L;
    public static final TimeUnit REDIS_ALL_SON_TIME_UNIT = TimeUnit.HOURS;

    public static final String REDIS_PUBLISHED_SONG_LIST_PREFIX = "cache:songList:published";

    public static final String REDIS_CONSUMER_PREFIX = "cache:consumer:";
    public static final Long REDIS_CONSUMER_TIME = 1L;
    public static final TimeUnit REDIS_CONSUMER_TIME_UNIT = TimeUnit.HOURS;

    public static final Long REDIS_NULL_TIME = 1L;
    public static final TimeUnit REDIS_NULL_TIME_UNIT = TimeUnit.MINUTES;
    public static final String REDIS_CONSUMER_ACCOUNT = "cache:consumer:account";

    public static final String REDIS_CONSUMER_TEMPINFO = "cache:consumer:tempInfo:";

    public static final String REDIS_CONSUMER_LOCK_PREFIX = "lock:consumer:";

    public static final Long REDIS_LOCK_TIME = 10L;
    public static final TimeUnit REDIS_LOCK_TIME_UNIT = TimeUnit.SECONDS;

    public static final String REDIS_CONSUMER_EXPIRE_PREFIX = "cache:consumer:expire:";
    public static final String SECKILL_STOCK_KEY = "seckill:stock:";
    public static final String SECKILL_ORDER_KEY = "seckill:order:";
    public static final String SECKILL_MQ_RETRY_KEY = "seckill:mq:retry:";
    public static final String FEED_KEY = "feed:";

    public static final String USER_SIGN_KEY = "sign:";

}
