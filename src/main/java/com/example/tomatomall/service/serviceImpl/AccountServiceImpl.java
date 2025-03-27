package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.exception.TomatoMailException;
import com.example.tomatomall.po.Account;
import com.example.tomatomall.repository.AccountRepository;
import com.example.tomatomall.service.AccountService;
import com.example.tomatomall.utils.SecurityUtil;
import com.example.tomatomall.utils.TokenUtil;
import com.example.tomatomall.vo.AccountVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.Basic;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    TokenUtil tokenUtil;

    @Autowired
    SecurityUtil securityUtil;

    @Override
    public Boolean createUser(AccountVO accountVO) {
        Account account = accountRepository.findByUsername(accountVO.getUsername());
        if (account != null) {
            throw  TomatoMailException.userNameAlreadyExist();
        }
        //对密码加密
        String rawPassword = accountVO.getPassword();
        String encodedPassword = passwordEncoder.encode(rawPassword);
        accountVO.setPassword(encodedPassword);

        accountRepository.save(accountVO.toPO());
        return true;
    }

    @Override
    public AccountVO getUser(String username) {
        Account account = accountRepository.findByUsername(username);
        if (account != null) {
            return account.toVO();
        } else {
            throw TomatoMailException.userNotExist();
        }
    }

    @Override
    public String login(String username, String password) {
        Account account = accountRepository.findByUsername(username);
        if (account != null && passwordEncoder.matches(password, account.getPassword())) {
            return tokenUtil.getToken(account);
        } else {
            throw TomatoMailException.loginError();
        }
    }

    @Override
    public Boolean updateUser(AccountVO accountVO) {
        Account account = accountRepository.findByUsername(accountVO.getUsername());
        if (account.getName() != null) {
            account.setName(accountVO.getName());
        }
        if (account.getPassword() != null) {
            String rawPassword = accountVO.getPassword();
            String encodedPassword = passwordEncoder.encode(rawPassword);
            account.setPassword(encodedPassword);
        }
        if (account.getAvatar() != null) {
            account.setAvatar(accountVO.getAvatar());
        }
        if (account.getTelephone() != null) {
            account.setTelephone(accountVO.getTelephone());
        }
        if (account.getLocation() != null) {
            account.setLocation(accountVO.getLocation());
        }
        if (account.getEmail() != null) {
            account.setEmail(accountVO.getEmail());
        }
        accountRepository.save(account);
        return null;
    }
}
