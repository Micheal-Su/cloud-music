package com.tacitn.songservice.service.Impl;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tacitn.songservice.domain.Collect;
import com.tacitn.songservice.domain.Singer;
import com.tacitn.songservice.domain.Song;
import com.tacitn.songservice.domain.SongList;
import com.tacitn.songservice.dto.Result;
import com.tacitn.songservice.mapper.CollectMapper;
import com.tacitn.songservice.service.CollectService;
import com.tacitn.songservice.service.SingerService;
import com.tacitn.songservice.service.SongListService;
import com.tacitn.songservice.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
public class CollectServiceImpl extends ServiceImpl<CollectMapper, Collect> implements CollectService {
    @Autowired
    SongService songService;

    @Autowired
    SingerService singerService;

    @Autowired
    SongListService songListService;

    @Override
    public Result addCollect(Collect collect) {
        QueryWrapper<Collect> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("consumer_id", collect.getConsumerId())
                .eq("song_id", collect.getSongId())
                .or().eq("consumer_id", collect.getConsumerId()).
                eq("song_list_id", collect.getSongListId());
        Collect one = getOne(queryWrapper);
        if (one != null) {
            //多点一下就取消收藏
            remove(queryWrapper);
            return Result.ok("取消收藏");
        }
        collect.setCreateTime(LocalDateTime.now());
        boolean flag = save(collect);
        if (flag) {
            if (null != collect.getSongId()) {
                UpdateWrapper<Song> updateWrapper = new UpdateWrapper<>();
                updateWrapper.setSql("liked_count = liked_count + 1").eq("id", collect.getSongId());
                songService.update(updateWrapper);

                Song song = songService.getById(collect.getSongId());
                QueryWrapper<Song> songQueryWrapper = new QueryWrapper<>();
                songQueryWrapper.eq("singer_id", song.getSingerId());
                List<Song> songList = songService.list(songQueryWrapper);
                Long likeCount = 0L;
                Long recmandId = 0L;
                for (Song song1 : songList) {
                    if (song1.getLikedCount() > likeCount) {
                        recmandId = song1.getId();
                        likeCount = song1.getLikedCount();
                    }
                }
                Singer singer = singerService.getById(song.getSingerId());
                singer.setRecmdSongId(recmandId);
                singerService.saveOrUpdate(singer);

            }
            return Result.ok("收藏成功");
        }
        return Result.fail("收藏失败");
    }

    @Override
    public Result getLikeSongs(Long consumerId) {
        JSONObject jsonObject = new JSONObject();
        QueryWrapper<Collect> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("consumer_id", consumerId)
                .orderByAsc("create_time")
                .isNotNull("song_id");
        ArrayList<Long> songIds = new ArrayList<>();
        for (Collect collect : list(queryWrapper)) {
            songIds.add(collect.getSongId());
        }
        if (songIds.size() == 0) {
            songIds.add(0L);
        }
        List<Song> songList = songService.listByIds(songIds);
        if (!songList.isEmpty()){
            jsonObject.set("songList",songList);
            return Result.ok(jsonObject);
        }
        return Result.fail("获取失败");
    }

    @Override
    public Result getCollectSongList(Long consumerId) {
        JSONObject jsonObject = new JSONObject();
        QueryWrapper<Collect> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("consumer_id", consumerId)
                .orderByAsc("create_time")
                .isNotNull("song_list_id");
        ArrayList<Long> songListIds = new ArrayList<>();
        for (Collect collect : list(queryWrapper)) {
            songListIds.add(collect.getSongListId());
        }
        if (songListIds.size() == 0) {
            songListIds.add(0L);
        }
        List<SongList> songListList = songListService.listByIds(songListIds);
        if (!songListList.isEmpty()){
            jsonObject.set("songLists",songListList);
            return Result.ok(jsonObject);
        }
        return Result.fail("获取失败");
    }
}
