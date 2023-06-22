package com.tacitn.songservice.service.Impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.benmanes.caffeine.cache.Cache;
import com.tacitn.feign.clients.AllClient;
import com.tacitn.songservice.domain.Collect;
import com.tacitn.songservice.domain.ListSong;
import com.tacitn.songservice.domain.SongList;
import com.tacitn.songservice.dto.Result;
import com.tacitn.songservice.mapper.SongListMapper;
import com.tacitn.songservice.service.AsyncService;
import com.tacitn.songservice.service.CollectService;
import com.tacitn.songservice.service.ListSongService;
import com.tacitn.songservice.service.SongListService;
import com.tacitn.songservice.utils.FileUtil;
//import io.seata.spring.annotation.GlobalTransactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import static com.tacitn.songservice.utils.Const.*;

@Service
public class SongListServiceImpl extends ServiceImpl<SongListMapper, SongList> implements SongListService {
    @Autowired
    SongListMapper songMapper;

    @Autowired
    AllClient allClient;

    @Autowired
    Cache<Long,List<SongList>> songListOfConsumerCache;

    @Autowired
    AsyncService asyncService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    ListSongService listSongService;

    @Autowired
    CollectService collectService;


    Logger logger = LoggerFactory.getLogger(SongListServiceImpl.class);
    @Override
    public List<SongList> selectAllPublished() {
        String key = REDIS_PUBLISHED_SONG_LIST_PREFIX;
        String publishedList = stringRedisTemplate.opsForValue().get(key);
        if (StrUtil.isNotBlank(publishedList)){
            List<SongList> list = JSONUtil.toList(publishedList, SongList.class);
            logger.warn("get songList from redis");
            return list;
        }
//        这里不是数据库和缓存异步，只是cacheAllPublished和它这行下面的代码异步而已
//        因为数据库和缓存操作本来就是要有先后顺序要求的。
        List<SongList> songLists = query().eq("published", 1).list();
        asyncService.cacheAllPublished(key, JSONUtil.toJsonStr(songLists));
        return songLists;
    }

    @Override
    //一个使用了@Transactional 的方法，如果方法内包含多线程的使用，方法内部出现异常，不会回滚线程中调用方法的事务。
    public Boolean updateSongListMsg(SongList songList) {
        boolean update = saveOrUpdate(songList);
        asyncService.deleteCacheAllPublished();
        return update;
    }

    @Override
    public Result updateSongListPic(MultipartFile avatarFile, Long id) {
        SongList songList = getById(id);
        String songListPic = songList.getPic();
//        若图片不是默认图片，就删除
        if (!(songListPic.equals(FileUtil.getSongListPicInProject()))) {
            File songListPicFile = new File(FileUtil.getProjectDir() + songListPic);
            songListPicFile.delete();
        }

        try {
            boolean flag = FileUtil.savePic(songList, avatarFile);
            if (flag) {
                boolean update = saveOrUpdate(songList);
                if (update) {
                    asyncService.deleteCacheAllPublished();
                    return Result.ok(songList.getPic());
                }
            }
            return Result.fail("上传失败");
        } catch (IOException e) {
            return Result.fail("上传失败");
        }
    }

    @Override
    public boolean deleteSongList(Long[] ids) {
        List<SongList> songListList = listByIds(Arrays.asList(ids));
        //       删除歌单与歌曲对应数据
        QueryWrapper<ListSong> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("song_list_id", Arrays.asList(ids));
        listSongService.remove(queryWrapper);

        //      将歌单从用户的收藏列表中删除
        QueryWrapper<Collect> clQueryWrapper = new QueryWrapper<>();
        clQueryWrapper.in("song_list_id", Arrays.asList(ids));
        collectService.remove(clQueryWrapper);

        boolean hasOnePublished = false;
        for (SongList songList : songListList) {
            if (songList.getPublished()) {
                hasOnePublished = true;
            }
            String songListPic = songList.getPic();
            if (!songListPic.equals(FileUtil.getSongListPicInProject())) {
                File songListPicFile = new File(FileUtil.getProjectDir() + songListPic);
                songListPicFile.delete();
            }
        }
        if (hasOnePublished) {
            // 如果删除的歌单里有一个是公开的，清除redis里的公开歌单数据
            asyncService.deleteCacheAllPublished();
        }
        return removeByIds(Arrays.asList(ids));
    }

    @Override
//    @GlobalTransactional // seata分布式事务,经测试有效
    public Result addSongList(SongList songList) {
        songList.setPic(FileUtil.getSongListPicInProject());
        songList.setSongNum(0);
        songList.setCreateTime(new Date());
        boolean flag1 = true;
        if (songList.getCreatorId() > 0){
            flag1 = allClient.setSongList(songList.getCreatorId(), -1);
        }
        boolean flag2 = save(songList);
        if (flag1 && flag2) {
            if (songList.getPublished()) {
                // 如果也是公开的歌单，则删除redis上的歌单缓存，等有下一个人查询所有歌单时，自然会将最新的存到redis上
                asyncService.deleteCacheAllPublished();
                // 删除该用户创建歌单的本地缓存
                songListOfConsumerCache.invalidate(songList.getCreatorId());
            }
            return Result.ok("添加成功");
        }
        return Result.fail("添加失败");
    }

    /**
     * 查到了就存至进程缓存
     * @param consumerId
     * @return
     */
    @Override
    public List<SongList> getSongListByCreator(Long consumerId) {
        return songListOfConsumerCache.get(consumerId,
                key -> query().eq("creator_id", consumerId).list());
    }
}
