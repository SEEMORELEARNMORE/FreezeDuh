package com.example.yin.util;

import com.auth0.jwt.JWT;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;


public class TokenUtil {
    public static  String getTokenUserId(){
        String token = getRequest().getHeader("token");//从http请求头中取出token
        String userId= JWT.decode(token).getAudience().get(0);
        return userId;
    }

    //获取request
    public static HttpServletRequest getRequest(){
        ServletRequestAttributes requestAttributes=(ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes();
        return requestAttributes==null?null:requestAttributes.getRequest();
    }
    //获取token从header中
    public static String getToken(){
        String token=getRequest().getHeader("token");
        return token;
    }
}
