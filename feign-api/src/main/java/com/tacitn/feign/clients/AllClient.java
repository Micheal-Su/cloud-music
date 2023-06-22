package com.tacitn.feign.clients;

import com.tacitn.feign.clients.fallback.AllClientFallbackFactory;
import com.tacitn.feign.domain.Admin;
import com.tacitn.feign.domain.Consumer;
import com.tacitn.feign.domain.VoucherOrder;
import com.tacitn.feign.domain.dto.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@FeignClient(value = "allclient",fallbackFactory = AllClientFallbackFactory.class)
public interface AllClient {
    @PostMapping("/voucher-order/seckill/updateVoucherDB")
    void updateVoucherDB(@RequestBody VoucherOrder voucherOrder);

    @GetMapping("/admin/getAdminById/{id}")
    Admin getAdminById(@PathVariable("id") Long id);

    @GetMapping("/consumer/selectByPrimaryKey/{id}")
    Result selectConsumerById(@PathVariable("id") String id);

    @GetMapping("/consumer/getConsumerAll/{id}")
    Consumer getConsumerAll(@PathVariable("id") String id);

    @PostMapping("/signUp/sendCode")
    Result sendCode(@RequestParam("email") String email);

    @GetMapping("/consumer/getConsumerList")
    List getConsumerList(@RequestParam("ids") List<Long> ids);

    @PostMapping("/conPossession/setSongList")
    boolean setSongList(@RequestParam("consumerId") Long consumerId
            ,@RequestParam("num") Integer num);

    @PostMapping("/conPossession/setSong")
    boolean setSong(@RequestParam("consumerId") Long consumerId
            ,@RequestParam("num") Integer num);
}
