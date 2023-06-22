package com.tacitn.feign.clients.fallback;

import com.tacitn.feign.clients.AllClient;
import com.tacitn.feign.domain.Admin;
import com.tacitn.feign.domain.Consumer;
import com.tacitn.feign.domain.VoucherOrder;
import com.tacitn.feign.domain.dto.Result;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class AllClientFallbackFactory implements FallbackFactory<AllClient> {
    @Override
    public AllClient create(Throwable throwable) {
        return new AllClient() {
            @Override
            public Admin getAdminById(Long id) {
                log.error("查询Admin异常");
                return new Admin();
            }

            @Override
            public void updateVoucherDB(VoucherOrder voucherOrder) {

            }
            @Override
            public Result selectConsumerById(String id) {
                log.error("查询Consumer异常 selectConsumerById");
                Consumer consumer = new Consumer();
                consumer.setUsername("");
                return Result.ok(consumer);
            }

            @Override
            public Consumer getConsumerAll(String id) {
                log.error("查询Consumer异常 getConsumerAll");
                return new Consumer();
            }

            @Override
            public Result sendCode(String email) {
                return null;
            }

            @Override
            public List getConsumerList(List<Long> ids) {
                return new ArrayList();
            }

            @Override
            public boolean setSongList(Long consumerId, Integer num) {
                return false;
            }

            @Override
            public boolean setSong(Long consumerId, Integer num) {
                return false;
            }
        };
    }
}
