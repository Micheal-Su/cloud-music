package com.tacitn.songservice.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tacitn.songservice.domain.Tag;
import com.tacitn.songservice.dto.Result;
import com.tacitn.songservice.mapper.TagMapper;
import com.tacitn.songservice.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag>implements TagService {
    @Autowired
    TagMapper tagMapper;

    @Override
    public Result addTag(Tag tag) {
        Tag one = query().eq("name", tag.getName()).one();
        if (one != null){
            return Result.warning("已存在该标签");
        }
        boolean flag = save(tag);
        if (flag){
            return Result.ok("添加成功");
        }
        return Result.fail("添加失败");
    }

    @Override
    public Result deleteTag(Tag tag) {
        QueryWrapper<Tag> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", tag.getName());
        boolean flag = remove(queryWrapper);
        if (flag){
            return Result.ok("删除成功");
        }
        return Result.fail("删除失败");
    }
}
