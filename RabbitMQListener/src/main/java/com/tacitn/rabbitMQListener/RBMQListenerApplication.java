package com.tacitn.rabbitMQListener;

import com.tacitn.feign.clients.AllClient;
import com.tacitn.feign.clients.SongClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author DongJiShiLiu
 * @create 2023/4/25 19:07
 */
@SpringBootApplication
@EnableFeignClients(clients = {AllClient.class, SongClient.class})
public class RBMQListenerApplication {
    public static void main(String[] args) {
        SpringApplication.run(RBMQListenerApplication.class, args);
    }
}
