package com.easymall.controller;


import com.easymall.entity.constants.vo.ResponseVO;

/**
 * 所有Controller的父类，封装通用响应方法
 */
public abstract class ABaseController {

    /**
     * 成功响应（带数据）
     */
    protected <T> ResponseVO getSuccessResponseVO(T t) {
        ResponseVO<T> vo = new ResponseVO<>();
        vo.setCode(200);
        vo.setMessage("操作成功");
        vo.setData(t);
        return vo;
    }

    /**
     * 失败响应
     */
    protected ResponseVO getFailResponseVO(String message) {
        ResponseVO vo = new ResponseVO();
        vo.setCode(500);
        vo.setMessage(message);
        return vo;
    }
}