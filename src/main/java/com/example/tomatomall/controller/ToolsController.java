package com.example.tomatomall.controller;

import com.example.tomatomall.service.ImageService;
import com.example.tomatomall.vo.Response;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class ToolsController {
    final
    ImageService imageService;

    public ToolsController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping("/images")
    public Response<String> upload(@RequestParam MultipartFile multipartFile) {
        return Response.buildSuccess(imageService.upload(multipartFile));
    }
}
