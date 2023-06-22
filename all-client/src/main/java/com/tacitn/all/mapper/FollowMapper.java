package com.tacitn.all.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tacitn.all.domain.Follow;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.List;

@Mapper
public interface FollowMapper extends BaseMapper<Follow> {
    List<HashMap<String,Object>> getFansByConsumerId(Long consumerId);

    List<HashMap<String,Object>> getConsumersByFansId(Long fansId);
}
