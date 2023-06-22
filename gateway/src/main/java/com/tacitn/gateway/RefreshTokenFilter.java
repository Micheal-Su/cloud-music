package com.tacitn.gateway;

import cn.hutool.core.util.StrUtil;
import com.tacitn.gateway.utils.PathUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import javax.annotation.Resource;
import static com.tacitn.gateway.utils.Consts.*;

/**
 * @author DongJiShiLiu
 * @create 2023/5/3 19:02
 * 刷新token用的
 */

@Order(-2) // 注解和实现接口的方法都可以
@Component
public class RefreshTokenFilter implements GlobalFilter {

    @Resource //这些注解只有在容器里的对象才能使用，可以通过构造方法从Configuration类传过来
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();

        String path = request.getURI().getPath();
        if (!PathUtils.refresh(path)){
            // 不刷新token的请求，频繁刷新浪费资源
            return chain.filter(exchange);
        }
        HttpHeaders headers = request.getHeaders();
        String token = headers.getFirst("token");
        if (StrUtil.isBlank(token)) {
            // 没登录自然不用刷新
            return chain.filter(exchange);
        }

        // 基于TOKEN获取redis中的用户
        String tokenkey = REDIS_LOGIN_TOKEN_PREFIX + token;
        String tokenExists = stringRedisTemplate.opsForValue().get(tokenkey);

        if (StrUtil.isNotBlank(tokenExists)) {
            // 刷新token有效期
            stringRedisTemplate.expire(tokenkey, REDIS_LOGIN_TOKEN_TIME, REDIS_LOGIN_TOKEN_TIME_UNIT);
        }
        else{
            String adminKey = REDIS_ADMIN_TOKEN_PREFIX + token;
            String adminExist = stringRedisTemplate.opsForValue().get(adminKey);
            if (adminExist != null && !"".equals(adminExist)) {
                // 刷新token有效期
                stringRedisTemplate.expire(adminKey, REDIS_ADMIN_TOKEN_TIME, REDIS_ADMIN_TOKEN_TIME_UNIT);
            }
        }
        // 放行
        return chain.filter(exchange);
    }
}
