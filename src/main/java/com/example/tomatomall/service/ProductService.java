package com.example.tomatomall.service;

import com.example.tomatomall.vo.ProductVO;
import com.example.tomatomall.vo.StockpileVO;

import java.util.List;

public interface ProductService {
    List<ProductVO> getAllProducts();

    ProductVO getProductById(Integer id);

    String updateProduct(ProductVO productVO);

    ProductVO createProduct(ProductVO productVO);

    String deleteProduct(Integer id);

    String adjustStockpile(Integer productId, StockpileVO stockpileVO);

    StockpileVO getStockpile(Integer productId);
}
