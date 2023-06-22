package com.tacitn.songservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tacitn.songservice.domain.Tag;
import com.tacitn.songservice.dto.Result;

public interface TagService extends IService<Tag> {
    Result addTag(Tag tag);

    Result deleteTag(Tag tag);
}
