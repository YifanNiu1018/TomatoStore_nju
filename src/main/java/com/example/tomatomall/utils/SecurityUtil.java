package com.example.tomatomall.utils;

import com.example.tomatomall.po.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class SecurityUtil {

    @Autowired
    HttpServletRequest httpServletRequest;

    public Account getCurrentUser(){
        return (Account) httpServletRequest.getSession().getAttribute("currentUser");
    }
}
