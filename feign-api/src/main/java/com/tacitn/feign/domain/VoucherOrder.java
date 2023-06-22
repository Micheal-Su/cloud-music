package com.tacitn.feign.domain;
import lombok.Data;
import java.time.LocalDateTime;
@Data
public class VoucherOrder {
    private Long id;
    private Long consumerId;
    private Long voucherId;
    private LocalDateTime createTime;
}
