package com.tacitn.all.service.Impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.tacitn.all.domain.vo.RedisData;
import com.tacitn.all.service.AsyncService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static com.tacitn.all.utils.Consts.*;


@Slf4j
@Service
public class AsyncServiceImpl implements AsyncService {

    //    一定要注意，使用此类的方法的 Service 不能在此类中被引入，不然@Async会失效，全部都是调用了AsyncService方法的Service所在线程
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    private static final Logger logger = LoggerFactory.getLogger(AsyncServiceImpl.class);

    @Override
    @Async("asyncServiceExecutor")
    public void executeAsync() {
        logger.info("start executeAsync");
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("end executeAsync");
    }

    @Override
    @Async("asyncServiceExecutor")
    public void cacheAllPublished(String key, String value) {
        logger.info("start cacheAllPublished");
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(value), REDIS_ALL_SONG_TIME, REDIS_ALL_SON_TIME_UNIT);

    }

    @Override
    @Async("asyncServiceExecutor")
    @Transactional
    public void deleteCacheAllPublished() {
        logger.info("start deleteCacheAllPublished");
        stringRedisTemplate.delete(REDIS_PUBLISHED_SONG_LIST_PREFIX);
        logger.info("end deleteCacheAllPublished");
    }

    @Override
    @Async("asyncServiceExecutor")
    //R,ID 都是泛型，因为不确定，Function 是函数类型                    //叫dbFallback是缓存查不到了再查数据库
    //相比传递Object，省去了拆箱封装，省去了类型判断逻辑，也更加有安全性，不会出现类型错误。
    public <R, ID> void saveExpireData2Redis(String keyPrefix, ID id, Function<ID, R> dbFallback, Long time, TimeUnit unit) {
        // 设置用户信息逻辑过期
        try {
            R r = dbFallback.apply(id);
            RedisData redisData = new RedisData();
            redisData.setExpireTime(LocalDateTime.now().plusSeconds(unit.toSeconds(time)));
            redisData.setData(r);
            // 使用泛型就没办法存进安全类型了，只能在从redis获取之后，再转成安全类型再返回给客户了
            stringRedisTemplate.opsForValue().set(keyPrefix + id, JSONUtil.toJsonStr(redisData));
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
//        释放锁
            stringRedisTemplate.delete(REDIS_CONSUMER_LOCK_PREFIX + id);
        }
    }


    @Override
    @Async("asyncServiceExecutor")
    public void sign(Long consumerId) {
        LocalDateTime now = LocalDateTime.now();
        String yyyyMM = now.format(DateTimeFormatter.ofPattern(":yyyyMM"));
        String key = USER_SIGN_KEY + consumerId + yyyyMM;
        int dayOfMonth = now.getDayOfMonth();
        stringRedisTemplate.opsForValue().setBit(key, dayOfMonth -1, true);
    }

}
