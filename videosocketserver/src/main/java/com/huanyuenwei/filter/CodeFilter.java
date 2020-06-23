package com.huanyuenwei.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.huanyuenwei.result.Res;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * @author CHR
 * @version 1.0
 *
 *
 */
// OncePerRequestFilter : 保证过滤器只被调用一次
@WebFilter(filterName = "sessionFilter",urlPatterns = {"/*"})
public class CodeFilter extends OncePerRequestFilter {

    private AuthenticationFailureHandler authenctiationFailureHandler;



    // 过滤 逻辑
    @Override

    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 是一个登陆请求
        if (StringUtils.equals("/login", request.getRequestURI())
                && StringUtils.equalsIgnoreCase(request.getMethod(), "POST")) {
            try {
              ;
                String code = request.getParameter("code");
                Object checkCode = request.getSession().getAttribute("checkCode");
                String oldcode = (String) checkCode;
                Gson gson=new Gson();
                if (code == null) {


//                    Res res = Res.build().code(203);
//                    String s = gson.toJson(res);
//                    response.getWriter().write(s);

                    System.out.println("验证码为空");
                }else if(!oldcode.equalsIgnoreCase(code)){

//                    Res res = Res.build().code(205);
//                    String s = gson.toJson(res);
//                    response.getWriter().write(s);
                    System.out.println("验证码错误");

                }



            } catch (Exception e) {
                // 有异常就返回自定义失败处理器
               e.printStackTrace();
                return;
            }
        }
        // 不是一个登录请求，不做校验 直接通过
        filterChain.doFilter(request, response);
    }



    public AuthenticationFailureHandler getAuthenctiationFailureHandler() {
        return authenctiationFailureHandler;
    }

    public void setAuthenctiationFailureHandler(AuthenticationFailureHandler authenctiationFailureHandler) {
        this.authenctiationFailureHandler = authenctiationFailureHandler;
    }

}
