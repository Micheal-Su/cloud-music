package com.tacitn.songservice.domain.vo;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@ConfigurationProperties(prefix = "es.pool")
public class ESPoolProperties {
    private Integer maxTotal;
    private Integer maxIdle;
    private Integer minIdle;
}
