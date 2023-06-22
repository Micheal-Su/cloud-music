package com.tacitn.all.service;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tacitn.all.domain.Admin;
import com.tacitn.all.dto.Result;

public interface AdminService extends IService<Admin> {
    Result logout(String token);
    //继承IService接口可以自动继承很多default的基础方法，AdminService实现类继承IService的实现类也就继承了很多基础方法
    Result checkUser(Admin admin);
}
