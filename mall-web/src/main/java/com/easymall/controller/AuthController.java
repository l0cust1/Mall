package com.easymall.controller;


import com.easymall.entity.DTO.LoginRequest;
import com.easymall.entity.DTO.RegisterRequest;
import com.easymall.entity.SysUser;
import com.easymall.entity.VO.LoginVO;
import com.easymall.entity.constants.Constants;
import com.easymall.entity.constants.vo.ResponseVO;
import com.easymall.service.SysUserService;
import com.easymall.service.VerificationCodeService;
import com.easymall.utils.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/auth")
public class AuthController extends ABaseController {

    @Autowired
    private SysUserService userService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private VerificationCodeService verificationCodeService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 发送验证码
     */
    @PostMapping("/sendCode")
    public ResponseVO<String> sendCode(@RequestParam String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return getFailResponseVO("手机号不能为空");
        }
        String code = verificationCodeService.generateAndSaveCode(phone);
        // 实际项目中调用短信接口发送 code，此处仅打印
        System.out.println("验证码：" + code + " 发送至 " + phone);
        return getSuccessResponseVO("验证码已发送");
    }

    /**
     * 注册（需验证手机验证码）
     */
    @PostMapping("/register")
    public ResponseVO<LoginVO> register(@RequestBody RegisterRequest request, HttpServletResponse response) {
        // 1. 校验手机号及验证码
        if (request.getPhone() == null || request.getPhone().trim().isEmpty()) {
            return getFailResponseVO("手机号不能为空");
        }
        if (request.getCode() == null || request.getCode().trim().isEmpty()) {
            return getFailResponseVO("验证码不能为空");
        }
        boolean valid = verificationCodeService.validateCode(request.getPhone(), request.getCode());
        if (!valid) {
            return getFailResponseVO("验证码错误或已过期");
        }

        // 2. 检查用户名唯一性
        if (userService.findByUsername(request.getUsername()) != null) {
            return getFailResponseVO("用户名已存在");
        }

        // 3. 创建用户
        SysUser user = new SysUser();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        userService.save(user);

        // 4. 注册成功，删除验证码
        verificationCodeService.deleteCode(request.getPhone());

        // 5. 生成双 Token
        String accessToken = setDoubleToken(response, user);

        LoginVO vo = new LoginVO(user.getUserId(), accessToken, user.getUsername(), user.getNickname(), user.getAvatar());
        return getSuccessResponseVO(vo);
    }

    /**
     * 登录 - 双 Token 机制
     * AccessToken: JWT，15分钟，返回前端内存
     * RefreshToken: 随机字符串，7天，存入 Redis + HttpOnly Cookie
     */
    @PostMapping("/login")
    public ResponseVO<LoginVO> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        SysUser user = userService.findByUsername(request.getUsername());
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return getFailResponseVO("用户名或密码错误");
        }

        // 生成双 Token
        String accessToken = setDoubleToken(response, user);

        LoginVO vo = new LoginVO(user.getUserId(), accessToken, user.getUsername(), user.getNickname(), user.getAvatar());
        return getSuccessResponseVO(vo);
    }

    /**
     * 生成双 Token 并设置 Cookie
     * @return accessToken
     */
    private String setDoubleToken(HttpServletResponse response, SysUser user) {
        // 1. 生成短期 AccessToken（JWT，15分钟）
        String accessToken = jwtUtil.generateAccessToken(user.getUsername(), user.getUserId(), Constants.ACCESS_TOKEN_EXPIRATION);

        // 2. 生成 RefreshToken（随机字符串）
        String refreshToken = UUID.randomUUID().toString().replace("-", "");

        // 3. Redis 存储 refreshToken -> userId，7天过期
        String redisKey = Constants.REDIS_KEY_REFRESH_TOKEN + refreshToken;
        stringRedisTemplate.opsForValue().set(redisKey, user.getUserId(), Constants.REDIS_KEY_EXPIRES_SEVEN_DAYS, TimeUnit.SECONDS);

        // 4. 设置 HttpOnly Cookie 存放 refreshToken
        Cookie cookie = new Cookie(Constants.COOKIE_REFRESH_TOKEN, refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // 本地 HTTP=false，线上 HTTPS=true
        cookie.setPath("/");
        cookie.setMaxAge(Constants.REDIS_KEY_EXPIRES_SEVEN_DAYS.intValue());
        cookie.setAttribute("SameSite", "Lax");
        response.addCookie(cookie);

        return accessToken;
    }

    /**
     * 从 Cookie 中获取 refreshToken
     */
    private String getRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (Constants.COOKIE_REFRESH_TOKEN.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    /**
     * 刷新 Token - 无感续登
     * 从 HttpOnly Cookie 拿 refreshToken，校验 Redis 后签发新双 Token
     */
    @PostMapping("/refresh")
    public ResponseVO<String> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = getRefreshTokenFromCookie(request);
        if (refreshToken == null) {
            response.setStatus(401);
            return getFailResponseVO("登录已失效，请重新登录");
        }

        // 校验 Redis 中是否存在该 refreshToken
        String redisKey = Constants.REDIS_KEY_REFRESH_TOKEN + refreshToken;
        String userId = stringRedisTemplate.opsForValue().get(redisKey);
        if (userId == null) {
            response.setStatus(401);
            return getFailResponseVO("登录已失效，请重新登录");
        }

        // 删除旧 refreshToken（防重放攻击）
        stringRedisTemplate.delete(redisKey);

        // 查询用户信息
        SysUser user = userService.getById(userId);
        if (user == null) {
            response.setStatus(401);
            return getFailResponseVO("用户不存在");
        }

        // 生成新双 Token
        String newAccessToken = setDoubleToken(response, user);
        return getSuccessResponseVO(newAccessToken);
    }

    /**
     * 退出登录
     * 删除 Redis 中的 refreshToken，清空 Cookie
     */
    @PostMapping("/logout")
    public ResponseVO<String> logout(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = getRefreshTokenFromCookie(request);

        // 删除 Redis 中的 refreshToken
        if (refreshToken != null) {
            String redisKey = Constants.REDIS_KEY_REFRESH_TOKEN + refreshToken;
            stringRedisTemplate.delete(redisKey);
        }

        // 清空 Cookie
        Cookie cookie = new Cookie(Constants.COOKIE_REFRESH_TOKEN, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setAttribute("SameSite", "Lax");
        response.addCookie(cookie);

        return getSuccessResponseVO("退出成功");
    }

}