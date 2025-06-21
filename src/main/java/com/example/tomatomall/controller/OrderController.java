package com.example.tomatomall.controller;

import com.alipay.api.internal.util.AlipaySignature;
import com.example.tomatomall.exception.TomatoMailException;
import com.example.tomatomall.service.StockService;
import com.example.tomatomall.vo.OrderVO;
import com.example.tomatomall.vo.Response;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import com.example.tomatomall.service.OrderService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    @Value("${alipay.alipayPublicKey}")
    private String ALIPAY_PUBLIC_KEY;

    private final StockService stockService;

    public OrderController(OrderService orderService, StockService stockService) {
        this.orderService = orderService;
        this.stockService = stockService;
    }

    @PostMapping("/{orderId}/pay")
    public Response<OrderItem> getProductById(@PathVariable Integer orderId) throws Exception {
        OrderVO orderVO = orderService.getOrderDetails(orderId);
        OrderItem orderItem = new OrderItem();
        orderItem.setOrderId(orderVO.getOrderId());
        orderItem.setTotalAmount(orderVO.getTotalAmount());
        orderItem.setPaymentMethod(orderVO.getPaymentMethod());
        orderItem.setPaymentForm(orderService.generateAlipayForm(orderId));
        return Response.buildSuccess(orderItem);
    }

    @PostMapping("/api/orders/notify")
    public void handleAlipayNotify(HttpServletRequest request, HttpServletResponse response) {
        try {
            // 1. 解析支付宝回调参数
            Map<String, String> params = request.getParameterMap().entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue()[0]));

            // 2. 验证支付宝签名
            boolean signVerified = AlipaySignature.rsaCheckV1(params, ALIPAY_PUBLIC_KEY, "UTF-8", "RSA2");
            if (!signVerified) {
                response.getWriter().print("fail");
                return;
            }

            // 3. 处理业务逻辑
            String tradeStatus = params.get("trade_status");
            if ("TRADE_SUCCESS".equals(tradeStatus)) {
                String orderId = params.get("out_trade_no");
                String amount = params.get("total_amount");

                // 使用事务确保原子性
                orderService.updateOrderStatus(Integer.parseInt(orderId), amount);
                stockService.reduceStock(Integer.valueOf(orderId));
            }

            // 4. 返回成功
            response.getWriter().print("success");
        } catch (Exception e) {
            throw TomatoMailException.AliPayError();
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class OrderItem {
        private String paymentForm;
        private int orderId;
        private BigDecimal totalAmount;
        private String paymentMethod;
    }
}


