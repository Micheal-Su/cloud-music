package com.tacitn.rabbitMQListener.Listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tacitn.feign.clients.AllClient;
import com.tacitn.feign.clients.SongClient;
import com.tacitn.feign.domain.VoucherOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

import static com.tacitn.rabbitMQListener.uitls.Consts.*;


@Slf4j
@Configuration
public class RabbitMQListener {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AllClient allClient;

    @Autowired
    private SongClient songClient;

    @RabbitListener(queues = VOUCHER_ORDER_QUEUE)
    public void voucherOrder(Message message) {
        try {
            // 因为发送方是专门处理优惠券的，发回去让其处理
            VoucherOrder voucherOrder = objectMapper.readValue(message.getBody(), VoucherOrder.class);
            allClient.updateVoucherDB(voucherOrder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 删除的直接在SongService里异步进行了
    @RabbitListener(queues = SONG_INSERT_QUEUE)
    public void songInsertOrUpdate(Long id) {
        Long songId = id;
        songClient.insertOrUpdateSongToES(songId);
    }

    @RabbitListener(queues = EMAIL_SEND_QUEUE)
    public void sendCode(Message email) {
        allClient.sendCode(new String(email.getBody()));
    }


    // 延迟交换机，只要在MessageBuilder中.setHeader("x-delay", 5000)
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "delay.queue", durable = "true"),
            exchange = @Exchange(name = "delay.direct", delayed = "true"),
            key = "delay"
    ))
    public void listenDelayExchange(String msg) {
        log.info("消费者接收到了delay.queue的延迟消息" + msg);
    }
}
