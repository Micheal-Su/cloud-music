package com.tacitn.songservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tacitn.songservice.domain.ConComUp;
import com.tacitn.songservice.dto.Result;

public interface ConComUpService extends IService<ConComUp> {
    Result changeUp(ConComUp conComUp);
}
