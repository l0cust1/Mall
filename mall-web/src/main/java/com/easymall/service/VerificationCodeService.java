package com.easymall.service;

import com.easymall.component.RedisComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class VerificationCodeService {
    @Autowired
    private RedisComponent redisComponent;

    /**
     * 生成6位数字验证码并存储到Redis，key为手机号
     */
    public String generateAndSaveCode(String phone) {
        String code = String.format("%06d", new Random().nextInt(999999));
        redisComponent.savePhoneCode(phone, code);
        return code;
    }


    /**
     * 校验验证码
     */
    public boolean validateCode(String phone, String inputCode) {
        String storedCode = redisComponent.getPhoneCode(phone);
        return storedCode != null && storedCode.equals(inputCode);
    }

    /**
     * 删除验证码
     */
    public void deleteCode(String phone) {
        redisComponent.deletePhoneCode(phone);
    }
}