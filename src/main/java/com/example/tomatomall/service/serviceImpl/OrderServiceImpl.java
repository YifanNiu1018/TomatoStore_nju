package com.example.tomatomall.service.serviceImpl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.example.tomatomall.configure.AliPayConfig;
import com.example.tomatomall.po.Order;
import com.example.tomatomall.repository.OrderRepository;
import com.example.tomatomall.service.OrderService;
import com.example.tomatomall.vo.OrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Resource
    AliPayConfig aliPayConfig;

    /*@Autowired
    private CORRepository corRepository;*/

    private static final String GATEWAY_URL ="https://openapi-sandbox.dl.alipaydev.com/gateway.do";
    private static final String FORMAT ="JSON";
    private static final String CHARSET ="utf-8";
    private static final String SIGN_TYPE ="RSA2";

    /*@Transactional
    public OrderVO createOrder(Integer userId, List<Integer> cartItemIds, Integer totalAmount, String paymentMethod) {
        Order order = new Order();
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setPaymentMethod(paymentMethod);
        order.setCreateTime(LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);

        for (Integer cartItemId : cartItemIds) {
            COR cor = new COR();
            cor.setCartItemId(cartItemId);
            cor.setOrderId(savedOrder.getOrderId());
            corRepository.save(cor);
        }

        return savedOrder.toVO();
    }*/

    /*public List<OrderVO> getUserOrders(Integer userId) {
        return orderRepository.findByUserId(userId)
                .stream()
                .map(Order::toVO)
                .collect(Collectors.toList());
    }*/

    public void updateOrderStatus(Integer orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(status);
        orderRepository.save(order).toVO();
    }

    public OrderVO getOrderDetails(Integer orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"))
                .toVO();
    }

    @Override
    public String generateAlipayForm(Integer orderId) throws Exception {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        AlipayClient alipayClient = new DefaultAlipayClient(GATEWAY_URL, aliPayConfig.getAppId(),
                aliPayConfig.getAppPrivateKey(), FORMAT, CHARSET, aliPayConfig.getAlipayPublicKey(), SIGN_TYPE);
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setNotifyUrl(aliPayConfig.getNotifyUrl());
        request.setBizContent(
                "\"total_amount\":\"" + order.getTotalAmount() + "\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");
        String form;
        try {
            form = alipayClient.pageExecute(request).getBody();
            return form;
        } catch (AlipayApiException e) {
            throw new Exception("Failed to generate Alipay form: " + e.getMessage());
        }
    }
}
