package com.example.tomatomall.vo;

import com.example.tomatomall.po.COR;
import com.example.tomatomall.po.Cart;
import com.example.tomatomall.po.Order;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CORVO {
    private Integer id;
    private Integer cartItemId;
    private Integer orderId;
    private Cart cart;
    private Order order;


    public COR toPO(){
        COR cor = new COR();
        cor.setId(this.id);
        cor.setCartItemId(this.cartItemId);
        cor.setOrderId(this.orderId);
        cor.setCart(this.cart);
        cor.setOrder(this.order);
        return cor;
    }



}
