package com.tacitn.all.service.Impl;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tacitn.all.domain.Admin;
import com.tacitn.all.dto.Result;
import com.tacitn.all.mapper.AdminMapper;
import com.tacitn.all.service.AdminService;
import com.tacitn.jwt.utils.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;
import static com.tacitn.all.utils.Consts.*;

@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {

    @Autowired
    AdminMapper adminMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result logout(String token) {
        String tokenKey = REDIS_ADMIN_TOKEN_PREFIX + token;
        stringRedisTemplate.expire(tokenKey, 0, TimeUnit.SECONDS);
        return Result.ok();
    }

    @Override
    public Result checkUser(Admin admin) {
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        JSONObject jsonObject = new JSONObject();
        queryWrapper.eq("name", admin.getName()).eq("password", admin.getPassword());
        Admin one = adminMapper.selectOne(queryWrapper);
        if (one!=null){
            String token = TokenUtil.getAdminToken(one.getId(),one.getPassword());
            // 7.3.存储
            String tokenKey = REDIS_ADMIN_TOKEN_PREFIX + token;
            stringRedisTemplate.opsForValue().set(tokenKey, "1");
            // 7.4.设置token有效期，1小时
            stringRedisTemplate.expire(tokenKey, REDIS_ADMIN_TOKEN_TIME, REDIS_ADMIN_TOKEN_TIME_UNIT);
            jsonObject.set("token", token);
            return Result.ok(jsonObject);
        }
        return Result.fail("用户名或密码错误");
    }
}
