package com.example.tomatomall.service;

import com.example.tomatomall.vo.CartListVO;
import com.example.tomatomall.vo.CartVO;

public interface CartService {
    CartVO addToCart(Integer productid, Integer quantity);

    String deleteFromCart(Integer cartitemid);

    String updateCart(Integer cartitemid, Integer quantity);

    CartListVO getCart();
}
