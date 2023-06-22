package com.tacitn.all.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tacitn.all.domain.Follow;
import com.tacitn.all.dto.Result;

/**
 * <p>
 * 粉丝关注表，两者一一对应
 * </p>
 *
 */
public interface FollowService extends IService<Follow> {
    Result getFansByConsumerId(Long consumerId);
    Result getConsumersByFansId(Long fansId);

    Result follow(Long consumerId, Long fansId);
}
