package com.tacitn.songservice.controller;

import com.tacitn.songservice.domain.ConPlayTimes;
import com.tacitn.songservice.domain.PlayTimes;
import com.tacitn.songservice.service.ConPlayTimesService;
import com.tacitn.songservice.service.PlayTimesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/playTimes")
public class PlayTimesController {
    @Autowired
    PlayTimesService playTimesService;

    @Autowired
    ConPlayTimesService conPlayTimesService;

    @PostMapping("plusTimes")
    @Transactional
    public Boolean plusTimes(ConPlayTimes conPlayTimes) {
        Long consumerId = conPlayTimes.getConsumerId();
        if (consumerId != -1){
            return playTimesService.plusTimes(conPlayTimes);
        }
        return null;
    }


    @GetMapping("getBysongId")
    public PlayTimes getBysongId(Integer songId) {
        return playTimesService.getById(songId);
    }

    @GetMapping("getAll")
    public List<PlayTimes> getAll() {
        return playTimesService.list();
    }

}
