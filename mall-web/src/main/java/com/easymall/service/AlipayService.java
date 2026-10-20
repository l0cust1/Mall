package com.easymall.service;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
@Slf4j
public class AlipayService {

    @Value("${alipay.gatewayUrl}")
    private String gatewayUrl;

    @Value("${alipay.appId}")
    private String appId;

    @Value("${alipay.appPrivateKey}")
    private String appPrivateKey;

    @Value("${alipay.alipayPublicKey}")
    private String alipayPublicKey;

    @Value("${alipay.notifyUrl}")
    private String notifyUrl;

    @Value("${alipay.returnUrl}")
    private String returnUrl;

    /**
     * 调用支付宝SDK，生成支付页面HTML（包含跳转链接）
     */
    public String createAlipayPayPage(String paymentNo, BigDecimal amount, String subject) throws AlipayApiException {
        log.info("========== 开始调用支付宝支付接口，paymentNo={}, amount={}, subject={} ==========", paymentNo, amount, subject);

        // 1. 创建支付宝客户端
        AlipayClient alipayClient = buildAlipayClient();

        // 2. 创建支付请求
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setNotifyUrl(notifyUrl);
        request.setReturnUrl(returnUrl);

        // 3. 组装业务参数
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", paymentNo);
        bizContent.put("total_amount", amount.toString());
        bizContent.put("subject", subject);
        bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");
        request.setBizContent(bizContent.toString());

        // 4. 执行请求，获取响应
        AlipayTradePagePayResponse response = alipayClient.pageExecute(request);

        if (response.isSuccess()) {
            // 返回的是自动提交的form表单HTML
            String formHtml = response.getBody();
            log.info("支付宝支付单创建成功，paymentNo={}", paymentNo);
            return formHtml;
        } else {
            log.error("支付宝下单失败：code={}, msg={}, subMsg={}",
                    response.getCode(), response.getMsg(), response.getSubMsg());
            throw new AlipayApiException("支付宝下单失败：" + response.getMsg());
        }
    }

    /**
     * 构建支付宝客户端（支付、查询共用）
     */
    private AlipayClient buildAlipayClient() {
        // 清理私钥/公钥：去掉所有空白字符和 PEM 头尾标记
        String cleanPrivateKey = appPrivateKey.trim()
                .replaceAll("-----BEGIN.*?-----", "")
                .replaceAll("-----END.*?-----", "")
                .replaceAll("\\s+", "");
        String cleanAlipayPublicKey = alipayPublicKey.trim()
                .replaceAll("-----BEGIN.*?-----", "")
                .replaceAll("-----END.*?-----", "")
                .replaceAll("\\s+", "");

        return new DefaultAlipayClient(
                gatewayUrl, appId, cleanPrivateKey,
                "json", "UTF-8", cleanAlipayPublicKey, "RSA2"
        );
    }

    /**
     * 主动查询支付宝交易状态
     * 用于异步通知丢失、延迟或不可达时，由前端轮询接口兜底同步支付结果
     */
    public AlipayTradeQueryResponse queryTrade(String paymentNo) throws AlipayApiException {
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", paymentNo);
        request.setBizContent(bizContent.toString());

        AlipayTradeQueryResponse response = buildAlipayClient().execute(request);
        log.info("主动查询支付宝交易，paymentNo={}, code={}, msg={}, tradeStatus={}",
                paymentNo, response.getCode(), response.getMsg(), response.getTradeStatus());
        return response;
    }

    /**
     * 验证支付宝异步回调签名，并解析回调参数
     *
     * @param params 支付宝回调的全部表单参数
     * @return true=签名校验通过，false=签名校验失败
     */
    public boolean verifyCallbackSignature(Map<String, String> params) {
        try {
            // 清理支付宝公钥
            String cleanAlipayPublicKey = alipayPublicKey.trim()
                    .replaceAll("-----BEGIN.*?-----", "")
                    .replaceAll("-----END.*?-----", "")
                    .replaceAll("\\s+", "");

            boolean signVerified = AlipaySignature.rsaCheckV1(params, cleanAlipayPublicKey, "UTF-8", "RSA2");
            if (!signVerified) {
                log.error("支付宝回调签名校验失败，params: {}", params);
            } else {
                log.info("支付宝回调签名校验成功");
            }
            return signVerified;
        } catch (AlipayApiException e) {
            log.error("支付宝回调签名校验异常", e);
            return false;
        }
    }

    /**
     * 构建支付宝标准回调响应 XML
     */
    public String buildCallbackResponse(boolean success) {
        if (success) {
            return "success";
        } else {
            return "fail";
        }
    }
}