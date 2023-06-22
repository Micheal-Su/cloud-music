package com.tacitn.songservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tacitn.songservice.domain.Song;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SongMapper extends BaseMapper<Song> {

}
