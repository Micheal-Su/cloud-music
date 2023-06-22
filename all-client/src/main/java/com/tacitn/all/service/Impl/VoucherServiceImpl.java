package com.tacitn.all.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tacitn.all.domain.SeckillVoucher;
import com.tacitn.all.domain.Voucher;
import com.tacitn.all.dto.Result;
import com.tacitn.all.mapper.VoucherMapper;
import com.tacitn.all.service.ISeckillVoucherService;
import com.tacitn.all.service.IVoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

import static com.tacitn.all.utils.Consts.SECKILL_STOCK_KEY;


@Service
public class VoucherServiceImpl extends ServiceImpl<VoucherMapper, Voucher> implements IVoucherService {

    @Autowired
    private VoucherMapper voucherMapper;
    @Autowired
    private ISeckillVoucherService seckillVoucherService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result queryVoucherOfAll() {
        // 查询优惠券信息
        List<Voucher> vouchers = voucherMapper.queryVoucherOfAll();
        // 返回结果
        return Result.ok(vouchers);
    }

    @Override
    @Transactional
    public Result addSeckillVoucher(Voucher voucher) {
        // 保存优惠券
        save(voucher);
        // 保存秒杀信息
        SeckillVoucher seckillVoucher = new SeckillVoucher();
        seckillVoucher.setVoucherId(voucher.getId());
        seckillVoucher.setStock(voucher.getStock());
        seckillVoucher.setBeginTime(voucher.getBeginTime());
        seckillVoucher.setEndTime(voucher.getEndTime());
        seckillVoucherService.save(seckillVoucher);
        // 保存秒杀库存到Redis中
        stringRedisTemplate.opsForValue().set(SECKILL_STOCK_KEY + voucher.getId(), voucher.getStock().toString());
        return Result.ok("添加成功");
    }

    @Override
    public Result selectByConsumerId(Long consumerId) {
        List<HashMap<String,Object>> voucherList = voucherMapper.selectVoucherByConsumerId(consumerId);
        return Result.ok(voucherList);
    }

}
