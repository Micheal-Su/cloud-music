package com.tacitn.all.controller;

import cn.hutool.core.util.StrUtil;
import com.tacitn.all.domain.Consumer;
import com.tacitn.all.domain.vo.Password;
import com.tacitn.all.dto.Result;
import com.tacitn.all.service.ConsumerService;
import com.tacitn.all.utils.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.annotation.Resource;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.tacitn.all.utils.Consts.*;

@Slf4j
@RestController
@RequestMapping("/consumer")
public class ConsumerController {
    @Autowired
    ConsumerService consumerService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;


    //    这个是后台系统添加用户的请求，用户注册的在SignUpConController/signUp/regist
    @PostMapping("/add")
    public Result addConsumer(@RequestBody Consumer consumer) {//@RequestBody接收Json格式的数据
        return consumerService.addConsumer(consumer);

    }

    @PostMapping("/logout")
    public Boolean logoutConsumer(@RequestParam("token") String token) {
        String tokenKey = REDIS_LOGIN_TOKEN_PREFIX + token;

        String tokenExists = stringRedisTemplate.opsForValue().get(tokenKey);
        if (StrUtil.isBlank(tokenExists)) {
            return true;
        }
        return stringRedisTemplate.expire(tokenKey,
                0,
                TimeUnit.SECONDS);
    }

    @PostMapping("/login")
    public Result loginConsumer(@RequestBody Consumer consumer) {//@RequestBody接收Json格式的数据

        return consumerService.login(consumer);
    }

    @PostMapping("/updatePassword")
    public Result updatePassword(@RequestBody Password password) {
        return consumerService.updatePassword(password);


    }

    @PostMapping("/update")
    public Result updateConsumer(@RequestBody Consumer consumer) {
        return consumerService.updateConsumer(consumer);

    }

    @GetMapping("/delete")
    public boolean deleteConsumer(Long id) {
        String avatar = consumerService.getById(id).getAvatar();
        if (!(avatar.equals(FileUtil.getConsumerPicInProject()))) {
            File avatarFile = new File(FileUtil.getProjectDir() + avatar);
            avatarFile.delete();
        }
        return consumerService.removeById(id);
    }

    @GetMapping("/signCount")
    public Result signCount(Long consumerId) {
        return consumerService.getSignCount(consumerId);

    }

    @GetMapping("/deleteSelectedConsumers")
    @Transactional
    public boolean deleteSelectedConsumers(Integer[] ids) {

        List<Consumer> consumerList = consumerService.listByIds(Arrays.asList(ids));
        for (Consumer consumer : consumerList) {
            //        删除用户图片
            String avatar = consumerService.getById(consumer.getId()).getAvatar();
            //        如果不是默认图片，则删除
            if (!(avatar.equals(FileUtil.getConsumerPicInProject()))) {
                File avatarFile = new File(FileUtil.getProjectDir() + avatar);
                avatarFile.delete();
            }
        }

        return consumerService.removeByIds(Arrays.asList(ids));
    }

    @GetMapping("/selectByPrimaryKey/{id}")
    public Result selectConsumerById(@PathVariable("id") String id) {
        Long idLong;
        try {
            idLong = Long.parseLong(id);
            /*
                 // 测试降级熔断用
                 if (idLong == 1){
                    Thread.sleep(60);
                 }
             */

        } catch (NumberFormatException e) {
            e.printStackTrace();
            return Result.fail("无效id");
        }
        return consumerService.queryById(idLong);
    }

    /**
     * 获取包括密码在内的所有数据
     * @param id
     * @return
     */
    @GetMapping("/getConsumerAll/{id}")
    public Consumer getConsumerAll(@PathVariable("id") String id) {
        return consumerService.getById(id);
    }

    @GetMapping("/getConsumerList")
    public List getConsumerList(@RequestParam("ids") List<Long> ids) {
        return consumerService.getListByIds(ids);
    }


    @GetMapping("/selectAll")
    public List<Consumer> selectAll() {
        return consumerService.list();
    }

    @PostMapping(value = "/updateConsumerAvatar")
    public Result updateConsumerPic(@RequestParam("file") MultipartFile avatarFile, @RequestParam("id") Long id) {
        return consumerService.updateConsumerPic(avatarFile,id);
    }
}
