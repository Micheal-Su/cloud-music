package com.tacitn.gateway.utils;

/**
 * @author DongJiShiLiu
 * @create 2023/5/2 15:42
 */
public class PathUtils {
    public static boolean pass(String uri) {
        // 放行的
        if (uri.contains("/login") || uri.contains("/logout")
                || uri.contains("/song") || uri.contains("/singer")
                || uri.contains("/listSong") || uri.contains("/songList")
                || uri.contains("/tag") || uri.contains("/playTimes")
                || uri.contains("/comment") || uri.contains("/conComUp")
                || uri.contains("/conPossession")|| uri.contains("/admin")
                || uri.contains("/consumer") || uri.contains("/signUp")) {
            // 拦截的
            if (false) {
                return false;
            }
        }
         return true;
    }
    public static boolean refresh(String uri) {
        if (uri.contains("/conPossession") || uri.contains("/song")
                || uri.contains("/singer") || uri.contains("/voucher")
                || uri.contains("/admin") || uri.contains("/consumer")) {

            // 排除的
            if (uri.contains("/login") || uri.contains("/logout")
                ||uri.contains("/getSuggest") || uri.contains("/getAggregation")
            ) {
                System.out.println("不刷新token有效期");
                return false;
            }

            System.out.println("刷新token有效期");
            return true;
        }
        return false;
    }
}
