package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.exception.TomatoMailException;
import com.example.tomatomall.po.COR;
import com.example.tomatomall.po.Cart;
import com.example.tomatomall.po.Order;
import com.example.tomatomall.po.Product;
import com.example.tomatomall.po.Stockpile;
import com.example.tomatomall.repository.CORRepository;
import com.example.tomatomall.repository.CartRepository;
import com.example.tomatomall.repository.OrderRepository;
import com.example.tomatomall.repository.ProductRepository;
import com.example.tomatomall.repository.StockpileRepository;
import com.example.tomatomall.service.CartService;
import com.example.tomatomall.utils.SecurityUtil;
import com.example.tomatomall.vo.CartListVO;
import com.example.tomatomall.vo.CartVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    final
    ProductRepository productRepository;

    final
    StockpileRepository stockpileRepository;

    final
    SecurityUtil securityUtil;

    final
    CartRepository cartRepository;

    final
    CORRepository corRepository;

    final
    OrderRepository orderRepository;

    public CartServiceImpl(ProductRepository productRepository, StockpileRepository stockpileRepository, SecurityUtil securityUtil, CartRepository cartRepository, CORRepository corRepository, OrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.stockpileRepository = stockpileRepository;
        this.securityUtil = securityUtil;
        this.cartRepository = cartRepository;
        this.corRepository = corRepository;
        this.orderRepository = orderRepository;
    }

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
        Cart existingCart = cartRepository.findByProductidAndUserid(productId, userId);
        if (existingCart != null) {
            existingCart.setQuantity(existingCart.getQuantity() + count);
            cartRepository.save(existingCart);
            return convertToCartVO(existingCart, product);
        }

        //如果不在购物车中，创建新的购物车项
        Cart newCart = new Cart();
        newCart.setProductid(productId);
        newCart.setUserid(userId);
        newCart.setQuantity(count);
        cartRepository.save(newCart);
        return convertToCartVO(newCart, product);
    }

    @Override
    @Transactional
    public String deleteFromCart(Integer cartitemid) {
        Integer userId = securityUtil.getCurrentUser().getId();
        Cart cart = cartRepository.findById(cartitemid).orElse(null);
        if (cart == null) {
            throw TomatoMailException.cartItemNotExist();
        }
        if (!cart.getUserid().equals(userId)) {
            throw TomatoMailException.cartItemNotBelongToUser();
        }

        // 先删除相关的COR记录（如果存在）
        List<COR> relatedCORs = corRepository.findByCartItemId(cartitemid);
        if (!relatedCORs.isEmpty()) {
            // 检查关联的订单状态，如果订单已支付，不允许删除
            for (COR cor : relatedCORs) {
                Order order = orderRepository.findById(cor.getOrderId()).orElse(null);
                if (order != null && "PAID".equals(order.getStatus())) {
                    throw new RuntimeException("无法删除已支付订单中的商品");
                }
            }
            // 删除COR记录
            corRepository.deleteAll(relatedCORs);

            // 释放锁定的库存
            for (COR cor : relatedCORs) {
                Order order = orderRepository.findById(cor.getOrderId()).orElse(null);
                if (order != null && "PENDING".equals(order.getStatus())) {
                    Product product = productRepository.findById(cart.getProductid()).orElse(null);
                    if (product != null) {
                        Stockpile stockpile = stockpileRepository.findByProductId(product.getId());
                        if (stockpile != null) {
                            stockpile.setFrozen(stockpile.getFrozen() - cart.getQuantity());
                            stockpileRepository.save(stockpile);
                        }
                    }
                }
            }
        }

        cartRepository.delete(cart);
        return "删除成功";
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
        List<Cart> cartList = cartRepository.findByUserid(userId);
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
