package com.huanyuenwei.controller;

import com.google.gson.Gson;
import com.huanyuenwei.result.Res;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author CHR
 * @version 1.0
 * Created by linziyu on 2019/2/9.
 *
 * 用户认证失败处理类
 */

@Component("UserLoginAuthenticationFailureHandler")
@Slf4j
public class UserLoginAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

   ;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        // log.info("{}","认证失败");


        // log.info("{}",exception.getMessage());

        // String username = (String) request.getAttribute("username");


//
//        if(exception.getMessage().equals("Bad credentials")){
//            jsonData = new JsonData(403,"密码错误");
            // String user_name =userService.findByUserNameAttemps(username);
            // if (user_name == null){
            //     String time = DateUtil.getTimeToString();
            //     UserLoginAttempts userLoginAttempts = new UserLoginAttempts(username,1,time);
            //     userService.saveAttempts(userLoginAttempts);
            // }


            // if(userService.getAttempts(username) == 1){
            //     String time = DateUtil.getTimeToString();
            //     userService.setAttempts(username,time);
            //     jsonData = new JsonData(403,"密码错误,你还有2次机会进行登录操作");
            // }
            // else if(userService.getAttempts(username) == 3){
            //     User user = userService.findByUserName(username);
            //     userService.LockUser(user.getId());
            //     jsonData = new JsonData(403,"最后一次尝试登陆失败，你已经被冻结了");
            // }
            // else if (userService.getAttempts(username) ==2 ){
            //     String time = DateUtil.getTimeToString();
            //     userService.setAttempts(username,time);
            //     jsonData = new JsonData(403,"密码错误,你还有1次机会进行登录操作");
            // }

//
//        }


        // if (exception.getMessage().equals("User account is locked")){
        //     jsonData = new JsonData(100,"LOCK");
        // }

//        String json = new Gson().toJson(jsonData);//包装成Json 发送的前台
        response.setContentType("application/json;charset=utf-8");
        Gson gson=new Gson();

        Res res = Res.build().code(204);
        String s = gson.toJson(res);
        response.getWriter().write(s);



    }
}