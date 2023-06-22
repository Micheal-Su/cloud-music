package com.tacitn.all.service;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public interface AsyncService {

    /**
     * 执行异步任务
     */
    void executeAsync();

    void cacheAllPublished(String key,String value);

    void deleteCacheAllPublished();


    <R,ID> void saveExpireData2Redis(String keyPrefix, ID id,  Function<ID,R> dbFallback, Long time,TimeUnit unit);


    void sign(Long consumerId);


}
