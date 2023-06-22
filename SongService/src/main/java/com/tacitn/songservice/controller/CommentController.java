package com.tacitn.songservice.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tacitn.feign.clients.AllClient;
import com.tacitn.feign.domain.Consumer;
import com.tacitn.songservice.domain.Comment;
import com.tacitn.feign.domain.dto.Result;
import com.tacitn.songservice.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/comment")
public class CommentController {
    @Autowired
    CommentService commentService;

    @Autowired
    AllClient allClient;


    @PostMapping("add")
    Result addComment(Comment comment) {
        comment.setCreateTime(new Date());
        boolean flag = commentService.save(comment);
        if (flag) {
            return Result.ok("评论成功");
        }
        return Result.fail("评论失败");
    }

    @PostMapping("delete")
    public Result deleteComment(Comment comment) {
        boolean flag = commentService.removeById(comment.getId());
        if (flag) {
            return Result.ok("删除成功");
        }
        return Result.fail("删除失败");

    }


    @GetMapping("/selectByPrimaryKey")
    public Comment selectByPrimaryKey(Integer id) {
        return commentService.getById(id);
    }


    @GetMapping("/selectByType")
    public List<Comment> selectByType(Integer type, Integer id) {
        QueryWrapper<Comment> parentWrapper = new QueryWrapper<>();
        if (type == 1) {
            parentWrapper.eq("song_list_id", id)
                    .eq("parent_id", 0);

        } else {
            parentWrapper.eq("song_id", id)
                    .eq("parent_id", 0);
        }
        List<Comment> parentComments = commentService.list(parentWrapper);
        return parentComments;
    }

    @GetMapping("/getChildCom")
    public List<Comment> getChildComment(Long id) {
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", id);
        List<Comment> comments = commentService.list(queryWrapper);
        for (Comment comment : comments){
            // 远程调用AllClient，获取评论回复的对象
            // 而远程调用的过程中可能会失败，进而导致one为null，进而one.getUsername() 空指针异常，所以可以在AllClient中
            // 编写失败后的降级逻辑，具体查看feign-api的AllClientFallbackFactory (另外，调用方得开启feign对sentinel的支持)
            Result result = allClient.selectConsumerById(comment.getToConId().toString());
            Object data = result.getData();
            Map<String, Object> map = BeanUtil.beanToMap(data);
            comment.setToConName(map.get("username").toString());
        }
        return comments;
    }

    //查询某歌曲下的所有父级评论
    @GetMapping("selectBySongId")
    public List<Comment> selectBySongId(Integer songId) {
        QueryWrapper<Comment> parentWrapper = new QueryWrapper<>();
        parentWrapper.eq("song_id", songId)
                .eq("parent_id", 0);
        List<Comment> parentComments = commentService.list(parentWrapper);
        return parentComments;
    }

    //查询某歌单下的所有父级评论
    @GetMapping("selectParentCommentBySongListId")
    public List<Comment> selectParentCommentBySongListId(Integer songListId) {
        QueryWrapper<Comment> parentWrapper = new QueryWrapper<>();
        parentWrapper.eq("song_list_id", songListId)
                .eq("parent_id", 0);
        List<Comment> parentComments = commentService.list(parentWrapper);
        return parentComments;
    }

    //查询某歌单下的所有评论
    @GetMapping("selectAllBySongListId")
    public List<Comment> selectBySongListId(Integer songListId) {
        QueryWrapper<Comment> parentWrapper = new QueryWrapper<>();
        parentWrapper.eq("song_list_id", songListId);
        List<Comment> comments = commentService.list(parentWrapper);
        return comments;
    }
    //查询某歌单下的所有评论
    @GetMapping("selectAllBySongId")
    public List<Comment> selectAllBySongId(Integer songId) {
        QueryWrapper<Comment> parentWrapper = new QueryWrapper<>();
        parentWrapper.eq("song_list_id", songId);
        List<Comment> comments = commentService.list(parentWrapper);
        return comments;
    }

}
