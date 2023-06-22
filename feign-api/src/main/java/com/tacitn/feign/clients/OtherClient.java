package com.tacitn.feign.clients;

import com.tacitn.feign.domain.Other;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author DongJiShiLiu
 * @create 2023/4/24 3:40
 */
@FeignClient("otherclient")
public interface OtherClient {
    @GetMapping("/other/{id}")
    Other getOtherById(@PathVariable("id") Long id);
}
