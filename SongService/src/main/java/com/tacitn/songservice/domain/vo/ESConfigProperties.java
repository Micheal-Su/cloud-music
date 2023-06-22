package com.tacitn.songservice.domain.vo;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "es.config")
public class ESConfigProperties {
    private String index;
    private Integer connectionRequestTimeout;
    private Integer socketTimeout;
    private Integer connectTimeout;
}
