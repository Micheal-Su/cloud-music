package com.tacitn.all.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tacitn.all.domain.Consumer;
import com.tacitn.all.domain.vo.Password;
import com.tacitn.all.dto.Result;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ConsumerService extends IService<Consumer> {
    Result login(Consumer consumer);

    Result queryById(Long id);

    Boolean updateWithExpire(Consumer consumer);

    Result addConsumer(Consumer consumer);

    Result updatePassword(Password password);

    Result updateConsumer(Consumer consumer);

    Result getSignCount(Long consumerId);

    Result updateConsumerPic(MultipartFile avatarFile, Long id);

    List getListByIds(List<Long> ids);
}
