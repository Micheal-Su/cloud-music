package com.tacitn.all.controller;

import com.tacitn.all.domain.Admin;
import com.tacitn.all.dto.Result;
import com.tacitn.all.service.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author DongJiShiLiu
 * @create 2022/11/3 21:01
 */
@Slf4j
@RestController // Controller + ResponseBody
@RequestMapping("/admin")
public class AdminController {
//    @Autowired
//    JdbcTemplate jdbcTemplate;//用户进行一些无法将返回结果封装在Mybatis-Plus Vo类中的操作

    @Autowired//这里找到的实际上是实现类而不是接口
    private AdminService adminService;

    @GetMapping("/getAdminById/{id}")
    public Admin getAdminById(@PathVariable("id") Long id){
        log.error("aaa");
        return adminService.getById(id);
    }

    @PostMapping("/logout")
    public Result logout(@RequestParam("token") String token){
        return adminService.logout(token);
    }

    @PostMapping("/login")
    public Result loginStatus(@RequestBody Admin admin) {
        return adminService.checkUser(admin );
    }
}
