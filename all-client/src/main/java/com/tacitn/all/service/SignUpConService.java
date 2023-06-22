package com.tacitn.all.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tacitn.all.domain.vo.ConsumerVo;
import com.tacitn.all.domain.vo.SignUpCon;
import com.tacitn.all.dto.Result;

import javax.servlet.http.HttpServletRequest;
public interface SignUpConService extends IService<SignUpCon> {
    Result commitUserInfo(SignUpCon signUpCon, HttpServletRequest request);

    Result sendCode(String email);

    Result doRegist(ConsumerVo vo);
}
