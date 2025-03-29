package com.example.tomatomall.controller;

import com.example.tomatomall.service.AccountService;
import com.example.tomatomall.vo.Response;
import com.example.tomatomall.vo.AccountVO;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Resource
    AccountService accountService;

    /**
     * 获取用户详情
     */
    @GetMapping("/{username}")
    public Response<AccountVO> getUser(@PathVariable String username) {
        return Response.buildSuccess(accountService.getUser(username));
    }

    /**
     * 创建新的用户
     */
    @PostMapping()
    public Response<String> createUser(@RequestBody AccountVO accountVO) {
        return Response.buildSuccess(accountService.createUser(accountVO));
    }

    /**
     * 更新用户信息
     */
    @PutMapping()
    public Response<String> updateUser(@RequestBody AccountVO accountVO) {
        return Response.buildSuccess(accountService.updateUser(accountVO));
    }

    /**
     * 登录
     */
    @PostMapping("/login")
    public Response<String> login(@RequestBody AccountVO accountVO) {
        return Response.buildSuccess(accountService.login(accountVO.getUsername(), accountVO.getPassword()));
    }
}
