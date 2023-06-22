package com.tacitn.all.service;


import com.tacitn.all.domain.Consumer;
import com.tacitn.all.domain.vo.ConsumerVo;

public interface MailService {
//    Boolean sendMimeMail( String email);
    Boolean register(ConsumerVo voCode);
    Boolean registerSuccess(Consumer consumer);
}
