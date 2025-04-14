package com.example.tomatomall.controller;

import com.example.tomatomall.service.ProductService;
import com.example.tomatomall.vo.ProductVO;
import com.example.tomatomall.vo.Response;
import com.example.tomatomall.vo.StockpileVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    // 获取商品列表
    @GetMapping
    public Response<List<ProductVO>> getAllProducts() {
        List<ProductVO> products = productService.getAllProducts();
        return Response.buildSuccess(products);
    }

    // 获取指定商品信息
    @GetMapping("/{id}")
    public Response<ProductVO> getProductById(@PathVariable Integer id) {
        try {
            ProductVO product = productService.getProductById(id);
            return Response.buildSuccess(product);
        } catch (Exception e) {
            return Response.buildFailure("商品不存在", "400");
        }
    }

    // 更新商品信息
    @PutMapping
    public Response<String> updateProduct(@RequestBody ProductVO productVO) {
        try {
            String result = productService.updateProduct(productVO);
            return Response.buildSuccess(result);
        } catch (Exception e) {
            return Response.buildFailure("商品不存在", "400");
        }
    }

    // 增加商品
    @PostMapping
    public Response<ProductVO> createProduct(@RequestBody ProductVO productVO) {
        ProductVO createdProduct = productService.createProduct(productVO);
        return Response.buildSuccess(createdProduct);
    }

    // 删除商品
    @DeleteMapping("/{id}")
    public Response<String> deleteProduct(@PathVariable Integer id) {
        try {
            String result = productService.deleteProduct(id);
            return Response.buildSuccess(result);
        } catch (Exception e) {
            return Response.buildFailure("商品不存在", "400");
        }
    }

    // 调整指定商品的库存
    @PatchMapping("/stockpile/{productId}")
    public Response<String> adjustStockpile(@PathVariable Integer productId,
                                            @RequestBody StockpileVO stockpileVO) {
        try {
            String result = productService.adjustStockpile(productId, stockpileVO);
            return Response.buildSuccess(result);
        } catch (Exception e) {
            return Response.buildFailure("商品不存在", "400");
        }
    }

    // 查询指定商品的库存
    @GetMapping("/stockpile/{productId}")
    public Response<StockpileVO> getStockpile(@PathVariable Integer productId) {
        try {
            StockpileVO stockpile = productService.getStockpile(productId);
            return Response.buildSuccess(stockpile);
        } catch (Exception e) {
            return Response.buildFailure("商品不存在", "400");
        }
    }
}