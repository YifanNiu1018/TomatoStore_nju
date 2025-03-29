package com.example.tomatomall.service;

import com.example.tomatomall.vo.AccountVO;

public interface AccountService {
    String createUser(AccountVO accountVO);

    AccountVO getUser(String username);

    String login(String username, String password);

    String updateUser(AccountVO accountVO);
}
