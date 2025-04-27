package com.example.tomatomall.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
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
import java.io.IOException;
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
    public void handleAlipayNotify(HttpServletRequest request, HttpServletResponse response) throws IOException, AlipayApiException {
        // 1. 解析支付宝回调参数（通常是 application/x-www-form-urlencoded）
        Map<String, String> params = request.getParameterMap().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue()[0]));

        // 2. 验证支付宝签名（防止伪造请求）
        boolean signVerified = AlipaySignature.rsaCheckV1(params, ALIPAY_PUBLIC_KEY, "UTF-8", "RSA2");
        if (!signVerified) {
            response.getWriter().print("fail"); // 签名验证失败，返回 fail
            return;
        }

        // 3. 处理业务逻辑（更新订单、减库存等）
        String tradeStatus = params.get("trade_status");
        if ("TRADE_SUCCESS".equals(tradeStatus)) {
            String orderId = params.get("out_trade_no"); // 您的订单号
            String amount = params.get("total_amount"); // 支付金额

            // 更新订单状态（注意幂等性，防止重复处理）
            orderService.updateOrderStatus(Integer.parseInt(orderId), amount);

            // 扣减库存（建议加锁或乐观锁）
            // TODO
            // 使用ProductService中的adjustStockpile函数来调整,可能需要重新实现这函数,逻辑可能有误
            //inventoryService.reduceStock(orderId);
            stockService.reduceStock(Integer.valueOf(orderId));
        }

        // 4. 必须返回纯文本的 "success"（支付宝要求）
        response.getWriter().print("success");
    }

    @Getter
    @Setter
    @NoArgsConstructor
    private static class OrderItem {
        private String paymentForm;
        private int orderId;
        private BigDecimal totalAmount;
        private String paymentMethod;
    }
}


