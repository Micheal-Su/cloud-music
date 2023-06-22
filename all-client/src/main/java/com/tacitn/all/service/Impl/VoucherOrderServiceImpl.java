package com.tacitn.all.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tacitn.all.domain.VoucherOrder;
import com.tacitn.all.domain.vo.SimpleVoucherOrder;
import com.tacitn.all.dto.Result;
import com.tacitn.all.mapper.VoucherOrderMapper;
import com.tacitn.all.service.IVoucherOrderService;
import com.tacitn.all.utils.RedisIdWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;
import static com.tacitn.all.utils.Consts.EXCHANGE_NAME;
import static com.tacitn.all.utils.Consts.ROUTING_KEY;

@Slf4j
@Service
public class VoucherOrderServiceImpl extends ServiceImpl<VoucherOrderMapper, VoucherOrder> implements IVoucherOrderService {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Resource
    private RedisIdWorker redisIdWorker;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    //    Redis脚本对象
    private static final DefaultRedisScript<Long> SECKILL_SCRIPT;

    static {
        SECKILL_SCRIPT = new DefaultRedisScript<>();
        SECKILL_SCRIPT.setLocation(new ClassPathResource("seckill.lua"));
        SECKILL_SCRIPT.setResultType(Long.class);
    }


    @Override
//    为了快，不要进行数据库相关操作, 类似日期等固定数据的判断可交给前端
    public Result seckillVoucher(Long voucherId, Long consumerId) throws JsonProcessingException {

        long orderId = redisIdWorker.nextId("order");
//        因为 keys 是在脚本文件里创建的，不需要传进去，传空集合即可，不能传null
        Long result = stringRedisTemplate.execute(
                SECKILL_SCRIPT,
                Collections.emptyList(),
                voucherId.toString(), consumerId.toString());
        int r = result.intValue();
        if (r != 0) {
            return Result.warning(r == 1 ? "库存不足" : "不可重复下单");
        }
        // 准备发送这MQ的消息对象
        SimpleVoucherOrder order = new SimpleVoucherOrder();
        order.setConsumerId(consumerId);
        order.setId(orderId);
        order.setVoucherId(voucherId);
        order.setCreateTime(LocalDateTime.now());
        String json =objectMapper.writeValueAsString(order);
        // 其实在SpringBoot中，会自动把传入队列的消息设置为持久化的，不需要自己设置，比如直接传json.getBytes(StandardCharsets.UTF_8)即可
        Message message = MessageBuilder.withBody(json.getBytes(StandardCharsets.UTF_8))
                .setDeliveryMode(MessageDeliveryMode.PERSISTENT)
                .build();

        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
        correlationData.getFuture().addCallback(MqResult -> {
            // 判断结果
            if (MqResult.isAck()) {
                // ACK
                log.info("消息成功投递到交换机！消息ID: {}", correlationData.getId());
            } else {
                // NACK
                log.error("消息投递到交换机失败！消息ID：{}", correlationData.getId());
                // 重发消息或者存入数据库，人工介入
                rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, message);

            }
        }, ex -> {
            // 记录日志
            log.error("消息发送失败！", ex);
            // 重发消息或者存入数据库，人工介入
            rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, message);

        });
        rabbitTemplate.convertAndSend(EXCHANGE_NAME , ROUTING_KEY, message,correlationData);

        // 上面是异步的，第一次能responseResult.setMsg("系统出错，请等待修复")可能是因为第一次返回得慢
        return Result.ok();

    }

}
