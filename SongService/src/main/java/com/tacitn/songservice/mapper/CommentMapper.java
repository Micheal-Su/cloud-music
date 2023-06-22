package com.tacitn.songservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tacitn.songservice.domain.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

/**
 * @author DongJiShiLiu
 * @create 2022/11/29 13:20
 */
@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
    @Update("update comment set up = up + 1 where id = #{commentId}")
    Integer plusUp (Long commentId);

    @Update("update comment set up = up - 1 where id = #{commentId}")
    Integer downUp (Long commentId);
}
