package com.example.tomatomall.service;

import com.example.tomatomall.vo.AccountVO;

public interface AccountService {
    Boolean createUser(AccountVO accountVO);

    AccountVO getUser(String username);

    String login(String username, String password);

    Boolean updateUser(AccountVO accountVO);
}
