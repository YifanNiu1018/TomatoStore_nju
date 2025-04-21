package com.example.tomatomall.service;

import com.example.tomatomall.vo.AdvertisementVO;

import java.util.List;

public interface AdvertisementService {
    List<AdvertisementVO> getAllAdvertisements();

    String updateAd(AdvertisementVO advertisementVO);

    AdvertisementVO createAd(AdvertisementVO advertisementVO);

    String deleteAd(Integer id);
}
