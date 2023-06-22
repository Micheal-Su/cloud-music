package com.tacitn.rabbitMQListener.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import static com.tacitn.rabbitMQListener.uitls.Consts.*;

/**
 * @author DongJiShiLiu
 * @create 2023/4/1 16:01
 */
@Slf4j
@Configuration
public class RabbitMQConfig {

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) throws InterruptedException {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        // 匿名内部类只有一个方法，可以用lambda表达式
        // ReturnCallback为路由不到队列时触发，成功则不触发
        // (而ConfirmCallback为发送Exchange（交换器）时回调，成功或者失败都会触发；)；
        // ReturnCallback一个springboot只能有一个
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            if (message.getMessageProperties().getReceivedDelay() > 0) {
                // 排除暂存在延迟交换机中的消息，避免报找不到队列的错误
                return;
            }
            System.out.println("\nSONG:确认消息送到队列(Queue)结果：");
            System.out.println("发送消息：" + message);
            System.out.println("回应码：" + replyCode);
            System.out.println("回应信息：" + replyText);
            System.out.println("交换机：" + exchange);
            System.out.println("路由键：" + routingKey);
        });
        return rabbitTemplate;
    }
    /**
     * Direct交换机
     */
    @Bean
    public DirectExchange exchange() {
        /**
         * 创建交换器，参数说明：
         * String name：交换器名称
         * boolean durable：设置是否持久化，默认是 false。durable 设置为 true 表示持久化，反之是非持久化。
         * 持久化可以将交换器存盘，在服务器重启的时候不会丢失相关信息。
         * boolean autoDelete：设置是否自动删除，为 true 则设置队列为自动删除，
         */
        return new DirectExchange(SONG_EXCHANGE_NAME, true, false);
    }

    /**
     * 新增和修改相关的队列
     */
    @Bean
    public Queue insertQueue() {
        /**
         * 创建队列，参数说明：
         * String name：队列名称。
         * boolean durable：设置是否持久化，默认是 false。durable 设置为 true 表示持久化，反之是非持久化。
         * 持久化的队列会存盘，在服务器重启的时候不会丢失相关信息。
         * boolean exclusive：设置是否排他，默认也是 false。为 true 则设置队列为排他。
         * boolean autoDelete：设置是否自动删除，为 true 则设置队列为自动删除，
         * 当没有生产者或者消费者使用此队列，该队列会自动删除。
         * Map<String, Object> arguments：设置队列的其他一些参数。
         *
         * deadLetterExchange：指定死信交换机,这个交换机可以是普通的交换机，只是因为它接收队列中的死信，所以叫死信交换机
         * ttl：设置队列的超时时间，5秒
         */
        return QueueBuilder.durable(SONG_INSERT_QUEUE) // 指定队列名称，并持久化
                .ttl(5000)
                .deadLetterExchange("dl.direct")
                .deadLetterRoutingKey("deadLetter")
                .build();// 如果属性和原先存在的队列不一致，得先删除原先的
    }

    /**
     * 删除相关的队列
     */
    @Bean
    public Queue deleteQueue() {
        return QueueBuilder.durable(SONG_DELETE_QUEUE) // 指定队列名称，并持久化
                .ttl(5000)
                .deadLetterExchange("dl.direct")
                .deadLetterRoutingKey("deadLetter")
                .build();// 如果属性和原先存在的队列不一致，得先删除原先的
    }

    /**
     * 绑定新增和修改相关的
     */
    @Bean
    Binding bindingInsert(DirectExchange exchange, Queue insertQueue) {
        return BindingBuilder.bind(insertQueue).to(exchange).with(SONG_INSERT_KEY);
    }

    /**
     * 绑定新增和修改相关的
     */
    @Bean
    Binding bindingDelete(DirectExchange exchange, Queue deleteQueue) {
        return BindingBuilder.bind(deleteQueue).to(exchange).with(SONG_DELETE_KEY);
    }


    /**
     * 邮箱相关交换机
     */
    @Bean
    public DirectExchange emailExchange() {
        return new DirectExchange(EMAIL_EXCHANGE_NAME, true, false);
    }

    @Bean
    public Queue emailSendQueue() {
        return QueueBuilder.durable(EMAIL_SEND_QUEUE) // 指定队列名称，并持久化
                .build();
    }

    @Bean
    Binding bindingEmailSend(DirectExchange emailExchange, Queue emailSendQueue) {
        return BindingBuilder.bind(emailSendQueue).to(emailExchange).with(EMAIL_SEND_KEY);
    }

}
