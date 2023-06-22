package com.tacitn.feign.config;

import com.tacitn.feign.clients.fallback.AllClientFallbackFactory;
import feign.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DefaultFeignConfiguration {
    @Bean
    public Logger.Level logLevel(){
        return Logger.Level.BASIC;
    }

    @Bean
    public AllClientFallbackFactory fallbackFactory(){
        return new AllClientFallbackFactory();
    }
}
