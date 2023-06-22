package com.tacitn.all.controller;

import com.tacitn.all.domain.Follow;
import com.tacitn.all.dto.Result;
import com.tacitn.all.service.FollowService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * @author DongJiShiLiu
 * @create 2023/3/9 14:07
 */
@RestController
@RequestMapping("/follow")
@Slf4j
public class FollowController {
    @Autowired
    FollowService followService;

    @GetMapping("/getFansByConsumerId")
    public Result getFansByConsumerId(Long consumerId) {
        return followService.getFansByConsumerId(consumerId);
    }

    @GetMapping("/getConsumersByFansId")
    public Result getConsumersByFansId(Long fansId) {
        return followService.getConsumersByFansId(fansId);
    }

    @GetMapping("/ifFollowed")
    public boolean ifFollowed(Long consumerId, Long fansId) {
        return followService.query().eq("consumer_id", consumerId)
                .eq("fans_id", fansId).one() != null;
    }

    @PostMapping("/follow")
    public Result follow(@RequestParam("consumerId") Long consumerId, @RequestParam("fansId") Long fansId) {
        return followService.follow(consumerId,fansId);

    }
}
