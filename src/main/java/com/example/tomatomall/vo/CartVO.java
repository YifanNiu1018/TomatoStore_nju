package com.example.tomatomall.vo;

import com.example.tomatomall.po.Cart;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class CartVO {
    private Integer cartitemid;
    private Integer productid;
    private String title;
    private BigDecimal price;
    private String description;
    private String cover;
    private Integer userid;
    private String detail;
    private Integer quantity;

    public Cart toPO() {
        Cart cart = new Cart();
        cart.setCartitemid(this.cartitemid);
        cart.setProductid(this.productid);
        cart.setUserid(this.userid);
        cart.setQuantity(this.quantity);
        return cart;
    }
}
