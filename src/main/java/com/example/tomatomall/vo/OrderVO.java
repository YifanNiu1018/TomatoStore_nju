package com.example.tomatomall.vo;

import com.example.tomatomall.po.COR;
import com.example.tomatomall.po.Order;
import com.example.tomatomall.po.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class OrderVO {
    private Integer orderId;
    private Integer userId;
    private String userName;
    private BigDecimal totalAmount;
    private String paymentMethod;
    private String status = "PENDING";
    private LocalDateTime createTime;
    private List<COR> CORS;
    private User user;

    public Order toPO() {
        Order order = new Order();
        order.setOrderId(this.orderId);
        order.setUserId(this.userId);
        order.setTotalAmount(this.totalAmount);
        order.setPaymentMethod(this.paymentMethod);
        order.setStatus(this.status);
        order.setCreateTime(this.createTime);
        order.setUser(this.user);
        order.setCORS(this.CORS);
        return order;
    }
}