package com.tacitn.otherclient.web;

import com.tacitn.otherclient.domain.Other;
import com.tacitn.otherclient.dto.Result;
import com.tacitn.otherclient.service.OtherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author DongJiShiLiu
 * @create 2023/4/24 12:28
 */
@RestController
@RequestMapping("/other")
public class OtherController {
    @Autowired
    private OtherService otherService;

    @GetMapping("/{id}")
    private Other getOtherById(@PathVariable("id") Long id){
        return otherService.getById(id);
    }

    @PostMapping("add")
    private Result addOther(Other other){
        boolean save = otherService.save(other);
        if (save){
            return Result.ok();
        }
        return Result.fail("添加失败");
    }
}
