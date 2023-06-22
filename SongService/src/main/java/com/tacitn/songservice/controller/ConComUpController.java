package com.tacitn.songservice.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tacitn.songservice.domain.ConComUp;
import com.tacitn.songservice.dto.Result;
import com.tacitn.songservice.service.CommentService;
import com.tacitn.songservice.service.ConComUpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/conComUp")
public class ConComUpController {
    @Autowired
    ConComUpService conComUpService;

    @Autowired
    CommentService commentService;

    //获取某一用户赞过的评论
    @GetMapping("upComments")
    List<ConComUp> upComments(Long consumerId) {
        QueryWrapper<ConComUp> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("consumer_id", consumerId);
        List<ConComUp> conComUpList = conComUpService.list(queryWrapper);
        return conComUpList;
    }
    //判断用户是否赞过该评论
    @GetMapping("hadUp")
    public boolean hadUp(Long consumerId, Integer commentId) {
        QueryWrapper<ConComUp> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("consumer_id", consumerId);
        queryWrapper.eq("comment_id", commentId);
        ConComUp conComUp = conComUpService.getOne(queryWrapper);
        if (conComUp == null) {
            return false;
        } else {
            return true;
        }
    }

    @PostMapping("changeUp")
    public Result changeUp(ConComUp conComUp) {
        return conComUpService.changeUp(conComUp);

    }
}
