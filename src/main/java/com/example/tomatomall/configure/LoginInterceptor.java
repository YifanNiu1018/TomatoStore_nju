package com.example.tomatomall.configure;

import com.example.tomatomall.exception.TomatoMailException;
import com.example.tomatomall.utils.TokenUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    final
    TokenUtil tokenUtil;

    public LoginInterceptor(TokenUtil tokenUtil) {
        this.tokenUtil = tokenUtil;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) {
        String token = request.getHeader("token");
        if (token != null && tokenUtil.verifyToken(token)) {
            request.getSession().setAttribute("currentUser",tokenUtil.getUser(token));
            return true;
        }else {
            throw TomatoMailException.notLogin();
        }
    }

}
