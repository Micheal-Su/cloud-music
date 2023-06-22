package com.tacitn.songservice.config;

import com.tacitn.songservice.domain.vo.ESConfigProperties;
import com.tacitn.songservice.domain.vo.ESPoolProperties;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.http.HttpHost;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
//@EnableConfigurationProperties注解可以将使用@ConfigurationProperties注解对应的类加入Spring容器管控
//也可以直接使用@Component将@ConfigurationProperties注解对应的类加入Spring容器管控
@EnableConfigurationProperties({ESConfigProperties.class,ESPoolProperties.class}) // 可以是数组
public class ESConfig implements InitializingBean {

    @Autowired
    private ESConfigProperties esConfigProperties;

    @Autowired
    private ESPoolProperties esPoolProperties;

    // 配置类没提供时使用的默认值
    public static String ES_INDEX = "";
    public static int ES_CONNECTION_REQUEST_TIMEOUT = -1;
    public static int ES_CONNECT_TIMEOUT = -1;
    public static int ES_SOCKET_TIMEOUT = -1;

    public static int MAX_TOTAL = 8;
    public static int MAX_IDLE = 8;
    public static int MIN_IDLE = 1;


    @Override
    public void afterPropertiesSet() {
        ES_INDEX = esConfigProperties.getIndex();
        ES_CONNECTION_REQUEST_TIMEOUT = esConfigProperties.getConnectionRequestTimeout();
        ES_CONNECT_TIMEOUT = esConfigProperties.getConnectTimeout();
        ES_SOCKET_TIMEOUT = esConfigProperties.getSocketTimeout();

        MAX_TOTAL = esPoolProperties.getMaxTotal();
        MAX_IDLE = esPoolProperties.getMaxIdle();
        MIN_IDLE = esPoolProperties.getMinIdle();
    }

    @Bean
    public GenericObjectPoolConfig config() {
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        poolConfig.setMinIdle(ESConfig.MIN_IDLE);
        poolConfig.setMaxTotal(ESConfig.MAX_TOTAL);
        poolConfig.setMaxIdle(ESConfig.MAX_IDLE);
        poolConfig.setJmxEnabled(false);
        return poolConfig;
    }

    @Bean
    public GenericObjectPool<RestHighLevelClient> pool(
            PooledObjectFactory<RestHighLevelClient> factory,
            GenericObjectPoolConfig config) {
        return new GenericObjectPool<>(factory, config);
    }

    @Bean
    public PooledObjectFactory<RestHighLevelClient> factory() {
        return new BasePooledObjectFactory<RestHighLevelClient>() {

            @Override
            public void destroyObject(PooledObject<RestHighLevelClient> pooledObject) throws Exception {
                RestHighLevelClient highLevelClient = pooledObject.getObject();
                highLevelClient.close();
            }

            @Override
            public RestHighLevelClient create() {

                RestHighLevelClient highLevelClient = new RestHighLevelClient(RestClient.builder(new HttpHost("47.115.203.67", 9200, "http"))
                        .setRequestConfigCallback(
                                requestConfigBuilder -> {
                                    requestConfigBuilder.setConnectTimeout(ESConfig.ES_CONNECT_TIMEOUT);
                                    requestConfigBuilder.setSocketTimeout(ESConfig.ES_SOCKET_TIMEOUT);
                                    requestConfigBuilder.setConnectionRequestTimeout(ESConfig.ES_CONNECTION_REQUEST_TIMEOUT);
                                    return requestConfigBuilder;
                                }
                        ).setHttpClientConfigCallback(
                                httpAsyncClientBuilder -> httpAsyncClientBuilder.setKeepAliveStrategy((httpResponse, httpContext)
                                        -> TimeUnit.MINUTES.toMillis(3))
                                        .setDefaultIOReactorConfig(IOReactorConfig.custom()
                                                .setSoKeepAlive(true).build()))
                );
                return highLevelClient;
            }

            @Override
            public PooledObject<RestHighLevelClient> wrap(RestHighLevelClient restHighLevelClient) {
                return new DefaultPooledObject<>(restHighLevelClient);
            }
        };
    }
}

