package com.tacitn.all.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tacitn.all.domain.Dynamic;
import com.tacitn.all.domain.vo.ScrollResult;
import com.tacitn.all.dto.Result;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author DongJiShiLiu
 * @create 2023/3/10 18:47
 */

public interface DynamicService extends IService<Dynamic> {
    Result postDynamic(Dynamic dynamic,MultipartFile image);

    ScrollResult queryBlogOfFollow(Long fansId, Long max, Integer offset);
}
