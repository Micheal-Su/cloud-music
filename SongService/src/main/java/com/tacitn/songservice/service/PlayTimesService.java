package com.tacitn.songservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tacitn.songservice.domain.ConPlayTimes;
import com.tacitn.songservice.domain.PlayTimes;

public interface PlayTimesService extends IService<PlayTimes> {

    Boolean plusTimes(ConPlayTimes conPlayTimes);
}
