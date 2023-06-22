package com.tacitn.rabbitMQListener.uitls;

public class Consts {
/*
 * RabbitMQ
 */
    /**
     * 优惠券相关
     */
    public static final String VOUCHER_ORDER_QUEUE = "djsl.voucher.order"; //队列名称
    public static final String VOUCHER_ORDER_EXCHANGE = "djsl.voucher.topic"; //交换器名称
    public static final String VOUCHER_ORDER_KEY = "VoucherRTKey"; //路由键


    /**
     * Song相关的交换机
     */
    public static final String SONG_EXCHANGE_NAME = "djsl.song.topic"; //交换器名称
    /**
     * 新增和修改相关的队列
     */
    public static final String SONG_INSERT_QUEUE = "song.insert.queue";
    /**
     * 删除相关的队列
     */
    public static final String SONG_DELETE_QUEUE = "song.delete.queue";
    /**
     * 新增和修改相关的RoutingKey
     */
    public static final String SONG_INSERT_KEY = "song.insert";
    /**
     * 删除相关的RoutingKey
     */
    public static final String SONG_DELETE_KEY = "song.delete";


    /**
     * 邮箱相关的交换机
     */
    public static final String EMAIL_EXCHANGE_NAME = "djsl.email.topic";
    /**
     * 邮箱相关的交换机
     */
    public static final String EMAIL_SEND_QUEUE = "email.send.queue";
    /**
     * RoutingKey
     */
    public static final String EMAIL_SEND_KEY = "email.send";

/*
 * Redis
 */
    public static final String SECKILL_STOCK_KEY = "seckill:stock:";
    public static final String SECKILL_ORDER_KEY = "seckill:order:";

}
