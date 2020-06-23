package com.huanyuenwei.controller;

import com.huanyuenwei.Entuty.Users;
import com.huanyuenwei.result.Res;
import com.huanyuenwei.service.UserServer;
import org.apache.catalina.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * @author CHR
 * @version 1.0
 * 登录控制层
 */

@RestController
public class UserController {
    @Autowired
    private UserServer userServer;

    /**
     * @param request
     * @param username
     * @param password
     * @param code
     * @return
     *
     * 登录
     */
//    @RequestMapping(value = "/login", method = {RequestMethod.GET})
    public Res login(HttpServletRequest request, String username, String password, String code) {

        /**
         * 200 success
         * 201 输用户名
         * 202 密码输入
         * 203 验证码没输入
         * 204 用户名或密码错误
         * 205  验证码错误
         * 校验 字符是否为空正确
         * 逻辑放在service中
         *
         */
        if (username == null) {
            return Res.build()
                    .code(201)
                    .error("请输入用户名");
        }
        if (password == null) {
            return Res.build()
                    .code(202)
                    .error("请输入密码");
        }
        if (code == null) {
            return Res.build()
                    .code(203)
                    .error("请输入验证码");
        }

        Object checkCode = request.getSession().getAttribute("checkCode");
        String oldcode = (String) checkCode;
        if (oldcode.equalsIgnoreCase(code)) {
            List<Users> list = userServer.checkLogin(username, password);

            if (list == null || list.size() == 0) {
                return Res.build()
                        .code(204)
                        .error("用户名或密码错误");
            } else {
                Users users=list.get(0);

                request.getSession().setAttribute("userdata",users);

                return Res.build()
                        .code(200)
                        .data(list);
            }
        } else {
            return Res.build()
                    .code(205)
                    .error("验证码错误");
        }

    }

    /**
     *
     * @param request
     * @param username
     * @param password
     * @param code
     * @return
     * 注册
     */
    @RequestMapping(value = "/register", method = {RequestMethod.GET})
    public Res register(HttpServletRequest request, String username, String password, String code) {

        /**
         *200 success
         * 201 输用户名
         * 202 密码输入
         * 203 验证码没输入
         * 204 注册失败
         * 205  验证码错误
         *
         */


        if (username == null) {
            return Res.build()
                    .code(201)
                    .error("请输入用户名");
        }
        if (password == null) {
            return Res.build()
                    .code(202)
                    .error("请输入密码");
        }
        if (code == null) {
            return Res.build()
                    .code(203)
                    .error("请输入验证码");
        }

        Object checkCode = request.getSession().getAttribute("checkCode");
        String oldcode = (String) checkCode;
        if (oldcode.equalsIgnoreCase(code)) {
            Users users = new Users();
            users.setUsername(username);
            users.setPassword(password);
            users.setDate(new Date());

            int requests = userServer.userRegister(users);
            if (requests == 1) {
                return Res.build()
                        .code(200);

            } else {

                return Res.build()
                        .code(204)
                        .error("注册失败");
            }


        } else {
            return Res.build()
                    .code(205)
                    .error("验证码错误");
        }


    }

    /**
     *
     * @param username
     * @return
     * 用户名查重
     */
    @RequestMapping(value = "/checkUser", method = {RequestMethod.GET})
    public Res checkUser(String username) {

        /**
         * 206可以注册
         * 207用户名存在
         */
        Users users = userServer.checkUser(username);
        if (users == null) {
            return Res.build()
                    .code(206);

        }else {
            return Res.build()
                    .code(207)
                    .error("用户名存在");
        }

    }

    @RequestMapping(value = "/removeUserData", method = {RequestMethod.GET})
    public void removeUserData(HttpServletRequest request, HttpServletResponse response) throws IOException {

                request.getSession().invalidate();
        System.out.println("aaaa");
                response.sendRedirect(request.getContextPath()+"/toLogin");
    }



}
