package com.tacitn.jwt;

import com.tacitn.feign.clients.AllClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author DongJiShiLiu
 * @create 2023/5/2 17:17
 */
@SpringBootApplication
public class CommonsJWTApplicaiton {
    public static void main(String[] args) {
        SpringApplication.run(CommonsJWTApplicaiton.class, args);
    }
}
