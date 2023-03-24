package com.example.yin.mapper;

import com.example.yin.model.domain.Consumer;
import com.example.yin.model.domain.UserLogin;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserLoginMapper {
    UserLogin findByUsername(String username);

    UserLogin findUserById(String id);

    Consumer getMessage(String id);
}
