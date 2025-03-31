package com.example.tomatomall.service;

import com.example.tomatomall.vo.UserVO;

public interface UserService {
    String createUser(UserVO userVO);

    UserVO getUser(String username);

    String login(String username, String password);

    String updateUser(UserVO userVO);
}
