package com.huanyuenwei.filter;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.huanyuenwei.result.Res;
import springfox.documentation.spring.web.json.Json;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * session过滤器
 */
//@WebFilter(filterName = "sessionFilter",urlPatterns = {"/*"})
public class SessionFilter implements Filter {





    //免登录就可访问的路径(比如:注册,登录,注册页面上的一些获取数据等)
    String[] includeUrls = new String[]{
            "/css/font.css",
            "/css/newlogin.css",

            "/code",
            "/layui/layui.js",
            "/toLogin",
            "/toRegister",
            "/css/weadmin.css",
            "/favicon.ico",
            "/images/bg.png",
            "/layui/css/layui.css",
            "/images/aiwrap.png",
            "/login",
            "/toRegister",

            "/css/weadmin.css.map",
            "/register",
            "/checkUser",


                    };


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpSession session = request.getSession(false);
        //当前请求的url
        String uri = request.getRequestURI();

        System.out.println("filter url:"+uri);
        //判断url是否需要过滤
        boolean needFilter = isNeedFilter(uri);
        if (!needFilter) { //不需要过滤直接传给下一个过滤器
            filterChain.doFilter(servletRequest, servletResponse);
        } else { //需要过滤器
            // session中包含user对象,则是登录状态
            Object userdata = request.getSession().getAttribute("userdata");
            if(userdata!=null){
                filterChain.doFilter(request, response);
            }else{
//                rtnMap.put("code", 403);
//                rtnMap.put("errMsg", "您还未登录,请先登录！！！");
                response.setContentType("text/html; charset=utf-8");
                Res res=Res.build().code(206).data("错误");

                ObjectMapper mapper=new ObjectMapper();
                String s = mapper.writeValueAsString(res);
                response.getWriter().write(s);
                request.getRequestDispatcher("/toLogin").forward(request,response);


            }
        }
    }

    /**
     * @Author: wdd
     * @Description: 是否需要过滤
     * @Date: 2019-02-21 13:20:54
     * @param uri
     */
    public boolean isNeedFilter(String uri) {

        for (String includeUrl : includeUrls) {
            if(includeUrl.equals(uri)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void destroy() {

    }
}