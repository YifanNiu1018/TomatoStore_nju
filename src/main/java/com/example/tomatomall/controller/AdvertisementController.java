package com.example.tomatomall.controller;

import com.example.tomatomall.service.AdvertisementService;
import com.example.tomatomall.vo.AdvertisementVO;
import com.example.tomatomall.vo.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/advertisements")
public class AdvertisementController {

    @Autowired
    private AdvertisementService advertisementService;

    @GetMapping
    public Response<List<AdvertisementVO>> getAllAdvertisements() {
        List<AdvertisementVO> result = advertisementService.getAllAdvertisements();
        return Response.buildSuccess(result);
    }

    @PutMapping
    public Response<String> updateAd(@RequestBody AdvertisementVO advertisementVO) {
        String result = advertisementService.updateAd(advertisementVO);
        return Response.buildSuccess(result);
    }

    @PostMapping
    public Response<AdvertisementVO> createAd(@RequestBody AdvertisementVO advertisementVO) {
        AdvertisementVO result = advertisementService.createAd(advertisementVO);
        return Response.buildSuccess(result);
    }

    @DeleteMapping("/{id}")
    public Response<String> deleteAd(@PathVariable Integer id) {
        String result = advertisementService.deleteAd(id);
        return Response.buildSuccess(result);
    }

}
