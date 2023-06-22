package com.tacitn.songservice.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.benmanes.caffeine.cache.Cache;
import com.tacitn.songservice.domain.ListSong;
import com.tacitn.songservice.domain.Singer;
import com.tacitn.songservice.domain.Song;
import com.tacitn.songservice.dto.Result;
import com.tacitn.songservice.mapper.SingerMapper;
import com.tacitn.songservice.service.ListSongService;
import com.tacitn.songservice.service.SingerService;
import com.tacitn.songservice.service.SongService;
import com.tacitn.songservice.utils.FileUtil;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author DongJiShiLiu
 * @create 2022/11/3 20:51
 */
@Service
public class SingerServiceImpl extends ServiceImpl<SingerMapper, Singer> implements SingerService {

    @Autowired
    private SongService songService;

    @Autowired
    private Cache<String, List<Singer>> singerListCache;

    @Autowired
    private ListSongService listSongService;


    @Override
    public List<Singer> getAll() {
        /**
         * 查找recmd（推荐的歌手）缓存,有则返回，无则查数据库后赋值
         * 这里并没有所谓的推荐歌手，就是查询所有歌手而已
         */
        return singerListCache.get("recmd", key -> query().list());
    }

    @Override
    public boolean updateSongData(Singer singer) {
        return false;
    }

    @Override
    public boolean deleteSinger(Long id) {
        //        删除歌手图片
//        如果不是默认图片，则删除
        String singerPic = getById(id).getPic();
        if (!(singerPic.equals(FileUtil.getSingerPicInProject()))) {
            File singerPicFile = new File(FileUtil.getProjectDir() + singerPic);
            singerPicFile.delete();
        }

        //        删除数据库相关歌曲
        QueryWrapper<Song> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("singer_id", id);
        List<Song> songList = songService.list(queryWrapper);

        songService.remove(queryWrapper);
        //        将相关歌曲从各个歌单中删除
        for (Song song:songList){
            QueryWrapper<ListSong> listSongQueryWrapper = new QueryWrapper<>();
            listSongQueryWrapper.eq("song_id", song.getId());
            listSongService.remove(listSongQueryWrapper);
        }
        //        删除相关歌曲文件
        FileUtil.deleteSongFile(songList);
        boolean remove = removeById(id);
        if (remove){
            // 删除本地缓存
            singerListCache.invalidate("recmd");
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteSelectedSingers(Long[] ids) {
        List<Singer> singerList = listByIds(Arrays.asList(ids));
        for (Singer singer:singerList){
            //        删除歌手图片
            String singerPic = getById(singer.getId()).getPic();
//            如果不是默认图片，则删除
            if (!(singerPic.equals(FileUtil.getSingerPicInProject()))) {
                File singerPicFile = new File(FileUtil.getProjectDir() + singerPic);
                singerPicFile.delete();
            }
        }
        QueryWrapper<Song> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("singer_id", ids);
        List<Song> songList = songService.list(queryWrapper);
        FileUtil.deleteSongFile(songList);
        songService.remove(queryWrapper);
        singerListCache.invalidate("recmd");
        return removeByIds(Arrays.asList(ids));
    }

    @Override
    public Result updateSingerPic(MultipartFile avatarFile, Long id) {
        Singer singer = getById(id);
        String singerPic = singer.getPic();
//        若图片不是默认图片，就删除
        if (!(FileUtil.getSingerPicInProject().equals(singerPic))) {
            File singerPicFile = new File(FileUtil.getProjectDir() + singerPic);
            singerPicFile.delete();
        }

        try {
            boolean flag = FileUtil.savePic(singer, avatarFile);
            if (flag) {
                saveOrUpdate(singer);
                singerListCache.invalidate("recmd");
                return Result.ok();
            } else {
                return Result.fail("更新失败");
            }
        } catch (IOException e) {
            return Result.fail("更新失败");
        } finally {
            return Result.ok();
        }
    }

}
