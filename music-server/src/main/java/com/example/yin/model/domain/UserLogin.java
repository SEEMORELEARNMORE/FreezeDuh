package com.example.yin.model.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
@TableName(value = "consumer")
@Data
public class UserLogin {
    private String id;
    private String username;
    private String password;
}
