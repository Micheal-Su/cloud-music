package com.tacitn.all.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tacitn.all.domain.Consumer;
import com.tacitn.all.domain.vo.ConsumerDTO;
import com.tacitn.all.domain.vo.ConsumerVo;
import com.tacitn.all.domain.vo.SignUpCon;
import com.tacitn.all.dto.Result;
import com.tacitn.all.mapper.SignUpConMapper;
import com.tacitn.all.service.AsyncService;
import com.tacitn.all.service.ConsumerService;
import com.tacitn.all.service.MailService;
import com.tacitn.all.service.SignUpConService;
import com.tacitn.all.utils.FileUtil;
import com.tacitn.all.utils.IpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.tacitn.all.utils.Consts.*;

@Slf4j
@Service
public class SignUpConServiceImpl extends ServiceImpl<SignUpConMapper, SignUpCon> implements SignUpConService {

    @Autowired
    ConsumerService consumerService;

    @Autowired
    SignUpConService signUpConService;

    @Autowired
    private MailService mailService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private JavaMailSender javaMailSender;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Value("${spring.mail.username}")
    private String from;


    @Override
    public Result commitUserInfo(SignUpCon signUpCon, HttpServletRequest request) {
        //防止碰巧两个号差不多时间内提交注册时，邮箱一样，
        //但是此时两个号都已经在接收验证码了，最后可能两个号的邮箱一样
        String signUpConKey = REDIS_SIGNUP_USER_PREFIX + signUpCon.getEmail();
        if (signUpCon.getResend()) {
            Map<Object, Object> consumerMap = stringRedisTemplate.opsForHash().
                    entries(REDIS_SIGNUP_USER_PREFIX + signUpCon.getEmail());

            SignUpCon signUpping = BeanUtil.fillBeanWithMap(consumerMap, new SignUpCon(), false);
            if (signUpping.getEmail() == null) {//Redis中保存的注册用户的临时信息
                return Result.warning("身份以过期，请重新注册");
            }
            stringRedisTemplate.expire(REDIS_SIGNUP_USER_PREFIX + signUpping.getEmail(), REDIS_SIGNUP_USER_TIME,
                    REDIS_SIGNUP_USER_TIME_UNIT);
            sendMimeMail(signUpping.getEmail());
            return Result.ok("重新发送成功，请留意邮箱");
        }

        String ipAddr = IpUtil.getIpAddr(request);
        QueryWrapper<Consumer> conqueryWrapper2 = new QueryWrapper<>();
        conqueryWrapper2.eq("email", signUpCon.getEmail());
        Consumer one = consumerService.getOne(conqueryWrapper2);
        //查询正在注册的用户中有无此email的
        Map<Object, Object> consumerMap = stringRedisTemplate.opsForHash().
                entries(REDIS_SIGNUP_USER_PREFIX + signUpCon.getEmail());
        SignUpCon signUpping = BeanUtil.fillBeanWithMap(consumerMap, new SignUpCon(), false);

        if (one != null || !consumerMap.isEmpty()) {
            if (signUpping.getEmail() != null) {
                return Result.warning("验证码已发送，注意查收");
            }
            return Result.fail("该邮箱已存在");
        }

        signUpCon.setEmail(signUpCon.getEmail());
        signUpCon.setIp(ipAddr);
        ConsumerDTO consumerDTO = BeanUtil.copyProperties(signUpCon, ConsumerDTO.class);
        Map<String, Object> beanToMap = BeanUtil.beanToMap(consumerDTO, new HashMap<>(),
                CopyOptions.create()
                        .setIgnoreNullValue(true)
                        .setFieldValueEditor((fieldName, fieldValue) -> fieldValue.toString()));
        stringRedisTemplate.opsForHash().putAll(signUpConKey,
                beanToMap);
        Boolean expire = stringRedisTemplate.expire(signUpConKey, REDIS_SIGNUP_USER_TIME, REDIS_SIGNUP_USER_TIME_UNIT);

        if (expire) {
            sendMimeMail(signUpCon.getEmail());
            return Result.ok("请留意邮箱");
        }
        return Result.ok("提交失败");

    }

    /**
     * 由MQ远程调用，发送验证码至邮箱
     *
     * @param email
     * @return
     */
    @Override
    public Result sendCode(String email) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setSubject("验证码邮件");//主题
            String code = RandomUtil.randomNumbers(6);
            stringRedisTemplate.opsForValue().set(REDIS_SIGNUP_CODE_PREFIX + email, code,
                    REDIS_SIGNUP_CODE_TIME, REDIS_SIGNUP_CODE_TIME_UNIT);
            mailMessage.setText("您收到的验证码是：" + code + "，两分钟后失效");//内容
            mailMessage.setTo(email);//发给谁
            mailMessage.setFrom(from);//你自己的邮箱
            javaMailSender.send(mailMessage);//发送
            //设置2分钟后失效
            stringRedisTemplate.expire(REDIS_SIGNUP_USER_PREFIX + email, REDIS_SIGNUP_USER_TIME,
                    REDIS_SIGNUP_USER_TIME_UNIT);
            return Result.ok("发送成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.ok("发送失败");
        }
    }

    @Override
    public Result doRegist(ConsumerVo vo) {
        if (mailService.register(vo)) {
            Map<Object, Object> consumerMap = stringRedisTemplate.opsForHash().
                    entries(REDIS_SIGNUP_USER_PREFIX + vo.getEmail());
            SignUpCon signUpping = BeanUtil.fillBeanWithMap(consumerMap, new SignUpCon(), false);

            //验证码验证对之后将正在注册的用户转为注册完成用户
            Consumer consumer = new Consumer();
            consumer.setCreateTime(LocalDateTime.now());
            consumer.setLastLoginTime(LocalDateTime.now());
            consumer.setUpdateTime(LocalDateTime.now());
            consumer.setEmail(vo.getEmail());
            consumer.setPhoneNum(signUpping.getPhoneNum());
            consumer.setPassword(signUpping.getPassword());
            consumer.setUsername(signUpping.getEmail());
            consumer.setSex(signUpping.getSex());
            consumer.setExp(1);
            consumer.setLevel(1);
            consumer.setAvatar(FileUtil.getConsumerPicInProject());
            boolean save = consumerService.save(consumer);
//            清除注册验证的用户信息
            Boolean expire = stringRedisTemplate.expire(REDIS_SIGNUP_USER_PREFIX + signUpping.getEmail(), 0, TimeUnit.SECONDS)
                    && stringRedisTemplate.expire(REDIS_SIGNUP_CODE_PREFIX + signUpping.getEmail(), 0, TimeUnit.SECONDS);
            if (save && expire) {
                // 将数据存入redis
                mailService.registerSuccess(consumer);
                return Result.ok("注册成功");
            }
        }
        return Result.ok("注册失败");
    }

    private void sendMimeMail(String email) {
        // 其实在SpringBoot中，会自动把传入队列的消息设置为持久化的，不需要自己设置，比如直接传json.getBytes(StandardCharsets.UTF_8)即可

        rabbitTemplate.convertAndSend(EMAIL_EXCHANGE_NAME, EMAIL_SEND_KEY, email);
        // 上面是异步的，第一次能responseResult.setMsg("系统出错，请等待修复")可能是因为第一次返回得慢
    }
}
