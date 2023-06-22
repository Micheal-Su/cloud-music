package com.tacitn.all.service.Impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tacitn.all.domain.Consumer;
import com.tacitn.all.domain.Dynamic;
import com.tacitn.all.domain.vo.ScrollResult;
import com.tacitn.all.dto.Result;
import com.tacitn.all.mapper.DynamicMapper;
import com.tacitn.all.service.ConsumerService;
import com.tacitn.all.service.DynamicService;
import com.tacitn.all.utils.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.tacitn.all.utils.Consts.FEED_KEY;


/**
 * @author DongJiShiLiu
 * @create 2023/3/10 18:47
 */
@Service
public class DynamicServiceImpl extends ServiceImpl<DynamicMapper, Dynamic> implements DynamicService {
    @Autowired
    DynamicMapper dynamicMapper;

    @Autowired
    ConsumerService consumerService;

//    @Autowired
//    StorageSizeService storageSizeService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Override
    public Result postDynamic(Dynamic dynamic, MultipartFile image) {
        if (!image.isEmpty()){
            try {
                FileUtil.savePic(dynamic, image);
            } catch (IOException e) {
                return Result.fail("图片上传失败");
            }
        }
        dynamic.setCreateTime(LocalDateTime.now());
        dynamic.setUpdateTime(LocalDateTime.now());
        boolean save = saveOrUpdate(dynamic);
        if (save) {
            return Result.ok("上传成功");
        }
        return Result.fail("上传失败");

    }

    /**
     *
     * @param fansId 粉丝的id
     * @param max 上一次查询到的最旧的动态的时间戳，也就是lastId；叫max是因为它是本次查询中时间戳值最大的
     * @param offset 偏移量：上一次查询结果中和最小值相等的个数
     * @return
     */
    @Override
    public ScrollResult queryBlogOfFollow(Long fansId, Long max, Integer offset) {
        String key = FEED_KEY + fansId;
        Set<ZSetOperations.TypedTuple<String>> typedTuples = stringRedisTemplate.opsForZSet()
                .reverseRangeByScoreWithScores(key, 0, max, offset, 3);

        if (typedTuples == null || typedTuples.isEmpty()) {
            return null;
        }
        // 4.解析数据：blogId、minTime（时间戳）、offset
        List<Long> ids = new ArrayList<>(typedTuples.size());
        long minTime = 0;
        int os = 1;
        for (ZSetOperations.TypedTuple<String> tuple : typedTuples) {
            // 有序的放入动态的id。
            ids.add(Long.valueOf(tuple.getValue()));
            // 获取分数(时间戳）
            long time = tuple.getScore().longValue();

            // 后面查的 time 不可能比 minTime 大
            if(time == minTime){
                os++;
            }else{
                // 如果 time 比 minTime 小,重置minTime
                minTime = time;
                os = 1;
            }
        }

        // 5.根据id查询blog
        String idStr = StrUtil.join(",", ids);
        List<Dynamic> dynamics = query().in("id", ids).last("ORDER BY FIELD(id," + idStr + ")").list();

        for (Dynamic dynamic : dynamics) {
            // 5.1.查询blog所属用户信息
            getDynamicUserInfo(dynamic);
            // 5.2.查询blog是否被点赞
//            isBlogLiked(blog);
        }

        // 6.封装并返回
        ScrollResult r = new ScrollResult();
        r.setList(dynamics);
        r.setOffset(os);
        r.setMinTime(minTime);
        return  r;
    }

    void getDynamicUserInfo(Dynamic dynamic){
        Consumer consumer = consumerService.getById(dynamic.getConsumerId());
        dynamic.setAvatar(consumer.getAvatar());
        dynamic.setUsername(consumer.getUsername());
    }
}
