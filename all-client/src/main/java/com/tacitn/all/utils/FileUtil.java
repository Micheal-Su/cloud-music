package com.tacitn.all.utils;

import com.tacitn.all.domain.Consumer;
import com.tacitn.all.domain.Dynamic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * @author DongJiShiLiu
 * @create 2022/11/10 14:22
 */
public class FileUtil {
    final static String property = "/usr/local/src/app/music-springcloud/files/img/all/";
//

    public static String lastName(MultipartFile file) {

        if (file == null) return null;
        String filename = file.getOriginalFilename();
        if (filename.lastIndexOf(".") == -1) {
            return "";//文件没有后缀名的情况
        }
        //此时返回的是带有 . 的后缀名，
        return filename.substring(filename.lastIndexOf("."));

        //return filename.subString(filename.lastIndexOf(".")+1);// 这种返回的是没有.的后缀名

        // 下面这种如果对于String类型可能有问题，如 以.结尾的字符串，会报错。但是文件没有以点结尾的
    }

    public static boolean savePic(Object obj, MultipartFile Pic) throws IOException {
        String filePath = "";
        String objClassName = obj.getClass().getSimpleName();
        boolean instance = obj.getClass().isInstance(Consumer.class);
        String fileName = "";
        // 后缀
        String lastName = lastName(Pic);
        if ("Consumer".equals(objClassName)) {
            filePath = getConsumerPicAbsoluteDir();
            Consumer consumer = (Consumer) obj;
            fileName = MD5Utils.encrypByMd5(consumer.getId() + consumer.getUsername());
            String storeAvatorPath = FileUtil.getConsumerPicDirInProject() + fileName + lastName;
            consumer.setAvatar(storeAvatorPath);
        } else if ("Dynamic".equals(objClassName)) {
            filePath = getDynamicPicAbsoluteDir();
            Dynamic dynamic = (Dynamic) obj;
            fileName = MD5Utils.encrypByMd5(dynamic.getConsumerId()+ dynamic.getTitle() + dynamic.getCreateTime().toString() );
            String storeAvatorPath = FileUtil.getDynamicPicDirInProject() + fileName + lastName;
            dynamic.setImages(storeAvatorPath);
        }

        File file1 = new File(filePath);
        if (!file1.exists()) {
            file1.mkdir();
        }
        File dest = new File(filePath + fileName + lastName);
        Pic.transferTo(dest);//方法底层会自动关闭流
        return true;

    }


    public static String getSeparator() {
        return System.getProperty("file.separator");
    }

    public static String getProjectDir() {
        return property;
    }

    /**
     * 动态Dynamic
     *
     * @return
     */

    public static String getDynamicPicAbsoluteDir() {
        return property  + "song-dynamicPic" + System.getProperty("file.separator");
    }

    public static String getDynamicPicDirInProject() {
        return "img" + System.getProperty("file.separator") + "song-dynamicPic" + System.getProperty("file.separator");
    }

    /**
     * Consumer
     * @return
     */

    public static String getConsumerPicInProject() {
        return "img" + System.getProperty("file.separator") + "all"  + System.getProperty("file.separator")
                + "song-consumerAvatar" + System.getProperty("file.separator") + "user.png";
    }

    public static String getConsumerPicAbsoluteDir() {
        return property + "song-consumerAvatar"
                + System.getProperty("file.separator");
    }

    public static String getConsumerPicDirInProject() {
        return "img" + System.getProperty("file.separator") + "all" +System.getProperty("file.separator")
                + "song-consumerAvatar" + System.getProperty("file.separator");
    }


}
