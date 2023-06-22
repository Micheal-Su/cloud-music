package com.tacitn.all.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tacitn.all.domain.Voucher;
import com.tacitn.all.dto.Result;

public interface IVoucherService extends IService<Voucher> {

    Result queryVoucherOfAll();

    Result addSeckillVoucher(Voucher voucher);

    Result selectByConsumerId(Long consumerId);
}
