package com.tacitn.songservice.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
//此表为歌单ID和歌曲ID的对应关系
public class ListSong {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long songId;

    private Long songListId;
}
