package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.exception.TomatoMailException;
import com.example.tomatomall.service.ImageService;
import com.example.tomatomall.utils.OssUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@Service
public class ImageServiceImpl implements ImageService{

    final
    OssUtil ossUtil;

    public ImageServiceImpl(OssUtil ossUtil) {
        this.ossUtil = ossUtil;
    }

    @Override
    public String upload(MultipartFile file) {
        try {
            return ossUtil.upload(file.getOriginalFilename(),file.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            throw TomatoMailException.fileUploadFail();
        }

    }
}
