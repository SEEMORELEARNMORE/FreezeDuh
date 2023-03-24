package com.example.yin.service.token;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.yin.model.domain.UserLogin;
import org.springframework.stereotype.Service;

import java.util.Date;



@Service
public class TokenService {
    public String getToken(UserLogin userLogin){
         Date start =new Date();
         long currentTime = System.currentTimeMillis()+5*60*1000;//5分钟有效时间？
        Date end =new Date(currentTime);


        String token="";
//        token= JWT.create().withAudience(userLogin.getId()).withIssuedAt(start).withExpiresAt(end)
//                .sign(Algorithm.HMAC256(userLogin.getPassword()));
        token= JWT.create().withAudience(userLogin.getId()).withExpiresAt(end).withIssuedAt(start)
                .sign(Algorithm.HMAC256(userLogin.getPassword()));

        return token;
    }
}
