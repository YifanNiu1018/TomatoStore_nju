package com.example.tomatomall.po;

import com.example.tomatomall.vo.OrderVO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Integer orderId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "payment_method", nullable = false)
    private String paymentMethod;

    @Column(name = "status", nullable = false)
    private String status = "PENDING";

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @OneToMany(mappedBy = "order")
    private List<COR> CORS;

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    public OrderVO toVO() {
        OrderVO vo = new OrderVO();
        vo.setOrderId(this.orderId);
        vo.setUserId(this.userId);
        vo.setTotalAmount(this.totalAmount);
        vo.setPaymentMethod(this.paymentMethod);
        vo.setStatus(this.status);
        vo.setCreateTime(this.createTime);
        vo.setUser(this.user);
        vo.setCORS(this.CORS);
        return vo;
    }
}