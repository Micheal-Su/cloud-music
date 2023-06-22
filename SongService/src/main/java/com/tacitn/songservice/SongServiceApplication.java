package com.tacitn.songservice;

import com.tacitn.feign.clients.AllClient;
import com.tacitn.feign.config.DefaultFeignConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author DongJiShiLiu
 * @create 2023/4/26 20:09
 */

// com.tacitn.feign有Bean有放入容器，要么@EnableFeignClients指定defaultConfiguration
//    要么让springboot扫描Bean所在的包
//@SpringBootApplication(scanBasePackages = {"com.tacitn.feign","com.tacitn.songservice"})
@SpringBootApplication()
@EnableFeignClients(clients = {AllClient.class},defaultConfiguration = DefaultFeignConfiguration.class)
public class SongServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SongServiceApplication.class, args);

    }
}
