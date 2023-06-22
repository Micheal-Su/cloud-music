package com.tacitn.songservice.domain;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 评论点赞
 */
@Data
@NoArgsConstructor
public class ConComUp {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long consumerId;

    private Long commentId;

}
