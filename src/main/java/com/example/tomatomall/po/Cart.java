package com.example.tomatomall.po;

import com.example.tomatomall.vo.CartVO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "carts")
public class Cart {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "cart_item_id")
    private Integer cartitemid;

    @Column(name = "user_id")
    private Integer userid;

    @Column(name = "product_id")
    private Integer productid;

    @Column(name = "quantity")
    private Integer quantity;

    public CartVO toVO() {
        CartVO cartVO = new CartVO();
        cartVO.setCartitemid(this.cartitemid);
        cartVO.setUserid(this.userid);
        cartVO.setProductid(this.productid);
        cartVO.setQuantity(this.quantity);
        return cartVO;
    }
}
