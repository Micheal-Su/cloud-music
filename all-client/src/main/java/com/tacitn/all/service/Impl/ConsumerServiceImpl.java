package com.tacitn.all.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tacitn.all.domain.Consumer;
import com.tacitn.all.domain.vo.Password;
import com.tacitn.all.domain.vo.RedisData;
import com.tacitn.all.dto.Result;
import com.tacitn.all.mapper.ConsumerMapper;
import com.tacitn.all.service.AsyncService;
import com.tacitn.all.service.ConsumerService;
import com.tacitn.all.utils.CacheClient;
import com.tacitn.all.utils.FileUtil;
import com.tacitn.jwt.utils.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.BitFieldSubCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.tacitn.all.utils.Consts.*;

@Service
public class ConsumerServiceImpl extends ServiceImpl<ConsumerMapper, Consumer> implements ConsumerService {
    @Autowired
    ConsumerMapper consumerMapper;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    AsyncService asyncService;

    @Autowired
    CacheClient cacheClient;


    @Override
    public Result login(Consumer consumer) {
        HashMap<String, Object> map = new HashMap<>();
        consumer = query().eq("email", consumer.getEmail()).eq("password", consumer.getPassword()).one();
        if (consumer != null) {
            LocalDateTime lastLoginTime = consumer.getLastLoginTime();
            int lastLoginYear = lastLoginTime.getYear();
            int dayOfYear = lastLoginTime.getDayOfYear();
            //今天第一次登录就加经验
            if (LocalDateTime.now().getYear() > lastLoginYear || dayOfYear < LocalDateTime.now().getDayOfYear()){
                consumer.setExp(consumer.getExp()+1);
                consumer.setLastLoginTime(LocalDateTime.now());
                asyncService.sign(consumer.getId());
            }
            String token = TokenUtil.getConsumerToken(consumer.getId(),consumer.getPassword());
            // 7.3.存储
            String tokenKey = REDIS_LOGIN_TOKEN_PREFIX + token;
            stringRedisTemplate.opsForValue().set(tokenKey, "1");
            // 7.4.设置token有效期，1小时
            stringRedisTemplate.expire(tokenKey, REDIS_LOGIN_TOKEN_TIME, REDIS_LOGIN_TOKEN_TIME_UNIT);

            map.put("token",token);
            map.put("consumer", consumer);
            //更新Redis信息
            updateWithExpire(consumer);
        } else {
            return Result.fail("出错");
        }

        return Result.ok(map);
    }


    @Override
    public Result queryById(Long id) {
        Consumer consumer = cacheClient.queryWithLogicalExpire(REDIS_CONSUMER_EXPIRE_PREFIX,id,
                Consumer.class,this::getById,REDIS_CONSUMER_TIME,REDIS_CONSUMER_TIME_UNIT);
        if (ObjectUtil.isNotEmpty(consumer)) {
            return Result.ok(consumer);
        }
//        缓存没有就查数据库
        consumer = query().eq("id", id).one();
        if (ObjectUtil.isNotEmpty(consumer)){
            return Result.ok(consumer);
        }

        return Result.fail("用户不存在");
    }

    @Override
    public Boolean updateWithExpire(Consumer consumer) {
        boolean update = saveOrUpdate(consumer);
        cacheClient.setWithLogicalExpire(REDIS_CONSUMER_EXPIRE_PREFIX + consumer.getId(), consumer, 1L, REDIS_CONSUMER_TIME_UNIT);
        return update;
    }


    @Override
    public Result addConsumer(Consumer consumer) {
        consumer.setCreateTime(LocalDateTime.now());
        consumer.setUpdateTime(LocalDateTime.now());
        consumer.setAvatar(FileUtil.getConsumerPicInProject());

        boolean flag = false;
        try {
            flag = save(consumer);

        } catch (Exception e) {
            if (e.getCause().toString().contains("username")) {
                return Result.fail("该用户名已存在");
            } else if (e.getCause().toString().contains("phone_num")) {
                return Result.fail("该手机后已被使用");
            } else if (e.getCause().toString().contains("email")) {
                return Result.fail("该邮箱已被使用");
            }

        }
        if (!flag) {
            return Result.fail("出错了");
        }

        // 设置用户信息逻辑过期
        RedisData redisData = new RedisData();
        redisData.setExpireTime(LocalDateTime.now().plusSeconds(3600));
        redisData.setData(consumer);
        stringRedisTemplate.opsForValue().set(REDIS_CONSUMER_EXPIRE_PREFIX + consumer.getId(), JSONUtil.toJsonStr(redisData));

        // 保存用户总数,用户id即为用户总数
        stringRedisTemplate.opsForValue().set(REDIS_CONSUMER_ACCOUNT, String.valueOf(consumer.getId()));
        return Result.ok("添加成功");
    }

    @Override
    public Result updatePassword(Password password) {
        JSONObject jsonObject = new JSONObject();
        Consumer consumer = query().eq("id", password.getConsumerId())
                .eq("password", password.getOldPassword()).one();
        if (consumer == null) {
            return Result.fail("密码错误");
        }

        // 1.获取旧token
        String oldToken = TokenUtil.getConsumerToken(consumer.getId(),consumer.getPassword());
        String oldTokenKey = REDIS_LOGIN_TOKEN_PREFIX + oldToken;
        UpdateWrapper<Consumer> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("password", password.getNewPassword())
                .eq("id", password.getConsumerId());
        update(updateWrapper);
        consumer.setPassword(password.getNewPassword());
        // 修改密码后要更新token
        // 2.获取新token
        String token = TokenUtil.getConsumerToken(consumer.getId(),consumer.getPassword());
        // 3.存储
        String tokenKey = REDIS_LOGIN_TOKEN_PREFIX + token;
        stringRedisTemplate.opsForValue().set(tokenKey, "1");
        // 4.设置token有效期，1小时
        stringRedisTemplate.expire(tokenKey, REDIS_LOGIN_TOKEN_TIME, REDIS_LOGIN_TOKEN_TIME_UNIT);
        // 5.删除旧token
        stringRedisTemplate.expire(oldTokenKey, 0, TimeUnit.SECONDS);
        jsonObject.set("token", token);
        return Result.ok();
    }

    @Override
    public Result updateConsumer(Consumer consumer) {

        consumer.setUpdateTime(LocalDateTime.now());
        boolean flag = false;
        if ("".equals(consumer.getPhoneNum())) {
            consumer.setPhoneNum(null);
        }
        try {
            if (consumer.getIntroduction() != null) {
                consumer.setIntroduction(consumer.getIntroduction().trim());
            }
            flag = updateWithExpire(consumer);

        } catch (Exception e) {
            e.printStackTrace();
            if (e.getCause().toString().contains("username")) {
                return Result.fail("该用户名已存在");
            } else if (e.getCause().toString().contains("phone_num")) {
                return Result.fail("该手机号已被使用");
            } else if (e.getCause().toString().contains("email")) {
                return Result.fail("该邮箱已被使用");
            }
        }
        if (!flag) {
            return Result.fail("出错了");
        }
        return Result.ok("修改成功");
    }

    @Override
    public Result getSignCount(Long consumerId) {
        LocalDateTime now = LocalDateTime.now();
        String yyyyMM = now.format(DateTimeFormatter.ofPattern(":yyyyMM"));
        String key = USER_SIGN_KEY + consumerId + yyyyMM;
        int dayOfMonth = now.getDayOfMonth();
        // bitField支持传入多个command的，且返回结果为多个，所以用List接收
        List<Long> result = stringRedisTemplate.opsForValue().bitField(key,
                BitFieldSubCommands.create()
                        .get(BitFieldSubCommands.BitFieldType.unsigned(dayOfMonth)).valueAt(0));

        if (result == null || result.isEmpty()) {
            return Result.ok(1);
        }
        Long num = result.get(0);
        if (num == 0) {
            return Result.ok(1);
        }
        int count = 0;
        while (true) {
            if ((num & 1) == 0) {
                // 此比特位为0，第一次出现未签到数据，可以退出循环了
                break;
            } else {
                count++;
            }
            num >>>= 1;// 右移并覆盖原有数据 num = num >>> 1;
        }
        return Result.ok(count);
    }

    @Override
    public Result updateConsumerPic(MultipartFile avatarFile, Long id) {
        Consumer consumer = getById(id);
        String consumerPic = consumer.getAvatar();
//        若图片不是默认图片，就删除
        if (!(consumerPic.equals(FileUtil.getConsumerPicInProject()))) {
            File consumerPicFile = new File(FileUtil.getProjectDir() + consumerPic);
            consumerPicFile.delete();
        }

        try {
            boolean flag = FileUtil.savePic(consumer, avatarFile);
            if (flag) {
                updateWithExpire(consumer);
                JSONObject jsonObject = new JSONObject();
                jsonObject.set("avatar", consumer.getAvatar());
                return Result.ok(jsonObject);
            }
            return Result.fail("更新失败");
        } catch (IOException e) {
            return Result.fail("更新失败，服务器异常");
        }
    }

    @Override
    public List getListByIds(List<Long> ids) {
        return query().in("id", ids).list();
    }

}
