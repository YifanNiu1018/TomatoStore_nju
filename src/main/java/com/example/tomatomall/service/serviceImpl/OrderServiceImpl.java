package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.po.COR;
import com.example.tomatomall.po.Order;
import com.example.tomatomall.repository.CORRepository;
import com.example.tomatomall.repository.CartRepository;
import com.example.tomatomall.repository.OrderRepository;
import com.example.tomatomall.service.OrderService;
import com.example.tomatomall.vo.OrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CORRepository corRepository;

    @Transactional
    public OrderVO createOrder(Integer userId, List<Integer> cartItemIds, BigDecimal totalAmount, String paymentMethod) {
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
    }

    public List<OrderVO> getUserOrders(Integer userId) {
        return orderRepository.findByUserId(userId)
                .stream()
                .map(Order::toVO)
                .collect(Collectors.toList());
    }

    public OrderVO updateOrderStatus(Integer orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(status);
        return orderRepository.save(order).toVO();
    }

    public OrderVO getOrderDetails(Integer orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"))
                .toVO();
    }
}
