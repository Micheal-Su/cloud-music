package com.tacitn.all.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tacitn.all.domain.ConPossession;
import com.tacitn.all.dto.Result;

/**
 * @author DongJiShiLiu
 * @create 2023/5/9 23:30
 */
public interface ConPossessionService extends IService<ConPossession> {
    boolean setSongList(Long consumerId, Integer num);

    boolean setSong(Long consumerId, Integer num);
}
