package com.tacitn.songservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tacitn.songservice.domain.PlayTimes;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PlayTimesMapper extends BaseMapper<PlayTimes> {
}
