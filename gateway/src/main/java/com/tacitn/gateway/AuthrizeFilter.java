package com.tacitn.gateway;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.tacitn.feign.clients.AllClient;
import com.tacitn.feign.domain.Admin;
import com.tacitn.feign.domain.Consumer;
import com.tacitn.feign.domain.dto.Result;
import com.tacitn.gateway.utils.PathUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import javax.annotation.Resource;
import java.util.Map;

import static com.tacitn.gateway.utils.Consts.*;


/**
 * @author DongJiShiLiu
 * @create 2023/4/24 23:40
 */

@Order(-1) // 注解和实现接口的方法都可以,值越小优先级越高
@Component
public class AuthrizeFilter implements GlobalFilter {
    @Resource //这些注解只有在容器里的对象才能使用，可以通过构造方法从Configuration类传过来
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private AllClient allClient;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        String path = request.getURI().getPath();
        if (PathUtils.pass(path)){
            return chain.filter(exchange);
        }

        HttpHeaders headers = request.getHeaders();
        String token = headers.getFirst("token");
        if (StrUtil.isNotBlank(token)){
            boolean flag = verifyToken(token);
            if (flag){
                // 放行

                return chain.filter(exchange);
            }
        }
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

//    @Override
//    public int getOrder() {
//        return -1;
//    }

    public boolean verifyToken(String token ){
        // 执行认证
        if (StrUtil.isBlank(token)) {
            return false;
        }

        String key = REDIS_LOGIN_TOKEN_PREFIX + token;
        String tokenExists = stringRedisTemplate.opsForValue().get(key);

        if (StrUtil.isBlank(tokenExists)) {
            String adminKey = REDIS_ADMIN_TOKEN_PREFIX + token;
            String adminExists = stringRedisTemplate.opsForValue().get(adminKey);

            if (StrUtil.isBlank(adminExists)) {
                return false;
            } else {
                String adminId;
                try {
                    adminId = JWT.decode(token).getAudience().get(0); //得到token中的userid载荷
                } catch (JWTDecodeException j) {
                    return false;
                }
                Admin admin = allClient.getAdminById(Long.valueOf(adminId));
                if (admin == null) {
                    return false;
                }
                JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(admin.getPassword() + REDIS_ADMIN_TOKEN_PW_SUFFIX)).build();
                try {
                    jwtVerifier.verify(token);
                } catch (JWTVerificationException e) {
                    e.printStackTrace();
                    return false;
                }
                return true;
            }
        } else {
            String consumerId;
            try {
                consumerId = JWT.decode(token).getAudience().get(0); //得到token中的userid载荷
            } catch (JWTDecodeException j) {
                return false;
            }
            Consumer consumer = allClient.getConsumerAll(consumerId);

            if (BeanUtil.isEmpty(consumer)){
                return false;
            }
            // 用户密码加签验证 token
            JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(consumer.getPassword() + REDIS_LOGIN_TOKEN_PW_SUFFIX)).build();
            try {
                jwtVerifier.verify(token);
            } catch (JWTVerificationException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
    }
}
