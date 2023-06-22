package com.tacitn.all.controller;

import com.tacitn.all.service.ConPossessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author DongJiShiLiu
 * @create 2023/5/9 23:31
 */
@RestController
@RequestMapping("/conPossession")
public class ConPossessionController {
    @Autowired
    private ConPossessionService conPossessionService;

    @PostMapping("/setSongList")
    private boolean setSongList(@RequestParam("consumerId") Long consumerId
            ,@RequestParam("num") Integer num){
        return conPossessionService.setSongList(consumerId,num);
    }

    @PostMapping("/setSong")
    private boolean setSong(@RequestParam("consumerId") Long consumerId
            ,@RequestParam("num") Integer num){
        return conPossessionService.setSong(consumerId,num);
    }

}
