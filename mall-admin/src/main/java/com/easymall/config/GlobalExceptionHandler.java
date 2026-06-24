package com.easymall.config;

import com.easymall.entity.constants.vo.ResponseVO;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理所有运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseVO handleRuntimeException(RuntimeException e) {
        System.err.println("捕获到运行时异常: " + e.getMessage());
        e.printStackTrace();
        
        ResponseVO vo = new ResponseVO();
        vo.setCode(500);
        vo.setMessage(e.getMessage() != null ? e.getMessage() : "服务器内部错误");
        return vo;
    }

    /**
     * 处理所有异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseVO handleException(Exception e) {
        System.err.println("捕获到异常: " + e.getMessage());
        e.printStackTrace();
        
        ResponseVO vo = new ResponseVO();
        vo.setCode(500);
        vo.setMessage("服务器内部错误: " + e.getMessage());
        return vo;
    }
}
