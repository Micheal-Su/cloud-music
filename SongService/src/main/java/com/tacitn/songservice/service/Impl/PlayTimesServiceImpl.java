package com.tacitn.songservice.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tacitn.songservice.domain.ConPlayTimes;
import com.tacitn.songservice.domain.PlayTimes;
import com.tacitn.songservice.mapper.PlayTimesMapper;
import com.tacitn.songservice.service.ConPlayTimesService;
import com.tacitn.songservice.service.PlayTimesService;
//import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service
public class PlayTimesServiceImpl extends ServiceImpl<PlayTimesMapper, PlayTimes> implements PlayTimesService {
    @Autowired
    ConPlayTimesService conPlayTimesService;


    @Override
    public Boolean plusTimes(ConPlayTimes conPlayTimes) {
        QueryWrapper<ConPlayTimes> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("song_id", conPlayTimes.getSongId())
                .eq("consumer_id", conPlayTimes.getConsumerId());
        ConPlayTimes one = conPlayTimesService.getOne(queryWrapper);

        PlayTimes pOne = query().eq("song_id", conPlayTimes.getSongId()).one();
        if (one == null) {//若用户没听过这首歌
            conPlayTimes.setPlayTimes(1);
            conPlayTimes.setDayTimes(1);
            conPlayTimes.setWeekTimes(1);
            conPlayTimes.setMonthTimes(1);
            conPlayTimes.setYearTimes(1);
        } else {
            conPlayTimes.setId(one.getId());
            conPlayTimes.setPlayTimes(one.getPlayTimes() + 1);
            conPlayTimes.setDayTimes(one.getDayTimes() + 1);
            conPlayTimes.setWeekTimes(one.getWeekTimes() + 1);
            conPlayTimes.setMonthTimes(one.getMonthTimes() + 1);
            conPlayTimes.setYearTimes(one.getYearTimes() + 1);
        }
        boolean flag1 = conPlayTimesService.saveOrUpdate(conPlayTimes);
        boolean flag2 = true;
        if (conPlayTimes.getDayTimes() <= 5) {//一个用户一天最多贡献5播放量
            pOne.setPlayTimes(pOne.getPlayTimes() + 1);
            pOne.setDayTimes(pOne.getDayTimes() + 1);
            pOne.setWeekTimes(pOne.getWeekTimes() + 1);
            pOne.setMonthTimes(pOne.getMonthTimes() + 1);
            pOne.setYearTimes(pOne.getYearTimes() + 1);
            flag2 = saveOrUpdate(pOne);
        }

        return flag1 && flag2;
    }
}
