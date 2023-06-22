package com.tacitn.all.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.tacitn.all.domain.Consumer;
import com.tacitn.all.domain.vo.ConsumerVo;
import com.tacitn.all.domain.vo.RedisData;
import com.tacitn.all.service.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import static com.tacitn.all.utils.Consts.*;


@Service
@Slf4j
public class MailServiceImpl implements MailService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;


//  这个是要根据返回结果在执行下一步的，不能异步
    @Override
    public Boolean register(ConsumerVo voCode){
        String email = voCode.getEmail();
        String code = stringRedisTemplate.opsForValue().get(REDIS_SIGNUP_CODE_PREFIX + email);

        //如果email数据为空，或者验证码不一致，注册失败
        if (email == null || email.isEmpty()){
            //return "error,请重新注册";
            return false;
        }else if (!code.equals(voCode.getCode())){
            //return "error,请重新注册";
            return false;
        }

        //跳转成功页面
        return true;
    }

    @Override
    @Async("asyncServiceExecutor")
    public Boolean registerSuccess(Consumer consumer) {
        try {
            // 设置用户信息逻辑过期
            RedisData redisData = new RedisData();
            redisData.setExpireTime(LocalDateTime.now().plusSeconds(3600));
            redisData.setData(consumer);
            stringRedisTemplate.opsForValue().set(REDIS_CONSUMER_EXPIRE_PREFIX + consumer.getId(), JSONUtil.toJsonStr(redisData));
            // 保存用户总数,用户id即为用户总数
            stringRedisTemplate.opsForValue().set(REDIS_CONSUMER_ACCOUNT, String.valueOf(consumer.getId()));
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
