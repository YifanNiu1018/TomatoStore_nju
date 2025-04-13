package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.exception.TomatoMailException;
import com.example.tomatomall.po.Product;
import com.example.tomatomall.po.Specification;
import com.example.tomatomall.po.Stockpile;
import com.example.tomatomall.repository.ProductRepository;
import com.example.tomatomall.repository.SpecificationRepository;
import com.example.tomatomall.repository.StockpileRepository;
import com.example.tomatomall.service.ProductService;
import com.example.tomatomall.vo.ProductVO;
import com.example.tomatomall.vo.SpecificationVO;
import com.example.tomatomall.vo.StockpileVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SpecificationRepository specificationRepository;

    @Autowired
    private StockpileRepository stockpileRepository;

    @Override
    public List<ProductVO> getAllProducts() {
        List<Product> products = productRepository.findAll();
        List<ProductVO> productVOS = new ArrayList<>();

        for (Product product : products) {
            ProductVO productVO = product.toVO();
            List<Specification> specifications = specificationRepository.findByProductId(product.getId());
            List<SpecificationVO> specificationVOS = specifications.stream()
                    .map(Specification::toVO)
                    .collect(Collectors.toList());
            productVO.setSpecifications(specificationVOS);
            productVOS.add(productVO);
        }

        return productVOS;
    }

    @Override
    public ProductVO getProductById(Integer id) {
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            ProductVO productVO = product.toVO();

            List<Specification> specifications = specificationRepository.findByProductId(id);
            List<SpecificationVO> specificationVOS = specifications.stream()
                    .map(Specification::toVO)
                    .collect(Collectors.toList());
            productVO.setSpecifications(specificationVOS);

            return productVO;
        } else {
            throw TomatoMailException.productNotExist();
        }
    }

    @Override
    @Transactional
    public String updateProduct(ProductVO productVO) {
        Optional<Product> productOptional = productRepository.findById(productVO.getId());
        if (productOptional.isPresent()) {
            Product product = productOptional.get();

            // 更新产品基本信息
            if (productVO.getTitle() != null) {
                product.setTitle(productVO.getTitle());
            }
            if (productVO.getPrice() != null) {
                product.setPrice(productVO.getPrice());
            }
            if (productVO.getRate() != null) {
                product.setRate(productVO.getRate());
            }
            if (productVO.getDescription() != null) {
                product.setDescription(productVO.getDescription());
            }
            if (productVO.getCover() != null) {
                product.setCover(productVO.getCover());
            }
            if (productVO.getDetail() != null) {
                product.setDetail(productVO.getDetail());
            }

            productRepository.save(product);

            // 如果提供了规格信息则更新
            if (productVO.getSpecifications() != null && !productVO.getSpecifications().isEmpty()) {
                // 首先，删除现有规格
                List<Specification> existingSpecs = specificationRepository.findByProductId(product.getId());
                specificationRepository.deleteAll(existingSpecs);

                // 然后添加新的规格
                for (SpecificationVO specVO : productVO.getSpecifications()) {
                    Specification spec = specVO.toPO(product);
                    specificationRepository.save(spec);
                }
            }

            return "更新成功";
        } else {
            throw TomatoMailException.productNotExist();
        }
    }

    @Override
    @Transactional
    public ProductVO createProduct(ProductVO productVO) {
        // 首先保存产品信息
        Product product = productVO.toPO();
        product = productRepository.save(product);

        // 然后保存规格信息（如果有）
        if (productVO.getSpecifications() != null && !productVO.getSpecifications().isEmpty()) {
            for (SpecificationVO specVO : productVO.getSpecifications()) {
                Specification spec = specVO.toPO(product);
                specificationRepository.save(spec);
            }
        }

        // 初始化库存为0
        Stockpile stockpile = new Stockpile();
        stockpile.setProduct(product);
        stockpile.setAmount(0);
        stockpile.setFrozen(0);
        stockpileRepository.save(stockpile);

        // 返回创建的产品及其规格信息
        return getProductById(product.getId());
    }

    @Override
    @Transactional
    public String deleteProduct(Integer id) {
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isPresent()) {
            // 首先删除关联的库存信息
            Stockpile stockpile = stockpileRepository.findByProductId(id);
            if (stockpile != null) {
                stockpileRepository.delete(stockpile);
            }

            // 然后删除关联的规格信息
            List<Specification> specifications = specificationRepository.findByProductId(id);
            specificationRepository.deleteAll(specifications);

            // 最后删除产品
            productRepository.deleteById(id);

            return "删除成功";
        } else {
            throw TomatoMailException.productNotExist();
        }
    }

    @Override
    public String adjustStockpile(Integer productId, StockpileVO stockpileVO) {
        Optional<Product> productOptional = productRepository.findById(productId);
        if (productOptional.isPresent()) {
            Stockpile stockpile = stockpileRepository.findByProductId(productId);
            if (stockpile == null) {
                // 如果不存在，则创建新的库存记录
                stockpile = new Stockpile();
                stockpile.setProduct(productOptional.get());
            }

            // 更新库存信息
            if (stockpileVO.getAmount() != null) {
                stockpile.setAmount(stockpileVO.getAmount());
            }
            if (stockpileVO.getFrozen() != null) {
                stockpile.setFrozen(stockpileVO.getFrozen());
            }

            stockpileRepository.save(stockpile);
            return "调整库存成功";
        } else {
            throw TomatoMailException.productNotExist();
        }
    }

    @Override
    public StockpileVO getStockpile(Integer productId) {
        Optional<Product> productOptional = productRepository.findById(productId);
        if (productOptional.isPresent()) {
            Stockpile stockpile = stockpileRepository.findByProductId(productId);
            if (stockpile != null) {
                return stockpile.toVO();
            } else {
                // 如果未找到库存记录，则返回空库存
                StockpileVO stockpileVO = new StockpileVO();
                stockpileVO.setProductId(productId);
                stockpileVO.setAmount(0);
                stockpileVO.setFrozen(0);
                return stockpileVO;
            }
        } else {
            throw TomatoMailException.productNotExist();
        }
    }
}