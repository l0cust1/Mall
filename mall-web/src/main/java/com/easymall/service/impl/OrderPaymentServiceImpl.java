package com.easymall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.easymall.entity.DTO.PayCreateDTO;
import com.easymall.entity.Order.OrderInfo;
import com.easymall.entity.Order.OrderItem;
import com.easymall.entity.Order.OrderPayment;
import com.easymall.entity.VO.PayCreateVO;
import com.easymall.entity.VO.PayQueryVO;
import com.easymall.mapper.OrderInfoMapper;
import com.easymall.mapper.OrderItemMapper;
import com.easymall.mapper.OrderPaymentMapper;
import com.easymall.service.AlipayService;
import com.easymall.service.OrderPaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 订单支付服务实现类
 */
@Service
@Slf4j
public class OrderPaymentServiceImpl extends ServiceImpl<OrderPaymentMapper, OrderPayment> implements OrderPaymentService {

    private static final DateTimeFormatter PAY_NO_DATE_FMT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final OrderInfoMapper orderInfoMapper;
    private final OrderItemMapper orderItemMapper;
    private final AlipayService alipayService;
    private final TransactionTemplate transactionTemplate;

    public OrderPaymentServiceImpl(OrderInfoMapper orderInfoMapper,
                                    OrderItemMapper orderItemMapper,
                                    AlipayService alipayService,
                                    PlatformTransactionManager transactionManager) {
        this.orderInfoMapper = orderInfoMapper;
        this.orderItemMapper = orderItemMapper;
        this.alipayService = alipayService;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PayCreateVO createPay(String userId, PayCreateDTO dto) {
        // ========== 步骤 1：权限 + 订单合法性校验 ==========
        OrderInfo orderInfo = orderInfoMapper.selectById(dto.getOrderId());
        if (orderInfo == null) {
            throw new RuntimeException("订单不存在");
        }
        if (!orderInfo.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作该订单");
        }
        if (orderInfo.getIsDeleted() != null && orderInfo.getIsDeleted() != 0) {
            throw new RuntimeException("订单已被删除，不支持支付");
        }
        if (orderInfo.getOrderStatus() != 1) {
            throw new RuntimeException("订单状态不是待付款，不支持支付");
        }

        // 校验是否已有支付成功的流水
        Long paidCount = this.count(
                new LambdaQueryWrapper<OrderPayment>()
                        .eq(OrderPayment::getOrderId, dto.getOrderId())
                        .eq(OrderPayment::getPaymentStatus, 1));
        if (paidCount > 0) {
            throw new RuntimeException("订单已有支付成功记录，禁止重复支付");
        }

        // 查询是否已有待支付的支付流水
        OrderPayment existingPayment = this.getOne(
                new LambdaQueryWrapper<OrderPayment>()
                        .eq(OrderPayment::getOrderId, dto.getOrderId())
                        .eq(OrderPayment::getPaymentStatus, 0)
                        .eq(OrderPayment::getPaymentMethod, dto.getPaymentMethod()));

        // 若已有待支付记录，直接返回现有支付信息
        if (existingPayment != null) {
            log.info("订单已有待支付流水，直接返回，orderId={}, paymentNo={}", dto.getOrderId(), existingPayment.getPaymentNo());
            String payUrl = buildPayUrl(existingPayment, orderInfo.getPayAmount());
            PayCreateVO vo = new PayCreateVO();
            vo.setPaymentNo(existingPayment.getPaymentNo());
            vo.setPayUrl(payUrl);
            vo.setPaymentAmount(existingPayment.getPaymentAmount());
            return vo;
        }

        // ========== 步骤 2：生成支付流水号 ==========
        String paymentNo = generatePaymentNo();

        // ========== 步骤 3：封装 order_payment 待支付记录，插入数据库 ==========
        OrderPayment payment = new OrderPayment();
        payment.setOrderId(dto.getOrderId());
        payment.setPaymentNo(paymentNo);
        payment.setPaymentMethod(dto.getPaymentMethod());
        payment.setPaymentTime(null);
        payment.setPaymentAmount(orderInfo.getPayAmount());
        payment.setPaymentStatus(0); // 0-待支付
        payment.setCreateTime(LocalDateTime.now());
        this.save(payment);

        // ========== 步骤 4：调用第三方支付服务，生成支付唤起链接 ==========
        String payUrl = buildPayUrl(payment, orderInfo.getPayAmount());

        // ========== 步骤 5：返回结果 ==========
        PayCreateVO vo = new PayCreateVO();
        vo.setPaymentNo(paymentNo);
        vo.setPayUrl(payUrl);
        vo.setPaymentAmount(orderInfo.getPayAmount());
        return vo;
    }

    /**
     * 生成支付流水号
     * 规则: PAY + yyyyMMddHHmmss + 4位随机数
     */
    private String generatePaymentNo() {
        String datePart = LocalDateTime.now().format(PAY_NO_DATE_FMT);
        int random = ThreadLocalRandom.current().nextInt(1000, 9999);
        return "PAY" + datePart + random;
    }

    /**
     * 构建支付链接（统一入口）
     */
    private String buildPayUrl(OrderPayment payment, BigDecimal amount) {
        String method = payment.getPaymentMethod();
        if (isAlipay(method)) {
            String subject = getOrderSubject(payment.getOrderId());
            try {
                return alipayService.createAlipayPayPage(payment.getPaymentNo(), amount, subject);
            } catch (Exception e) {
                throw new RuntimeException("支付宝支付单创建失败: " + e.getMessage(), e);
            }
        }
        return buildMockPayUrl(payment.getPaymentNo(), amount, method);
    }

    /**
     * 是否支付宝支付方式
     */
    private boolean isAlipay(String method) {
        return "支付宝".equals(method) || "alipay".equalsIgnoreCase(method);
    }

    /**
     * 获取订单支付标题（取首个商品名称）
     */
    private String getOrderSubject(String orderId) {
        try {
            List<OrderItem> items = orderItemMapper.selectList(
                    new LambdaQueryWrapper<OrderItem>()
                            .eq(OrderItem::getOrderId, orderId)
                            .last("LIMIT 1"));
            if (items != null && !items.isEmpty()) {
                return items.get(0).getProductName();
            }
        } catch (Exception e) {
            log.warn("获取订单商品标题失败，使用默认标题: orderId={}", orderId, e);
        }
        return "订单支付";
    }

    /**
     * 模拟第三方支付链接（微信/银联等未接入真实 SDK 时使用）
     */
    private String buildMockPayUrl(String paymentNo, BigDecimal amount, String method) {
        if ("微信支付".equals(method) || "wechat".equalsIgnoreCase(method) || "weixin".equalsIgnoreCase(method)) {
            return "https://pay.weixin.qq.com/wxpay/pay?action=pay&payment_no=" + paymentNo;
        } else if ("银联支付".equals(method) || "unionpay".equalsIgnoreCase(method)) {
            return "https://pay.95516.com/pay/web?payment_no=" + paymentNo;
        }
        return "https://pay.easymall.com/checkout?payment_no=" + paymentNo;
    }

    @Override
    public String handlePayCallback(Map<String, String> params) {
        log.info("========== 开始处理支付宝异步回调，原始参数: {} ==========", params);

        // ========== 第一步：签名校验 ==========
        try {
            boolean signVerified = alipayService.verifyCallbackSignature(params);
            if (!signVerified) {
                log.error("【支付宝回调】签名校验失败，拒绝处理。params: {}", params);
                return "fail";
            }
        } catch (Exception e) {
            log.error("【支付宝回调】签名校验异常", e);
            return "fail";
        }

        // 提取关键参数
        String outTradeNo = params.get("out_trade_no");  // 我方支付流水号 = payment_no
        String tradeNo = params.get("trade_no");          // 支付宝第三方流水号
        String totalAmount = params.get("total_amount");  // 实际扣款金额
        String tradeStatus = params.get("trade_status");  // 交易状态

        log.info("【支付宝回调】解析参数: outTradeNo={}, tradeNo={}, totalAmount={}, tradeStatus={}",
                outTradeNo, tradeNo, totalAmount, tradeStatus);

        // ========== 第二步：根据 payment_no 查询支付单 ==========
        if (outTradeNo == null || outTradeNo.isEmpty()) {
            log.error("【支付宝回调】out_trade_no 为空，无法定位支付单");
            return "fail";
        }

        OrderPayment payment = this.getOne(
                new LambdaQueryWrapper<OrderPayment>()
                        .eq(OrderPayment::getPaymentNo, outTradeNo));

        if (payment == null) {
            log.warn("【支付宝回调】未找到支付单，paymentNo={}，返回 success（无需重试）", outTradeNo);
            return "success";
        }

        // 幂等检查：已支付成功
        if (payment.getPaymentStatus() == 1) {
            log.info("【支付宝回调】支付单已成功，幂等跳过。paymentNo={}", outTradeNo);
            return "success";
        }

        // 支付单状态为 2/3（失败/退款）
        if (payment.getPaymentStatus() == 2 || payment.getPaymentStatus() == 3) {
            log.warn("【支付宝回调】支付单状态为失败/退款，幂等跳过。paymentNo={}, status={}",
                    outTradeNo, payment.getPaymentStatus());
            return "success";
        }

        // ========== 第三步：查询订单主表做合法性校验 ==========
        String orderId = payment.getOrderId();
        OrderInfo orderInfo = orderInfoMapper.selectById(orderId);

        if (orderInfo == null) {
            log.error("【支付宝回调】订单不存在，orderId={}", orderId);
            return "fail";
        }

        // 校验订单未被软删除
        if (orderInfo.getIsDeleted() != null && orderInfo.getIsDeleted() != 0) {
            log.warn("【支付宝回调】订单已被软删除，orderId={}", orderId);
            return "success";
        }

        // 校验订单状态必须 = 1（待付款）
        if (orderInfo.getOrderStatus() != 1) {
            log.warn("【支付宝回调】订单状态不是待付款，orderId={}, status={}",
                    orderId, orderInfo.getOrderStatus());

            // 订单状态是 5/6/7（已取消/关闭/退款），更新支付单为失败
            Integer status = orderInfo.getOrderStatus();
            if (status == 5 || status == 6 || status == 7) {
                log.info("【支付宝回调】订单已取消/关闭/退款，更新支付单为失败。orderId={}", orderId);
                this.update(new UpdateWrapper<OrderPayment>()
                        .eq("payment_no", outTradeNo)
                        .eq("payment_status", 0)
                        .set("payment_status", 2));
                return "success";
            }

            // 订单已流转（2/3/4），不更新
            return "success";
        }

        // 金额校验：回调 total_amount 与 order_payment.payment_amount、order_info.pay_amount 三者一致
        BigDecimal callbackAmount;
        try {
            callbackAmount = new BigDecimal(totalAmount);
        } catch (Exception e) {
            log.error("【支付宝回调】金额格式非法，totalAmount={}", totalAmount);
            return "fail";
        }

        if (callbackAmount.compareTo(payment.getPaymentAmount()) != 0
                || callbackAmount.compareTo(orderInfo.getPayAmount()) != 0) {
            log.error("【支付宝回调】金额不匹配！callbackAmount={}, paymentAmount={}, payAmount={},"
                    + " paymentNo={}, orderId={}",
                    callbackAmount, payment.getPaymentAmount(), orderInfo.getPayAmount(),
                    outTradeNo, orderId);
            return "fail";
        }

        // ========== 第四步：事务内双表更新 ==========
        return processPaymentSuccess(payment, orderInfo, tradeNo);
    }

    @Override
    public PayQueryVO queryPayStatus(String userId, String paymentNo) {
        // ========== 步骤 1：根据 payment_no 查询支付记录 ==========
        OrderPayment payment = this.getOne(
                new LambdaQueryWrapper<OrderPayment>()
                        .eq(OrderPayment::getPaymentNo, paymentNo));

        if (payment == null) {
            throw new RuntimeException("支付流水号不存在");
        }

        // ========== 步骤 2：权限校验 - 通过 orderId 关联查询订单，验证用户归属 ==========
        OrderInfo orderInfo = orderInfoMapper.selectById(payment.getOrderId());
        if (orderInfo == null) {
            throw new RuntimeException("关联订单不存在");
        }
        if (!orderInfo.getUserId().equals(userId)) {
            throw new RuntimeException("无权查看该订单支付状态");
        }

        // ========== 步骤 3：本地仍待支付时，主动向支付宝查询兜底 ==========
        // 异步通知可能丢失、延迟或隧道不可达，不能只依赖回调更新状态
        if (Integer.valueOf(0).equals(payment.getPaymentStatus())
                && isAlipay(payment.getPaymentMethod())) {
            syncPaymentFromAlipay(payment, orderInfo);

            // 同步完成后重新读取，确保返回最新状态
            payment = this.getOne(
                    new LambdaQueryWrapper<OrderPayment>()
                            .eq(OrderPayment::getPaymentNo, paymentNo));
            if (payment == null) {
                throw new RuntimeException("支付流水号不存在");
            }
            orderInfo = orderInfoMapper.selectById(payment.getOrderId());
            if (orderInfo == null) {
                throw new RuntimeException("关联订单不存在");
            }
        }

        // ========== 步骤 4：组装返回 VO ==========
        PayQueryVO vo = new PayQueryVO();
        vo.setPaymentNo(payment.getPaymentNo());
        vo.setOrderId(payment.getOrderId());
        vo.setPaymentMethod(payment.getPaymentMethod());
        vo.setPaymentAmount(payment.getPaymentAmount());
        vo.setPaymentStatus(payment.getPaymentStatus());
        vo.setOrderStatus(orderInfo.getOrderStatus());
        vo.setPayTime(payment.getPaymentTime());

        return vo;
    }

    /**
     * 主动查询支付宝交易并同步支付/订单状态
     * 仅在异步通知未到达时作为兜底，查询失败不影响本地状态返回
     */
    private void syncPaymentFromAlipay(OrderPayment payment, OrderInfo orderInfo) {
        try {
            AlipayTradeQueryResponse response = alipayService.queryTrade(payment.getPaymentNo());
            if (response == null || !response.isSuccess()) {
                return;
            }

            String tradeStatus = response.getTradeStatus();
            if (!"TRADE_SUCCESS".equals(tradeStatus) && !"TRADE_FINISHED".equals(tradeStatus)) {
                log.info("主动查询支付宝交易未成功，paymentNo={}, tradeStatus={}",
                        payment.getPaymentNo(), tradeStatus);
                return;
            }

            BigDecimal tradeAmount;
            try {
                tradeAmount = new BigDecimal(response.getTotalAmount());
            } catch (Exception e) {
                log.error("主动查询支付宝交易金额格式非法，paymentNo={}, totalAmount={}",
                        payment.getPaymentNo(), response.getTotalAmount());
                return;
            }

            if (tradeAmount.compareTo(payment.getPaymentAmount()) != 0
                    || tradeAmount.compareTo(orderInfo.getPayAmount()) != 0) {
                log.error("主动查询支付宝交易金额不一致，paymentNo={}, tradeAmount={}, paymentAmount={}, payAmount={}",
                        payment.getPaymentNo(), tradeAmount,
                        payment.getPaymentAmount(), orderInfo.getPayAmount());
                return;
            }

            String result = processPaymentSuccess(payment, orderInfo, response.getTradeNo());
            log.info("主动查询同步支付结果完成，paymentNo={}, result={}", payment.getPaymentNo(), result);
        } catch (Exception e) {
            // 查询失败时前端继续轮询，不把异常抛给查询接口
            log.error("主动查询支付宝交易状态失败，paymentNo={}", payment.getPaymentNo(), e);
        }
    }

    /**
     * 事务内处理支付成功逻辑：更新 order_payment + order_info 双表
     * 注意：仅处理TRADE_SUCCESS支付成功回调；幂等场景正常返回success，禁止无谓抛异常造成支付宝重试
     */
    public String processPaymentSuccess(OrderPayment payment, OrderInfo orderInfo, String tradeNo) {
        // 使用 TransactionTemplate 显式开启事务，避免同类自调用导致 @Transactional 失效
        return transactionTemplate.execute(status -> doProcessPaymentSuccess(payment, orderInfo, tradeNo));
    }

    private String doProcessPaymentSuccess(OrderPayment payment, OrderInfo orderInfo, String tradeNo) {
        LocalDateTime now = LocalDateTime.now();

        // ① 更新 order_payment：仅当状态=待支付(0)才更新
        int paymentRows = this.baseMapper.update(null,
                new UpdateWrapper<OrderPayment>()
                        .eq("payment_no", payment.getPaymentNo())
                        .eq("payment_status", 0)
                        .set("payment_status", 1)
                        .set("payment_time", now));

        // 场景：重复回调，支付单已经处理完成
        // 不直接返回，继续检查订单状态，兼容历史“支付单已成功但订单仍待付款”的部分更新
        if (paymentRows <= 0) {
            log.warn("【支付宝回调幂等】order_payment已完成更新，继续检查订单状态是否需要补偿，paymentNo={}", payment.getPaymentNo());
        }

        // ② 更新 order_info：仅当订单状态=待付款(1)才更新
        // 注意：OrderInfo 实体未映射 out_trade_no，若数据库也无该列会导致整条 SQL 失败，
        // 因此这里只更新订单状态，支付宝交易号通过 paymentNo 关联即可
        int orderRows = orderInfoMapper.update(null,
                new UpdateWrapper<OrderInfo>()
                        .eq("order_id", orderInfo.getOrderId())
                        .eq("order_status", 1)
                        .set("order_status", 2)
                        .set("update_time", now));

        if (orderRows <= 0) {
            log.warn("【支付宝回调幂等】order_info已完成更新，无需重复处理，orderId={}", orderInfo.getOrderId());
            // ⭐重点：此时支付单已经更新成功，属于重复回调，正常返回success，事务提交，不抛异常！
            return "success";
        }

        log.info("【支付宝回调】支付处理成功完成，paymentNo={}, orderId={}, tradeNo={}",
                payment.getPaymentNo(), orderInfo.getOrderId(), tradeNo);

        // 异步推送商家订单通知（后续可接入 MQ 或 @Async）
        // notifyMerchant(orderInfo);

        return "success";
    }
}
