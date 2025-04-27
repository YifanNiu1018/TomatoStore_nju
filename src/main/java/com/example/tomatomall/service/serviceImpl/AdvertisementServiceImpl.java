package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.exception.TomatoMailException;
import com.example.tomatomall.po.Advertisement;
import com.example.tomatomall.po.Product;
import com.example.tomatomall.repository.AdvertisementRepository;
import com.example.tomatomall.repository.ProductRepository;
import com.example.tomatomall.service.AdvertisementService;
import com.example.tomatomall.vo.AdvertisementVO;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdvertisementServiceImpl implements AdvertisementService {

    private final AdvertisementRepository advertisementRepository;

    private final ProductRepository productRepository;

    public AdvertisementServiceImpl(AdvertisementRepository advertisementRepository, ProductRepository productRepository) {
        this.advertisementRepository = advertisementRepository;
        this.productRepository = productRepository;
    }

    @Override
    public List<AdvertisementVO> getAllAdvertisements() {
        List<Advertisement> advertisements = advertisementRepository.findAll();
        if (advertisements.isEmpty()) {
            return Collections.emptyList();
        }
        return advertisements.stream().map(Advertisement::toVO).collect(Collectors.toList());
    }

    @Override
    public String updateAd(AdvertisementVO advertisementVO) {
        Advertisement advertisement = advertisementRepository.findById(advertisementVO.getId())
                .orElseThrow(TomatoMailException::advertisementNotFound);
        if (advertisementVO.getId() != null) {
            advertisement.setId(advertisementVO.getId());
        }
        if (advertisementVO.getContent() != null) {
            advertisement.setContent(advertisementVO.getContent());
        }
        if (advertisementVO.getImageUrl() != null) {
            advertisement.setImageUrl(advertisementVO.getImageUrl());
        }
        if (advertisementVO.getProductId() != null) {
            // Check if the product exists
            Product product = productRepository.findById(advertisementVO.getProductId()).orElse(null);
            if (product == null) {
                throw TomatoMailException.productNotExist();
            }
            advertisement.setProductId(advertisementVO.getProductId());
        }
        if (advertisementVO.getTitle() != null) {
            advertisement.setTitle(advertisementVO.getTitle());
        }
        advertisementRepository.save(advertisement);
        return "更新成功";
    }

    @Override
    public AdvertisementVO createAd(AdvertisementVO advertisementVO) {
        // Check if the product exists
        Product product = productRepository.findById(advertisementVO.getProductId()).orElse(null);
        if (product == null) {
            throw TomatoMailException.productNotExist();
        }
        Advertisement advertisement = advertisementVO.toPO();
        advertisementRepository.save(advertisement);
        // Return the saved advertisement as a VO
        AdvertisementVO savedAdvertisementVO = new AdvertisementVO();
        savedAdvertisementVO.setId(advertisement.getId());
        savedAdvertisementVO.setTitle(advertisement.getTitle());
        savedAdvertisementVO.setContent(advertisement.getContent());
        savedAdvertisementVO.setImageUrl(advertisement.getImageUrl());
        savedAdvertisementVO.setProductId(advertisement.getProductId());
        return savedAdvertisementVO;
    }

    @Override
    public String deleteAd(Integer id) {
        Advertisement advertisement = advertisementRepository.findById(id)
                .orElseThrow(TomatoMailException::advertisementNotFound);
        advertisementRepository.delete(advertisement);
        return "删除成功";
    }
}
