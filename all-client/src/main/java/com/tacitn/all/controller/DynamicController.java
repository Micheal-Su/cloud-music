package com.tacitn.all.controller;

import com.tacitn.all.domain.Dynamic;
import com.tacitn.all.domain.Follow;
import com.tacitn.all.domain.vo.ScrollResult;
import com.tacitn.all.dto.Result;
import com.tacitn.all.service.DynamicService;
import com.tacitn.all.service.FollowService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author DongJiShiLiu
 * @create 2023/3/10 18:48
 */

@Slf4j
@RestController
@RequestMapping("/dynamic")
public class DynamicController {
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    DynamicService dynamicService;

    @Autowired
    FollowService followService;

    @GetMapping("/getAllMyDynamics")
    public Result getAllMyDynamics(Long consumerId){
        List<Dynamic> dynamicList = dynamicService.query().eq("consumer_id", consumerId).list();
        return Result.ok(dynamicList);
    }
    /**
     *
     * @param fansId 粉丝的id
     * @param lastId 上一次查询到的最旧的动态的时间戳
     * @param offset 偏移量：上一次查询结果中和最小值相等的个数
     * @return
     */
    @GetMapping("/getFollowsDynamics")
    public Result getFollowsDynamics(Long fansId, Long lastId, Integer offset){
        ScrollResult scrollResult = dynamicService.queryBlogOfFollow(fansId, lastId, offset);
        if (scrollResult == null){
            return Result.warning("已经到底了");
        }
        return Result.ok(scrollResult);
    }
    @PostMapping("/postDynamic")
    @Transactional
    public Result postDynamic(Dynamic dynamic, @RequestParam("image") MultipartFile image) {
        return dynamicService.postDynamic(dynamic,image);

    }

}

