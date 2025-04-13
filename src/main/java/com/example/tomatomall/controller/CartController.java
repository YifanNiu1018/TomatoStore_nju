package com.example.tomatomall.controller;

import com.example.tomatomall.service.CartService;
import com.example.tomatomall.vo.CartListVO;
import com.example.tomatomall.vo.CartVO;
import com.example.tomatomall.vo.OrderVO;
import com.example.tomatomall.vo.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    CartService cartService;

    @PostMapping
    public Response<CartVO> addToCart(@RequestBody CartVO cartVO) {
        CartVO cart = cartService.addToCart(cartVO.getProductid(), cartVO.getQuantity());
        return Response.buildSuccess(cart);
    }

    @DeleteMapping
    public Response<String> deleteFromCart(@PathVariable Integer cartitemid) {
        String result = cartService.deleteFromCart(cartitemid);
        return Response.buildSuccess(result);
    }

    @PatchMapping
    public Response<String> updateCart(@PathVariable Integer cartitemid, @RequestBody CartVO cartVO) {
        String result = cartService.updateCart(cartitemid, cartVO.getQuantity());
        return Response.buildSuccess(result);
    }

    @GetMapping
    public Response<CartListVO> getCart() {
        CartListVO cartList = cartService.getCart();
        return Response.buildSuccess(cartList);
    }

    @PatchMapping("/checkout")
    public Response<OrderVO> getOrder(@RequestParam List<Integer> cartItemIds,@RequestParam ) {


    }


}
