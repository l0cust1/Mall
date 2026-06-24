package com.easymall.controller;

import com.easymall.component.RedisComponent;
import com.easymall.config.AppConfig;
import com.easymall.entity.constants.Constants;
import com.easymall.entity.constants.vo.CheckCodeVO;
import com.easymall.entity.constants.vo.ResponseVO;
import com.wf.captcha.ArithmeticCaptcha;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotEmpty;
import lombok.extern.slf4j.Slf4j;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/account")
@Slf4j
@Validated
public class AccountController extends ABaseController {
    @Resource
    private RedisComponent redisComponent;

   @Resource
   private AppConfig appConfig;

    @GetMapping("/checkcode")
    public ResponseVO checkcode(){
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(100, 40);
        String code = captcha.text();
        String checkCodeKey = redisComponent.saveCheckCode(code);
        String checkCodeBase64 = captcha.toBase64();
        CheckCodeVO checkCodeVO = new CheckCodeVO(checkCodeKey, checkCodeBase64);
        return  getSuccessResponseVO(checkCodeVO);
    }

    @PostMapping("/login")
    public ResponseVO login(@RequestParam @NotEmpty String account,
                        @RequestParam @NotEmpty String password,
                        @RequestParam @NotEmpty String checkCode,
                        @RequestParam @NotEmpty String checkCodeKey){
        String storedCode = redisComponent.getCheckCodeKey(checkCodeKey);
        
        if(storedCode == null || storedCode.isEmpty()){
            return getFailResponseVO("验证码已过期，请重新获取");
        }
        
        if(!checkCode.equalsIgnoreCase(storedCode)){
            return getFailResponseVO("验证码错误");
        }
        
        if(!account.equalsIgnoreCase(appConfig.getAdminAccount()) || !password.equals(appConfig.getAdminPassword())){
            return getFailResponseVO("账号或密码错误");
        }
        
        try{
            String token = redisComponent.saveTokenInfo(account);
            log.info("用户登录成功: {}", account);
            return getSuccessResponseVO(token);
        } finally {
            redisComponent.clearCheckCode(checkCodeKey);
        }
    }
    
    @RequestMapping("/logout")
    public ResponseVO logout(@RequestHeader(Constants.TOKEN_ADMIN) @NotEmpty String token) {
        redisComponent.cleanTokenInfo(token);
        log.info("用户登出成功，token: {}", token);
        return getSuccessResponseVO("登出成功");
    }
}

