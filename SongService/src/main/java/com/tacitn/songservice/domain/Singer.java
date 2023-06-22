package com.tacitn.songservice.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Singer {
    @TableId(type = IdType.AUTO)//声明数据库中为自增的，这样将实体类添加进数据库表时就不需要setId()了
    private Long id;
    private String name;
    private Integer sex;
    private String pic;
    private Long recmdSongId;//推荐歌曲id
    @TableField(exist = false)
    private String recmdSongName;//推荐歌曲名，给前端用而已

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")//使前端读取的时候不会少8个小时，不过这样就只能是JSON格式的数据了
    @DateTimeFormat(pattern = "yyyy-MM-dd")//将前端传过来的String类型的日期转化为此格式的Date类型
    private Date birth;                //如果是以参数形式传过来的 可以将yyyy-MM-dd HH:mm:ss.SSSX 转为yyyy-MM-dd
                                       //但是如果是传过来JSON,只能将yyyy-MM-dd格式的String类型的的数据转成Date
//    但是，但是，存到数据库Date长度还是那么长，只不过-dd后面全变成0而已，如2022-12-02 00:00:00
    private String location;
    private String introduction;

}
