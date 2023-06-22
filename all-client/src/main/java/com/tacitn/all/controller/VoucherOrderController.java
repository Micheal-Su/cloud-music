package com.tacitn.all.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.tacitn.all.domain.VoucherOrder;
import com.tacitn.all.dto.Result;
import com.tacitn.all.service.ISeckillVoucherService;
import com.tacitn.all.service.IVoucherOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * <p>
 *  前端控制器
 * </p>

 */
@RestController
@RequestMapping("/voucher-order")
public class VoucherOrderController {

    @Resource
    private IVoucherOrderService voucherOrderService;

    @Autowired
    private ISeckillVoucherService seckillVoucherService;


    @PostMapping("/seckill")
    public Result seckillVoucher(@RequestParam("id") Long voucherId, @RequestParam("consumerId") Long consumerId) throws JsonProcessingException {
        return voucherOrderService.seckillVoucher(voucherId,consumerId);
    }

    @PostMapping("/seckill/updateVoucherDB")
    public void updateVoucherDB(@RequestBody VoucherOrder voucherOrder){
        seckillVoucherService.update()
                .setSql("stock = stock - 1") // set stock = stock - 1
                .eq("voucher_id", voucherOrder.getVoucherId())
                .gt("stock", 0) // where id = ? and stock > 0
                .update();
        voucherOrderService.save(voucherOrder);
    }

}
