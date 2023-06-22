package com.tacitn.songservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tacitn.songservice.domain.Rank;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface RankMapper extends BaseMapper<Rank> {
    @Select("select avg(score) from rank where song_list_id = #{songListId}")
    Double getScoreSum(Integer songListId);
}
