package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.po.COR;
import com.example.tomatomall.po.Cart;
import com.example.tomatomall.po.Product;
import com.example.tomatomall.po.Stockpile;
import com.example.tomatomall.repository.CORRepository;
import com.example.tomatomall.repository.CartRepository;
import com.example.tomatomall.repository.ProductRepository;
import com.example.tomatomall.repository.StockpileRepository;
import com.example.tomatomall.service.StockService;
import com.example.tomatomall.utils.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class StockServiceImpl implements StockService {

    @Autowired
    private StockpileRepository stockpileRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CORRepository corRepository;



    @Override
    @Transactional
    public void reduceStock(Integer orderId) {
        List<COR> corList = corRepository.findByOrderId(orderId);
        for (COR cor : corList) {
            Cart cart = cartRepository.findById(cor.getCartItemId()).get();
            Product product = productRepository.findById(cart.getProductid()).get();
            Stockpile stockpile = stockpileRepository.findByProductId(product.getId());
            stockpile.setAmount(stockpile.getAmount() - cart.getQuantity());
            stockpileRepository.save(stockpile);
            //更新购物车
            cartRepository.delete(cart);
        }
    }
}
