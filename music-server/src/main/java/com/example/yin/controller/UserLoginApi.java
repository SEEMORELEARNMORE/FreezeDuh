package com.example.yin.controller;

import com.example.yin.common.R;
import com.example.yin.model.domain.UserLogin;
import com.example.yin.service.token.TokenService;
import com.example.yin.service.token.UserLoginService;
import com.example.yin.service.token.UserLoginToken;
import com.example.yin.util.TokenUtil;
import io.swagger.annotations.ApiOperation;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;

import static com.example.yin.constant.Constants.SALT;

@RestController
public class UserLoginApi {
    @Autowired
    UserLoginService userLoginService;
    @Autowired
    TokenService tokenService;

    //登录
    @ApiOperation(value ="登录" ,notes="登录")
    @RequestMapping(value = "/UserLogin",method = RequestMethod.POST)
    public Object login(@RequestBody UserLogin userLogin, HttpServletResponse response){
        JSONObject jsonObject=new JSONObject();
        UserLogin userForBase = new UserLogin();
        userForBase.setId(userLoginService.findByUsername(userLogin).getId());
        userForBase.setUsername(userLoginService.findByUsername(userLogin).getUsername());
        userForBase.setPassword(userLoginService.findByUsername(userLogin).getPassword());
        String secretPassword = DigestUtils.md5DigestAsHex((SALT + userLogin.getPassword()).getBytes(StandardCharsets.UTF_8));
        if(!userForBase.getPassword().equals(secretPassword)){
            jsonObject.put("message","登录失败，密码错误");
            return jsonObject.toString();
        }else {
            String token =tokenService.getToken(userForBase);
            jsonObject.put("token",token);
            Cookie cookie=new Cookie("token",token);
            cookie.setPath("/");
            response.addCookie(cookie);
          return R.success("登录成功", userLoginService.getMessage(userForBase.getId()),token);
//            return jsonObject.toString();
        }
    }
    //这个请求需要验证token才能访问
    @UserLoginToken
    @ApiOperation(value ="获取信息",notes = "获取信息")
    @RequestMapping(value = "/getMessage",method = RequestMethod.POST)
    public R getMessage(){
        //取出token中带的用户id进行操作
//        System.out.println(TokenUtil.getTokenUserId());
//        return "您已通过验证";
        return R.success("获取信息成功", userLoginService.getMessage(TokenUtil.getTokenUserId()));
    }



}
