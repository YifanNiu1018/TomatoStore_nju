package com.example.tomatomall.po;

import com.example.tomatomall.vo.CORVO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "carts_orders_relation")
public class COR {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "cart_item_id", nullable = false)
    private Integer cartItemId;

    @Column(name = "order_id", nullable = false)
    private Integer orderId;

    @ManyToOne
    @JoinColumn(name = "cart_item_id", insertable = false, updatable = false)
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "order_id", insertable = false, updatable = false)
    private Order order;

    public CORVO toVO() {
        CORVO corvo = new CORVO();
        corvo.setId(this.id);
        corvo.setCartItemId(this.cartItemId);
        corvo.setOrderId(this.orderId);
        corvo.setCart(this.cart);
        corvo.setOrder(this.order);
        return corvo;
    }
}
