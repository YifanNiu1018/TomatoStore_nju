package com.example.tomatomall.exception;

import com.example.tomatomall.vo.Response;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = TomatoMailException.class)
    public Response<String> handleTomatoMailException(TomatoMailException e) {
        e.printStackTrace();
        if (e.getMessage().equals("未登录")) {
            return Response.buildFailure(e.getMessage(), "401");
        }
        return Response.buildFailure(e.getMessage(), "400");
    }
}
