package com.tacitn.all.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.tacitn.all.domain.VoucherOrder;
import com.tacitn.all.dto.Result;

public interface IVoucherOrderService extends IService<VoucherOrder> {

    Result seckillVoucher(Long voucherId, Long consumerId) throws JsonProcessingException;

}
