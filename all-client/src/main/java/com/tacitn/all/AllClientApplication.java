package com.tacitn.all;

import com.tacitn.feign.clients.OtherClient;
import com.tacitn.feign.config.DefaultFeignConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author DongJiShiLiu
 * @create 2023/4/22 23:12
 */
@SpringBootApplication
@EnableFeignClients(clients = {OtherClient.class},defaultConfiguration = DefaultFeignConfiguration.class)
public class AllClientApplication {
    public static void main(String[] args) {
        SpringApplication.run(AllClientApplication.class, args);
    }

}
