package com.example.tomatomall.controller;

import com.example.tomatomall.po.COR;
import com.example.tomatomall.po.Product;
import com.example.tomatomall.repository.CORRepository;
import com.example.tomatomall.service.CartService;
import com.example.tomatomall.service.ProductService;
import com.example.tomatomall.vo.CartListVO;
import com.example.tomatomall.vo.CartVO;
import com.example.tomatomall.vo.OrderVO;
import com.example.tomatomall.vo.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.List;


@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    CartService cartService;

    /*@Autowired
    ProductService productService;*/

    @PostMapping
    public Response<CartVO> addToCart(@RequestBody CartVO cartVO) {
        CartVO cart = cartService.addToCart(cartVO.getProductid(), cartVO.getQuantity());
        return Response.buildSuccess(cart);
    }

    @DeleteMapping
    public Response<String> deleteFromCart(@PathVariable Integer cartItemId) {
        String result = cartService.deleteFromCart(cartItemId);
        return Response.buildSuccess(result);
    }

    @PatchMapping
    public Response<String> updateCart(@PathVariable Integer cartItemId, @RequestBody CartVO cartVO) {
        String result = cartService.updateCart(cartItemId, cartVO.getQuantity());
        return Response.buildSuccess(result);
    }

    @GetMapping
    public Response<CartListVO> getCart() {
        CartListVO cartList = cartService.getCart();
        return Response.buildSuccess(cartList);
    }

    @PatchMapping("/checkout")
    public Response<OrderVO> getOrder(@RequestParam List<Integer> cartItemIds, @RequestParam String paymentMethod ) {
        for(Integer cartItemId : cartItemIds) {
            // TODO
            //查找所有属于同样类型的商品,使用StockpileVO加锁,生成OrderVO
        }
        return Response.buildSuccess(new OrderVO());
    }
}
