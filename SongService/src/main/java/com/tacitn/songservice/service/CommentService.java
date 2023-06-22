package com.tacitn.songservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tacitn.songservice.domain.Comment;
import com.tacitn.songservice.domain.ConComUp;

public interface CommentService extends IService<Comment> {
    Integer plusUp(ConComUp conComUp);

    Integer downUp(ConComUp conComUp);
}
