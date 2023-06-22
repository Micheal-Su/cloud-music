package com.tacitn.all.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tacitn.all.domain.Admin;
import org.apache.ibatis.annotations.Mapper;


//垃圾MapperScan注解失效了，自己手动加Mapper吧
@Mapper
//使用mybatis-plus在主程序中此类已经被加入容器了，不需要写@Repository，@Mapper等注解
//想这种简单几个方法就行的都不需要写Mapper.xml文件
public interface AdminMapper extends BaseMapper<Admin> {

}
