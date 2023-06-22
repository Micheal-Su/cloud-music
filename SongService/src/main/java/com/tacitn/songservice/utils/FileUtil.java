package com.tacitn.songservice.utils;

import com.tacitn.songservice.domain.Singer;
import com.tacitn.songservice.domain.Song;
import com.tacitn.songservice.domain.SongList;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public class FileUtil {

    // 保存文件的目录
    final static String property = "/usr/local/src/app/music-springcloud/allproject/files/";

    public static String lastName(MultipartFile file) {
        if (file == null) return null;
        String filename = file.getOriginalFilename();
        if (filename.lastIndexOf(".") == -1) {
            return "";//文件没有后缀名的情况
        }
        //此时返回的是带有 . 的后缀名，
        return filename.substring(filename.lastIndexOf("."));

        //return filename.subString(filename.lastIndexOf(".")+1);// 这种返回的是没有.的后缀名

        // 下面这种如果对于String类型可能有问题，如 以.结尾的字符串，会报错。但是文件没有以点结尾的
    }

    public static void saveOrUpdateSongFile(Song song, MultipartFile music) throws IOException {

        if (song.getUrl() != null) {
            String songName = song.getUrl();
            File songFile = new File(FileUtil.getSongAbsoluteDir() + songName.substring(songName.lastIndexOf(FileUtil.getSeparator()) + 1));
            songFile.delete();
        }
        // 使用md5加密，一是加密，二是去除中文和空格等，防止出意外
        String fileName = System.currentTimeMillis() + MD5Utils.encrypByMd5(music.getOriginalFilename());
        String filePath = FileUtil.getSongAbsoluteDir();
        File file1 = new File(filePath);
        if (!file1.exists()) {
            file1.mkdir();
        }

        File dest = new File(filePath + fileName);
        String storeUrlPath = FileUtil.getSongDirInProject() + fileName;
        song.setUrl(storeUrlPath);
        music.transferTo(dest);
    }


    public static <T> boolean savePic(T obj, MultipartFile Pic) throws IOException {
        String filePath = "";
        String objClassName = obj.getClass().getSimpleName();
        String fileName = "";
        // 后缀
        String lastName = lastName(Pic);
        // 文件名就是每个对象的Id，放在各自的目录下
        if ("Song".equals(objClassName)) {
            filePath = getSongPicAbsoluteDir();
            Song song = (Song) obj;
            // 图片名使用md5加密，一是加密，二是去除中文和空格等，防止出意外
            fileName = MD5Utils.encrypByMd5(song.getSongName() + song.getSingerName());
            String storeAvatarPath = FileUtil.getSongPicDirInProject() + fileName + lastName;
            song.setPic(storeAvatarPath);
        } else if ("Singer".equals(objClassName)) {
            filePath = getSingerPicAbsoluteDir();
            Singer singer = (Singer) obj;
            // 图片名使用md5加密，一是加密，二是去除中文和空格等，防止出意外
            fileName = MD5Utils.encrypByMd5(singer.getName());
            String storeAvatarPath = FileUtil.getSingerPicDirInProject() + fileName + lastName;
            singer.setPic(storeAvatarPath);
        }else if ("SongList".equals(objClassName)){
            filePath = getSongListPicAbsoluteDir();
            SongList songList = (SongList) obj;
            // 图片名使用md5加密，一是加密，二是去除中文和空格等，防止出意外
            fileName = MD5Utils.encrypByMd5(songList.getTitle() + songList.getCreatorId()) + LocalDateTime.now();
            String storeAvatarPath = FileUtil.getSongListPicDirInProject() + fileName + lastName ;
            songList.setPic(storeAvatarPath);
        }

        File file1 = new File(filePath);
        if (!file1.exists()) {
            file1.mkdir();
        }
        File dest = new File(filePath + fileName + lastName);
        Pic.transferTo(dest);
        return true;
    }


    //    删除歌曲相关文件
    public static void deleteSongFile(List<Song> songList) {
        for (Song song : songList) {
            String picName = song.getPic();
            String demoPicName = FileUtil.getSongPicInProject();
            if (!(picName.equals(demoPicName))) {
                picName = picName.substring(picName.lastIndexOf(FileUtil.getSeparator()) + 1);
                File picFile = new File(FileUtil.getSongPicAbsoluteDir() + picName);
                picFile.delete();
            }
            String songUrl = song.getUrl();
            File songFile = new File(FileUtil.getSongAbsoluteDir() + songUrl.substring(songUrl.lastIndexOf(FileUtil.getSeparator()) + 1));
            songFile.delete();
        }
    }


    public static String getSeparator() {
        return System.getProperty("file.separator");
    }

    public static String getProjectDir() {
        return property;
    }

    //    图片绝对路径
    public static String getSongPicAbsoluteDir() {
        return property + "img" + System.getProperty("file.separator") + "music" + System.getProperty("file.separator")
                + "songPic" + System.getProperty("file.separator");
    }

    //    项目之后，图片所在文件夹
    public static String getSongPicDirInProject() {
        return  "img" + System.getProperty("file.separator")+ "music" + System.getProperty("file.separator")
                + "songPic" + System.getProperty("file.separator");
    }

    //    项目之后，图片路径
    public static String getSongPicInProject() {
        return "img" + System.getProperty("file.separator")+ "music" + System.getProperty("file.separator")
                + "songPic" + System.getProperty("file.separator") + "song.png";
    }

    //====================> mp3文件
    public static String getSongDirInProject() {
        return  "song" + System.getProperty("file.separator");
    }

    public static String getSongAbsoluteDir() {
        return property + "song" + System.getProperty("file.separator");
    }


    //    ======================> 歌单
    //    图片绝对路径
    public static String getSongListPicAbsoluteDir() {
        return property + "img" + System.getProperty("file.separator")+ "music" + System.getProperty("file.separator") +
                "songListPic" + System.getProperty("file.separator");
    }

    //    项目之后，图片所在文件夹
    public static String getSongListPicDirInProject() {
        return "img" + System.getProperty("file.separator")+ "music" + System.getProperty("file.separator")
                + "songListPic" + System.getProperty("file.separator");
    }

    //    项目之后，图片路径
    public static String getSongListPicInProject() {
        return "img" + System.getProperty("file.separator")+ "music" + System.getProperty("file.separator") + "songListPic" + System.getProperty("file.separator")
                + "songList.png";
    }

    public static String getSingerPicAbsoluteDir() {
        return property + "img" + System.getProperty("file.separator")+ "music" + System.getProperty("file.separator")
                + "singerPic" + System.getProperty("file.separator");
    }


    public static String getSingerPicDirInProject() {
        return   "img" + System.getProperty("file.separator")+ "music" + System.getProperty("file.separator")
                + "singerPic" + System.getProperty("file.separator");
    }

    public static String getSingerPicInProject() {
        return   "img" + System.getProperty("file.separator") + "music" + System.getProperty("file.separator")
                + "singerPic" + System.getProperty("file.separator") + "singer.png";
    }

}
