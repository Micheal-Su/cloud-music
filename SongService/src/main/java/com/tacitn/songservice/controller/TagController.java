package com.tacitn.songservice.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tacitn.songservice.domain.Tag;
import com.tacitn.songservice.dto.Result;
import com.tacitn.songservice.service.TagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/tag")
public class TagController {
    @Autowired
    TagService tagService;

    @PostMapping("add")
    @Transactional
    public Result addTag(Tag tag){
        return tagService.addTag(tag);
    }

    @PostMapping("delete")
    public Result deleteTag(Tag tag){
        return tagService.deleteTag(tag);
    }

    @GetMapping("getAllTags")
    public List<Tag> selectAll(){
        QueryWrapper<Tag> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("name");
        return tagService.list(queryWrapper);
    }
}
