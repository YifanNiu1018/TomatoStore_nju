package com.example.tomatomall.service.serviceImpl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.example.tomatomall.configure.AliPayConfig;
import com.example.tomatomall.exception.TomatoMailException;
import com.example.tomatomall.po.*;
import com.example.tomatomall.repository.*;
import com.example.tomatomall.service.OrderService;
import com.example.tomatomall.vo.CartVO;
import com.example.tomatomall.vo.OrderVO;
import org.bouncycastle.jcajce.provider.asymmetric.rsa.CipherSpi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StockpileRepository stockpileRepository;

    @Autowired
    private CORRepository corRepository;

    @Autowired
    private UserRepository userRepository;

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

    @Override
    public OrderVO createOrder(Integer userId, List<Integer> cartItemIds, String paymentMethod) {
        List<Cart> carts = cartItemIds.stream()
                .map(cartItemId -> cartRepository.findByCartidAndUserid(cartItemId, userId))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        //检查库存
        for (Cart cart : carts) {
            Product product = productRepository.findById(cart.getProductid()).orElse(null);
            if (product == null) {
                throw TomatoMailException.productNotExist();
            }
            Stockpile stockpile = stockpileRepository.findByProductId(product.getId());
            if (stockpile.getAmount() < cart.getQuantity()) {
                throw TomatoMailException.stockpileNotEnough();
            }
        }

        //计算总金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (Cart cart : carts) {
            Product product = productRepository.findById(cart.getProductid()).orElse(null);
            if (product != null) {
                totalAmount = totalAmount.add(product.getPrice().multiply(new BigDecimal(cart.getQuantity())));
            }
        }

        //创建订单
        Order order = new Order();
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setPaymentMethod(paymentMethod);
        order.setCreateTime(LocalDateTime.now());
        order.setStatus("待支付");
        Order savedOrder = orderRepository.save(order);
        orderRepository.save(order);
        //保存订单和购物车的关系
        for (Cart cart : carts) {
            COR cor = new COR();
            cor.setCartItemId(cart.getCartitemid());
            cor.setOrderId(savedOrder.getOrderId());
            corRepository.save(cor);
        }

        //锁库存
        for (Cart cart : carts) {
            Product product = productRepository.findById(cart.getProductid()).orElse(null);
            if (product == null) {
                throw TomatoMailException.productNotExist();
            }
            Stockpile stockpile = stockpileRepository.findByProductId(product.getId());
            stockpile.setFrozen(stockpile.getFrozen() + cart.getQuantity());
            stockpileRepository.save(stockpile);
        }

        return ConvertToOrderVO(savedOrder);

    }

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

    private OrderVO ConvertToOrderVO(Order order) {
        OrderVO orderVO = new OrderVO();
        orderVO.setOrderId(order.getOrderId());
        orderVO.setUserName(userRepository.findById(order.getUserId()).get().getUsername());
        orderVO.setTotalAmount(order.getTotalAmount());
        orderVO.setPaymentMethod(order.getPaymentMethod());
        orderVO.setCreateTime(order.getCreateTime());
        orderVO.setStatus(order.getStatus());
        return orderVO;
    }
}
