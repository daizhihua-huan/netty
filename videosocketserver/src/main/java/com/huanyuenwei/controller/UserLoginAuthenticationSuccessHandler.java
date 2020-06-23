package com.huanyuenwei.controller;

import com.google.gson.Gson;
import com.huanyuenwei.result.Res;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.security.core.userdetails.User;
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
 * 用户认证成功处理类
 */

@Component("UserLoginAuthenticationSuccessHandler")
@Slf4j
public class UserLoginAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {


        User user=(User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        request.getSession().setAttribute("userdata",user.getUsername());

        String code = request.getParameter("code");
        Object checkCode = request.getSession().getAttribute("checkCode");
        String oldcode = (String) checkCode;
        Gson gson=new Gson();
        if (code == null) {


            Res res = Res.build().code(203);
            String s = gson.toJson(res);
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write(s);


        }else if(!oldcode.equalsIgnoreCase(code)){

            Res res = Res.build().code(205);
            String s = gson.toJson(res);
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write(s);

        }else {

            JSONObject jo = new JSONObject();
            jo.put("code", 200);
            response.setContentType("application/json;charset=utf-8");
            PrintWriter out = response.getWriter();

            out.write(jo.toString());
            out.flush();
            out.close();
        }
    }
}