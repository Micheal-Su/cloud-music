package com.tacitn.songservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class FileConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        final String  property =  System.getProperty("user.dir")+ System.getProperty("file.separator")
                + "files"+ System.getProperty("file.separator");
//        歌曲地址
        registry.addResourceHandler("/song/**")
                .addResourceLocations("file:" + property
                        + "song" + System.getProperty("file.separator"));

    }
}
