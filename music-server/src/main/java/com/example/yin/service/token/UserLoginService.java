package com.example.yin.service.token;

import com.example.yin.mapper.UserLoginMapper;
import com.example.yin.model.domain.Consumer;
import com.example.yin.model.domain.UserLogin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserLoginService {
    @Autowired
    private UserLoginMapper userLoginMapper;
    public UserLogin findByUsername(UserLogin userLogin){
        return userLoginMapper.findByUsername(userLogin.getUsername());
    }
    public  UserLogin findUserById(String id){
        return userLoginMapper.findUserById(id);
    }

    public Consumer getMessage(String id){ return userLoginMapper.getMessage(id);}
}
