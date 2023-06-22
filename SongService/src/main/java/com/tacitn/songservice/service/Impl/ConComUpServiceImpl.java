package com.tacitn.songservice.service.Impl;

import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tacitn.songservice.domain.ConComUp;
import com.tacitn.songservice.dto.Result;
import com.tacitn.songservice.mapper.ConComUpMapper;
import com.tacitn.songservice.service.CommentService;
import com.tacitn.songservice.service.ConComUpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConComUpServiceImpl extends ServiceImpl<ConComUpMapper, ConComUp> implements ConComUpService {
    @Autowired
    ConComUpMapper conComUpMapper;

    @Autowired
    ConComUpService conComUpService;

    @Autowired
    CommentService commentService;

    @Override
    public Result changeUp(ConComUp conComUp) {
        int flag;
        QueryChainWrapper<ConComUp> queryChainWrapper = query().eq("consumer_id", conComUp.getConsumerId())
                .eq("comment_id", conComUp.getCommentId());

        ConComUp tempConComUp = queryChainWrapper.one();
        if (tempConComUp == null) {
            flag = commentService.plusUp(conComUp);
            conComUpService.save(conComUp);
        } else {
            commentService.downUp(conComUp);
            conComUpService.remove(queryChainWrapper);
            flag = 2;

        }
        if (flag == 1) {
            return Result.ok("点赞成功");
        } else if (flag == 2) {
            return Result.warning("取消点赞");
        }
        return Result.fail("点赞失败");

    }
}
