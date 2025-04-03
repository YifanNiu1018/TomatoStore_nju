package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.exception.TomatoMailException;
import com.example.tomatomall.po.Cart;
import com.example.tomatomall.po.Product;
import com.example.tomatomall.po.Stockpile;
import com.example.tomatomall.repository.CartRepository;
import com.example.tomatomall.repository.ProductRepository;
import com.example.tomatomall.repository.StockpileRepository;
import com.example.tomatomall.service.CartService;
import com.example.tomatomall.utils.SecurityUtil;
import com.example.tomatomall.vo.CartListVO;
import com.example.tomatomall.vo.CartVO;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CartServiceImpl implements CartService {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    StockpileRepository stockpileRepository;

    @Autowired
    SecurityUtil securityUtil;

    @Autowired
    CartRepository cartRepository;

    @Override
    public CartVO addToCart(Integer productId, Integer count) {
        Integer userId = securityUtil.getCurrentUser().getId();
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            throw  TomatoMailException.productNotExist();
        }

        //检查库存
        Stockpile stockpile = stockpileRepository.findByProductId(productId);
        if (stockpile.getAmount() < count) {
            throw TomatoMailException.stockpileNotEnough();
        }

        //检查是否已经在购物车中
        if (cartRepository.findByProductIdAndUserId(productId, userId) != null) {
            Cart cart = cartRepository.findByProductIdAndUserId(productId, userId);
            cart.setQuantity(cart.getQuantity() + count);
            cartRepository.save(cart);
        }

        //如果不在购物车中，创建新的购物车项
        Cart newcart = new Cart();
        newcart.setProductid(productId);
        newcart.setUserid(userId);
        newcart.setQuantity(count);
        cartRepository.save(newcart);
        return convertToCartVO(newcart, product);
    }

    @Override
    public String deleteFromCart(Integer cartitemid) {
        Integer userId = securityUtil.getCurrentUser().getId();
        Cart cart = cartRepository.findById(cartitemid).orElse(null);
        if (cart == null) {
            throw TomatoMailException.cartItemNotExist();
        }
        if (!cart.getUserid().equals(userId)) {
            throw TomatoMailException.cartItemNotBelongToUser();
        }
        cartRepository.delete(cart);
        return null;
    }

    @Override
    public String updateCart(Integer cartitemid, Integer quantity) {
        Integer userId = securityUtil.getCurrentUser().getId();
        Cart cart = cartRepository.findById(cartitemid).orElse(null);
        if (cart == null) {
            throw TomatoMailException.cartItemNotExist();
        }
        if (!cart.getUserid().equals(userId)) {
            throw TomatoMailException.cartItemNotBelongToUser();
        }
        //检查库存
        Stockpile stockpile = stockpileRepository.findByProductId(cart.getProductid());
        if (stockpile.getAmount() < quantity) {
            throw TomatoMailException.stockpileNotEnough();
        }
        cart.setQuantity(quantity);
        cartRepository.save(cart);
        return "修改数量成功";
    }

    @Override
    public CartListVO getCart() {
        Integer userId = securityUtil.getCurrentUser().getId();
        List<Cart> cartList = cartRepository.findByUserId(userId);
        if (cartList == null || cartList.isEmpty()) {
            return null;
        }
        List<CartVO> cartVOList = new ArrayList<>();
        for (Cart cart : cartList) {
            Product product = productRepository.findById(cart.getProductid()).orElse(null);
            if (product != null) {
                CartVO cartVO = convertToCartVO(cart, product);
                cartVOList.add(cartVO);
            }
        }
        if (cartVOList.isEmpty()) {
            return null;
        }
        // 计算总价
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (CartVO cartVO : cartVOList) {
            totalPrice = totalPrice.add(cartVO.getPrice().multiply(new BigDecimal(cartVO.getQuantity())));
        }
        CartListVO cartListVO = new CartListVO();
        cartListVO.setTotalAmount(totalPrice);
        cartListVO.setTotal(cartVOList.size());
        cartListVO.setItems(cartVOList);
        return cartListVO;
    }

    private CartVO convertToCartVO(Cart cart, Product product) {
        CartVO cartVO = new CartVO();
        cartVO.setCartitemid(cart.getCartitemid());
        cartVO.setProductid(cart.getProductid());
        cartVO.setUserid(cart.getUserid());
        cartVO.setQuantity(cart.getQuantity());
        cartVO.setTitle(product.getTitle());
        cartVO.setPrice(product.getPrice());
        cartVO.setDescription(product.getDescription());
        cartVO.setCover(product.getCover());
        cartVO.setDetail(product.getDetail());
        return cartVO;
    }
}
