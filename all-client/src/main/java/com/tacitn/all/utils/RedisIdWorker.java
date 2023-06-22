package com.tacitn.all.utils;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * @author DongJiShiLiu
 * @create 2023/2/24 10:34
 */
@Component
public class RedisIdWorker {
    /**
     * 开始时间戳,2023-2-24:00:00:00
     */
    private static final Long BEGIN_TIMESTAMP = 1677196800L;
    /**
     * 序列号位数
     */
    private static final int COUNT_BITS = 32;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    public long nextId(String keyPrefix) {
        //1.生成时间戳
        LocalDateTime now = LocalDateTime.now();
        long nowSecond = now.toEpochSecond(ZoneOffset.UTC);
        long timeStamp = nowSecond - BEGIN_TIMESTAMP;

        //2.生成序列号
        //2.1 获取当前日期，精确到天

        String date = now.format(DateTimeFormatter.ofPattern("yyyy:MM:dd"));
        //2.2 自增长，每次查询value都会自动增长，为避免长时间使用同一个，超过32位，在key中加入当前日期
        //.还要运算，不用包装类
        long count = stringRedisTemplate.opsForValue().increment("icr:" + keyPrefix + ":" + date);

        //3.凭借并返回,时间戳左移32位让给序列号，接着加上序列号 或者 异或运算插入
        return timeStamp << COUNT_BITS | count;
    }

}
