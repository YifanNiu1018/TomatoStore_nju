package com.example.tomatomall.controller;

import com.example.tomatomall.po.COR;
import com.example.tomatomall.po.Product;
import com.example.tomatomall.repository.CORRepository;
import com.example.tomatomall.service.CartService;
import com.example.tomatomall.service.OrderService;
import com.example.tomatomall.service.ProductService;
import com.example.tomatomall.utils.SecurityUtil;
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

    @Autowired
    ProductService productService;

    @Autowired
    SecurityUtil securityUtil;

    @Autowired
    OrderService orderService;

    @PostMapping
    public Response<CartVO> addToCart(@RequestBody CartVO cartVO) {
        CartVO cart = cartService.addToCart(cartVO.getProductid(), cartVO.getQuantity());
        return Response.buildSuccess(cart);
    }

    @DeleteMapping("/{cartItemId}")
    public Response<String> deleteFromCart(@PathVariable Integer cartItemId) {
        String result = cartService.deleteFromCart(cartItemId);
        return Response.buildSuccess(result);
    }

    @PatchMapping("/item/{cartItemId}")
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
        OrderVO order = orderService.createOrder(securityUtil.getCurrentUser().getId(), cartItemIds, paymentMethod);
        return Response.buildSuccess(order);
    }
}
