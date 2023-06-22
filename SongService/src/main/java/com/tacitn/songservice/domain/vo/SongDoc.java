package com.tacitn.songservice.domain.vo;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.tacitn.songservice.domain.Song;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

/**
 * ES 与 mysql对应表
 */
@Data
@NoArgsConstructor
public class SongDoc {
    private Long id;

    private Long singerId;

    private String singerName; // 放入自动补全suggestion
    // 不包含歌手的歌名
    private String songName; // 放入自动补全suggestion
    // 歌曲介绍
    private String introduction;
    // 专辑
    private String album; // 放入自动补全suggestion

    @JsonFormat(timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(timezone = "GMT+8")
    private Date updateTime;

    private String pic;

    private String lyric;

    private String url;

    private String cloudUrl;

    private String beyondApp;

    private String musicUid;

    private Long uploaderId;

    private Long likedCount;

    private List<String> suggestion;

    // 是否置顶
    private Boolean toTop;

    public SongDoc(Song song){
        this.id = song.getId();
        this.songName = song.getSongName();
        this.singerName = song.getSingerName();
        this.singerId = song.getSingerId();
        this.introduction = song.getIntroduction();
        this.album = song.getAlbum();
        this.likedCount = song.getLikedCount();
        this.uploaderId = song.getUploaderId();
        this.musicUid = song.getMusicUid();
        this.beyondApp = song.getBeyondApp();
        this.url = song.getUrl();
        this.cloudUrl = song.getCloudUrl();
        this.lyric = song.getLyric();
        this.pic = song.getPic();
        this.updateTime = song.getUpdateTime();
        this.createTime = song.getCreateTime();
        // 若单首歌由多个歌手合作，则切割后再存入自动补全suggestion
        if (this.singerName.contains(",")){
            String[] arr = this.singerName.split(",");
            this.suggestion = new ArrayList<>();
            this.suggestion.add(this.songName);
            if (!"".equals(this.album)){
                this.suggestion.add(this.album);
            }
            Collections.addAll(this.suggestion, arr);
        }else {
//            this.suggestion = Arrays.asList(this.songName,this.singerName);
            this.suggestion = new ArrayList<>();
            this.suggestion.add(this.songName);
            this.suggestion.add(this.singerName);
            if (!"".equals(this.album)){
                this.suggestion.add(this.album);
            }
        }
    }

}
