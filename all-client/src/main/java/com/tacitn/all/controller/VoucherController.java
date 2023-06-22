package com.tacitn.all.controller;
import com.tacitn.all.domain.Voucher;
import com.tacitn.all.dto.Result;
import com.tacitn.all.service.IVoucherService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/voucher")
public class VoucherController {

    @Resource
    private IVoucherService voucherService;

    /**
     * 新增秒杀券
     * @param voucher 优惠券信息，包含秒杀信息
     * @return 优惠券id
     */
    @PostMapping("seckill")
    public Result addSeckillVoucher(@RequestBody Voucher voucher) {
        return voucherService.addSeckillVoucher(voucher);
    }

    /**
     * 新增普通券
     * @param voucher 优惠券信息
     * @return 优惠券id
     */
    @PostMapping("add")
    public Result addVoucher(@RequestBody Voucher voucher) {
        if (voucherService.save(voucher)){
            return Result.ok();
        }
        return Result.fail("添加失败");
    }


    /**
     * 获取所有优惠券信息
     */
    @GetMapping("/selectAll")
    public Result queryVoucherOfAll() {
       return voucherService.queryVoucherOfAll();
    }
    /**
     * 查询某一用户的优惠券信息
     */
    @GetMapping("/selectByConsumerId")
    public Result selectByConsumerId(Long consumerId) {
        return voucherService.selectByConsumerId(consumerId);
    }

}
