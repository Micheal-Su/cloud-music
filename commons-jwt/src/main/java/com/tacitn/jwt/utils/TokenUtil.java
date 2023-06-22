package com.tacitn.jwt.utils;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import static com.tacitn.jwt.utils.Consts.REDIS_ADMIN_TOKEN_PW_SUFFIX;
import static com.tacitn.jwt.utils.Consts.REDIS_LOGIN_TOKEN_PW_SUFFIX;

public class TokenUtil {

    /**
     * 生成Token
     * @return
     */
    public static String getConsumerToken(Long id,String password){   //以password作为签名
        return JWT.create().withAudience(String.valueOf(id)) // 将 conusmer id 保存到 token 里面.作为载荷
//                .withExpiresAt(DateUtil.offsetHour(new Date(),2)) //使用huttool里的util设置两小时过期,没用，维护还费劲，直接利用redis管理过期时间
                .sign(Algorithm.HMAC256(password + REDIS_LOGIN_TOKEN_PW_SUFFIX)); // 以 password + @ 作为 token 的密钥
    };

    public static String getAdminToken(Long id,String password){   //以password作为签名
        return JWT.create().withAudience(String.valueOf(id)) // 将 conusmer id 保存到 token 里面.作为载荷
//                .withExpiresAt(DateUtil.offsetHour(new Date(),2)) //使用huttool里的util设置两小时过期
                .sign(Algorithm.HMAC256(password + REDIS_ADMIN_TOKEN_PW_SUFFIX)); // 以 password + @ 作为 token 的密钥
    };

}
