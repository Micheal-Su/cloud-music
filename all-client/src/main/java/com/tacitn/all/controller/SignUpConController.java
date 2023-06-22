package com.tacitn.all.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONObject;
import com.tacitn.all.domain.Consumer;
import com.tacitn.all.domain.vo.ConsumerVo;
import com.tacitn.all.domain.vo.SignUpCon;
import com.tacitn.all.dto.Result;
import com.tacitn.all.service.ConsumerService;
import com.tacitn.all.service.MailService;
import com.tacitn.all.service.SignUpConService;
import com.tacitn.all.utils.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.tacitn.all.utils.Consts.REDIS_SIGNUP_CODE_PREFIX;
import static com.tacitn.all.utils.Consts.REDIS_SIGNUP_USER_PREFIX;

@Slf4j
@RestController
@RequestMapping("/signUp")
public class SignUpConController {

    @Autowired
    SignUpConService signUpConService;


    @PostMapping("/regist")
    @Transactional
    public Result regist(ConsumerVo vo) {
        return signUpConService.doRegist(vo);

    }

    @PostMapping("/sendCode")
    public Result sendCode(@RequestParam("email") String email) {
        return signUpConService.sendCode(email);
    }

    @PostMapping("/commit")
    public Result commit(@RequestBody SignUpCon signUpCon, HttpServletRequest request) {
         log.warn("email :{}",signUpCon.getEmail());
         return signUpConService.commitUserInfo(signUpCon, request);
    }
}
