package com.tacitn.all.service.Impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tacitn.all.domain.ConPossession;
import com.tacitn.all.mapper.ConPossessionMapper;
import com.tacitn.all.service.ConPossessionService;
import org.springframework.stereotype.Service;

@Service
public class ConPossessionServiceImpl extends ServiceImpl<ConPossessionMapper, ConPossession> implements ConPossessionService{
    @Override
    public boolean setSongList(Long consumerId, Integer num) {
        UpdateWrapper<ConPossession> updateWrapper = new UpdateWrapper<>();
        if(num > 0){
            updateWrapper.setSql("song_list_remain = song_list_remain+"+ num).eq("consumer_id", consumerId);
        }else {
            num = -num;
            updateWrapper.setSql("song_list_remain = song_list_remain-"+ num).eq("consumer_id", consumerId);
        }

        return update(updateWrapper);
    }

    @Override
    public boolean setSong(Long consumerId, Integer num) {
        UpdateWrapper<ConPossession> updateWrapper = new UpdateWrapper<>();
        if(num > 0){
            updateWrapper.setSql("song_remain = song_remain+"+ num).eq("consumer_id", consumerId);
        }else {
            num = -num;
            updateWrapper.setSql("song_remain = song_remain-"+ num).eq("consumer_id", consumerId);
        }
        return update(updateWrapper);
    }
}
