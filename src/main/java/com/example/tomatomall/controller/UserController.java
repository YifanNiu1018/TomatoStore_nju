package com.example.tomatomall.controller;

import com.example.tomatomall.service.UserService;
import com.example.tomatomall.vo.Response;
import com.example.tomatomall.vo.UserVO;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/accounts")
public class UserController {

    @Resource
    UserService userService;

    /**
     * 获取用户详情
     */
    @GetMapping("/{username}")
    public Response<UserVO> getUser(@PathVariable String username) {
        return Response.buildSuccess(userService.getUser(username));
    }

    /**
     * 创建新的用户
     */
    @PostMapping()
    public Response<String> createUser(@RequestBody UserVO userVO) {
        return Response.buildSuccess(userService.createUser(userVO));
    }

    /**
     * 更新用户信息
     */
    @PutMapping()
    public Response<String> updateUser(@RequestBody UserVO userVO) {
        return Response.buildSuccess(userService.updateUser(userVO));
    }

    /**
     * 登录
     */
    @PostMapping("/login")
    public Response<String> login(@RequestBody UserVO userVO) {
        return Response.buildSuccess(userService.login(userVO.getUsername(), userVO.getPassword()));
    }
}
