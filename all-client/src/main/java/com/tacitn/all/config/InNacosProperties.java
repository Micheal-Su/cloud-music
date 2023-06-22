package com.tacitn.all.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "pattern")
public class InNacosProperties {
    private String dateFormat;
    private String envShareValue;
}
