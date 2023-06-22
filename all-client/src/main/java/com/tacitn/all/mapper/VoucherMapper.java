package com.tacitn.all.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tacitn.all.domain.Voucher;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.List;

@Mapper
public interface VoucherMapper extends BaseMapper<Voucher> {

    List<Voucher> queryVoucherOfAll();

    List<HashMap<String,Object>> selectVoucherByConsumerId(Long consumerId);
}
