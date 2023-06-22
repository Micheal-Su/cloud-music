package com.tacitn.all.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tacitn.all.domain.Follow;
import com.tacitn.all.dto.Result;
import com.tacitn.all.mapper.FollowMapper;
import com.tacitn.all.service.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

/**
 * @author DongJiShiLiu
 * @create 2023/3/9 14:06
 */
@Service
public class FollowServiceImpl extends ServiceImpl<FollowMapper, Follow> implements FollowService {
    @Autowired
    FollowMapper followMapper;

    @Override
    public Result getFansByConsumerId(Long consumerId) {
        List<HashMap<String, Object>> fansList = followMapper.getFansByConsumerId(consumerId);
        return Result.ok(fansList);
    }

    @Override
    public Result getConsumersByFansId(Long fansId) {
        List<HashMap<String, Object>> consumerList = followMapper.getConsumersByFansId(fansId);
        return Result.ok(consumerList);
    }

    @Override
    public Result follow(Long consumerId, Long fansId) {
        Follow one = query().eq("consumer_id", consumerId)
                .eq("fans_id", fansId).one();
        if (one != null) {
            boolean remove = removeById(one.getId());
            if (remove) {
                return Result.ok("取关成功");
            }
            return Result.fail("取关失败");

        }
        Follow follow = new Follow();
        follow.setConsumerId(consumerId);
        follow.setFansId(fansId);
        follow.setFollowTime(LocalDateTime.now());
        boolean save = save(follow);
        if (save) {
            return Result.ok("关注成功");
        }
        return Result.fail("关注失败");


    }
}
