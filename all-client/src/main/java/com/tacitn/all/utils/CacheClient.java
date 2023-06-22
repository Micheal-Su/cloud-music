package com.tacitn.all.utils;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.tacitn.all.domain.vo.RedisData;
import com.tacitn.all.service.AsyncService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static com.tacitn.all.utils.Consts.*;

@Slf4j
@Data
@AllArgsConstructor
@Component
public class CacheClient {

    @Autowired
    AsyncService asyncService;

    @Autowired
    private final StringRedisTemplate stringRedisTemplate;

    public void set(String key, Object value, Long time, TimeUnit unit) {
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(value), time, unit);
    }

    public void setWithLogicalExpire(String key, Object value, Long time, TimeUnit unit) {
        RedisData redisData = new RedisData();
        redisData.setData(value);
        redisData.setExpireTime(LocalDateTime.now().plusSeconds(unit.toSeconds(time)));
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(redisData));
    }

    //缓存null值。 考虑到有些查询不是通过id的，所以不通过数据条数判断是否返回null
    //R,ID 都是泛型，因为不确定，Function 是函数类型                                 //叫dbFallback是缓存查不到了再查数据库
    public <R, ID> R queryWithPassThrough(String keyPrefix, ID id, Class<R> type, Function<ID, R> dbFallback, Long time, TimeUnit unit) {
        String key = keyPrefix + id;
        String json = stringRedisTemplate.opsForValue().get(key);
        if (StrUtil.isNotBlank(json)) {
            return JSONUtil.toBean(json, type);
        }

        if ("".equals(json)) {
            return null;
        }

        R r = dbFallback.apply(id);

        if (r == null) {
            stringRedisTemplate.opsForValue().set(key, "", REDIS_NULL_TIME, REDIS_NULL_TIME_UNIT);
            return null;
        }

        this.set(key, r, time, unit);

        return r;

    }

    //    缓存击穿处理之逻辑过期，因为每一个用户信息都保存在redis中，所以不需要限制id范围防止频繁去数据库查询无效数据了
    public <R, ID> R queryWithLogicalExpire(String keyPrefix, ID id, Class<R> type, Function<ID, R> dbFallback, Long time, TimeUnit unit) {
        String key = keyPrefix + id;
        String json = stringRedisTemplate.opsForValue().get(key);

        if (StrUtil.isBlank(json)) {
            return null;
        }

        RedisData redisData = JSONUtil.toBean(json, RedisData.class);
        R r = JSONUtil.toBean(StrUtil.toString( redisData.getData()), type);
        LocalDateTime expireTime = redisData.getExpireTime();

//        未过期
        if (expireTime.isAfter(LocalDateTime.now())) {
            return r;
        }

        boolean isLock = tryLock(REDIS_CONSUMER_LOCK_PREFIX + id);
        if (isLock) {//缓存重建
            asyncService.saveExpireData2Redis(keyPrefix, id,
                    dbFallback, time,
                    unit);
        }
//        拿不到锁就返回旧数据
        return r;

    }

    private boolean tryLock(String key) {
        Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent(key, "1", REDIS_LOCK_TIME, REDIS_LOCK_TIME_UNIT);
//        不直接返回flag是拆箱过程可能出现null
        return BooleanUtil.isTrue(flag);

    }

    private void unlock(String key) {
        stringRedisTemplate.delete(key);
    }


}
