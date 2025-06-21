package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.exception.TomatoMailException;
import com.example.tomatomall.po.COR;
import com.example.tomatomall.po.Cart;
import com.example.tomatomall.po.Product;
import com.example.tomatomall.po.Stockpile;
import com.example.tomatomall.repository.CORRepository;
import com.example.tomatomall.repository.CartRepository;
import com.example.tomatomall.repository.ProductRepository;
import com.example.tomatomall.repository.StockpileRepository;
import com.example.tomatomall.service.StockService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class StockServiceImpl implements StockService {

    private final StockpileRepository stockpileRepository;

    private final ProductRepository productRepository;

    private final CartRepository cartRepository;

    private final CORRepository corRepository;

    public StockServiceImpl(StockpileRepository stockpileRepository, ProductRepository productRepository, CartRepository cartRepository, CORRepository corRepository) {
        this.stockpileRepository = stockpileRepository;
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;
        this.corRepository = corRepository;
    }


    @Override
    @Transactional
    public void reduceStock(Integer orderId) {
        List<COR> corList = corRepository.findByOrderId(orderId);
        for (COR cor : corList) {
            Cart cart = cartRepository.findById(cor.getCartItemId()).get();
            Product product = productRepository.findById(cart.getProductid()).get();
            Stockpile stockpile = stockpileRepository.findByProductId(product.getId());
            
            // 使用重试机制处理乐观锁冲突
            int maxRetries = 3;
            boolean success = false;
            
            for (int attempt = 0; attempt < maxRetries && !success; attempt++) {
                int oldVersion = stockpile.getVersion();
                int affectedRows = stockpileRepository.reduceStockWithVersion(cart.getProductid(), cart.getQuantity(), oldVersion);
                
                if (affectedRows > 0) {
                    success = true;
                    stockpile = stockpileRepository.findByProductId(product.getId()); // 重新获取最新状态
                    cartRepository.delete(cart);
                } else if (attempt < maxRetries - 1) {
                    // 短暂等待后重试
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    stockpile = stockpileRepository.findByProductId(product.getId()); // 刷新版本
                }
            }
            
            if (!success) {
                throw TomatoMailException.stockPileError();
            }
        }
    }
}
