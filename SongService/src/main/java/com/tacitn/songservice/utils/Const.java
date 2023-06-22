package com.tacitn.songservice.utils;

import java.util.concurrent.TimeUnit;

/**
 * @author DongJiShiLiu
 * @create 2023/4/29 0:31
 */
public class Const {
    /**
     * Song相关的交换机
     */
    public static final String SONG_EXCHANGE_NAME = "djsl.song.topic"; //交换器名称
    /**
     * 新增和修改相关的队列
     */
    public static final String SONG_INSERT_QUEUE = "song.insert.queue";
    /**
     * 删除相关的队列
     */
    public static final String SONG_DELETE_QUEUE = "song.delete.queue";
    /**
     * 新增和修改相关的RoutingKey
     */
    public static final String SONG_INSERT_KEY = "song.insert";
    /**
     * 删除相关的RoutingKey
     */
    public static final String SONG_DELETE_KEY = "song.delete";

    public static final String ES_Index = "song";
    public static final String ES_SONG_AGG_FIELDS[] = {"singerId","uploaderId"};
    public static final Integer ES_SONG_AGG_TABS_SIZE = 10;

    public static final String REDIS_PUBLISHED_SONG_LIST_PREFIX = "cache:songList:published";
    public static final Long REDIS_ALL_SONG_TIME = 1L;
    public static final TimeUnit REDIS_ALL_SON_TIME_UNIT = TimeUnit.HOURS;


}
