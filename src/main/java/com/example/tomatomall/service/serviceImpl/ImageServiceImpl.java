package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.exception.TomatoMailException;
import com.example.tomatomall.service.ImageService;
import com.example.tomatomall.utils.OssUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class ImageServiceImpl implements ImageService {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "gif");
    
    final OssUtil ossUtil;

    public ImageServiceImpl(OssUtil ossUtil) {
        this.ossUtil = ossUtil;
    }

    @Override
    public String upload(MultipartFile file) {
        try {
            // 验证文件大小
            if (file.getSize() > MAX_FILE_SIZE) {
                throw TomatoMailException.fileUploadFail();
            }
            
            // 验证文件类型
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null ? 
                    originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase() : "";
            
            if (!ALLOWED_EXTENSIONS.contains(extension)) {
                throw TomatoMailException.fileUploadFail();
            }
            
            // 生成唯一文件名
            String uniqueFileName = UUID.randomUUID() + "." + extension;
            
            return ossUtil.upload(uniqueFileName, file.getInputStream());
        } catch (IOException e) {
            throw TomatoMailException.fileUploadFail();
        }
    }
}