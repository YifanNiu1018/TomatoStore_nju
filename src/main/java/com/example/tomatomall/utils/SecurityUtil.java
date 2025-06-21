package com.example.tomatomall.utils;

import com.example.tomatomall.po.User;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class SecurityUtil {

    final
    HttpServletRequest httpServletRequest;

    public SecurityUtil(HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    public User getCurrentUser(){
        return (User) httpServletRequest.getSession().getAttribute("currentUser");
    }
}
