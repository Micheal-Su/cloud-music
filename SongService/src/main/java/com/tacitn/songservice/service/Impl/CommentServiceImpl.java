package com.tacitn.songservice.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tacitn.songservice.domain.Comment;
import com.tacitn.songservice.domain.ConComUp;
import com.tacitn.songservice.mapper.CommentMapper;
import com.tacitn.songservice.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment>implements CommentService {
    @Autowired
    CommentMapper commentMapper;

    public Integer plusUp(ConComUp conComUp){
        return commentMapper.plusUp(conComUp.getCommentId());
    }

    public Integer downUp(ConComUp conComUp){
        return commentMapper.downUp(conComUp.getCommentId());
    }

}
