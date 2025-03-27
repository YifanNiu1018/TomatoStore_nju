package com.example.tomatomall.exception;

import com.example.tomatomall.vo.Response;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = TomatoMailException.class)
    public Response<String> handleTomatoMailException(TomatoMailException e) {
        return Response.buildFailure(e.getMessage(), "400");
    }
}
