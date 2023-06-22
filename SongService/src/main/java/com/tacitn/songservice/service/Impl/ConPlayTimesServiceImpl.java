package com.tacitn.songservice.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tacitn.songservice.domain.ConPlayTimes;
import com.tacitn.songservice.mapper.ConPlayTimesMapper;
import com.tacitn.songservice.service.ConPlayTimesService;
import org.springframework.stereotype.Service;

@Service
public class ConPlayTimesServiceImpl extends ServiceImpl<ConPlayTimesMapper, ConPlayTimes> implements ConPlayTimesService {
}
