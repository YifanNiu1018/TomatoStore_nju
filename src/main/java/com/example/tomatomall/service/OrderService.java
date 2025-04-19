package com.example.tomatomall.service;


import com.example.tomatomall.vo.OrderVO;

import java.util.List;


public interface OrderService {
    OrderVO createOrder(Integer userId, List<Integer> cartItemIds, String paymentMethod);

    //List<OrderVO> getUserOrders(Integer userId);

    void updateOrderStatus(Integer orderId, String status);

    OrderVO getOrderDetails(Integer orderId);

    String generateAlipayForm(Integer orderId) throws Exception;

}
