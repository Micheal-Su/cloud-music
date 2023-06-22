package com.tacitn.all.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author DongJiShiLiu
 * @create 2023/4/1 15:50
 */
@Data
public class SimpleVoucherOrder {
    /**
     * orderId
     */
    private Long id;
    /**
     * 下单的用户id
     */
    private Long consumerId;

    /**
     * 购买的代金券id
     */
    private Long voucherId;
    /**
     * 购买的时间
     */
    private LocalDateTime createTime;


}
