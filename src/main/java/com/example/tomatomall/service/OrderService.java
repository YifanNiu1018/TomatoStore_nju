package com.example.tomatomall.service;


import com.example.tomatomall.vo.OrderVO;

import java.math.BigDecimal;
import java.util.List;

public interface OrderService {
    public OrderVO createOrder(Integer userId, List<Integer> cartItemIds, BigDecimal totalAmount, String paymentMethod);

    public List<OrderVO> getUserOrders(Integer userId);

    public OrderVO updateOrderStatus(Integer orderId, String status);

    public OrderVO getOrderDetails(Integer orderId);

}
