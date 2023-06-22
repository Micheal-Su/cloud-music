package com.tacitn.songservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tacitn.songservice.domain.Singer;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SingerMapper extends BaseMapper<Singer> {
}
