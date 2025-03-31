package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.exception.TomatoMailException;
import com.example.tomatomall.po.User;
import com.example.tomatomall.repository.UserRepository;
import com.example.tomatomall.service.UserService;
import com.example.tomatomall.utils.SecurityUtil;
import com.example.tomatomall.utils.TokenUtil;
import com.example.tomatomall.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    UserRepository userRepository;

    @Autowired
    TokenUtil tokenUtil;

    @Autowired
    SecurityUtil securityUtil;

    @Override
    public String createUser(UserVO userVO) {
        User user = userRepository.findByUsername(userVO.getUsername());
        if (user != null) {
            throw  TomatoMailException.userNameAlreadyExist();
        }
        //对密码加密
        String rawPassword = userVO.getPassword();
        String encodedPassword = passwordEncoder.encode(rawPassword);
        userVO.setPassword(encodedPassword);

        userRepository.save(userVO.toPO());
        return "注册成功";
    }

    @Override
    public UserVO getUser(String username) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            return user.toVO();
        } else {
            throw TomatoMailException.userNotExist();
        }
    }

    @Override
    public String login(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return tokenUtil.getToken(user);
        } else {
            throw TomatoMailException.loginError();
        }
    }

    @Override
    public String updateUser(UserVO userVO) {
        User user = userRepository.findByUsername(userVO.getUsername());
        if (user.getName() != null) {
            user.setName(userVO.getName());
        }
        if (user.getPassword() != null) {
            String rawPassword = userVO.getPassword();
            String encodedPassword = passwordEncoder.encode(rawPassword);
            user.setPassword(encodedPassword);
        }
        if (user.getAvatar() != null) {
            user.setAvatar(userVO.getAvatar());
        }
        if (user.getTelephone() != null) {
            user.setTelephone(userVO.getTelephone());
        }
        if (user.getLocation() != null) {
            user.setLocation(userVO.getLocation());
        }
        if (user.getEmail() != null) {
            user.setEmail(userVO.getEmail());
        }
        userRepository.save(user);
        return "更新成功";
    }
}
