package com.huanyuenwei.confg;

import com.huanyuenwei.controller.UserLoginAuthenticationFailureHandler;
import com.huanyuenwei.controller.UserLoginAuthenticationSuccessHandler;
import com.huanyuenwei.filter.CodeFilter;
import com.huanyuenwei.service.UserServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserServer userServer;

    @Autowired
    private UserLoginAuthenticationFailureHandler userLoginAuthenticationFailureHandler;//验证失败的处理类

    @Autowired
    private UserLoginAuthenticationSuccessHandler userLoginAuthenticationSuccessHandler;//验证成功的处理类

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilterBefore(new CodeFilter(),UsernamePasswordAuthenticationFilter.class);
        http
                .authorizeRequests()
                .antMatchers("/code").permitAll()
                .antMatchers("/toRegister").permitAll()
                .antMatchers("/register").permitAll()
                .antMatchers("/checkUser").permitAll()
                // 支持表单登录
                .anyRequest().authenticated()
                .and()
                .formLogin().loginPage("/toLogin")
                .loginProcessingUrl("/login")
                .usernameParameter("username")//请求验证参数
                .passwordParameter("password")
                .failureHandler(userLoginAuthenticationFailureHandler)//验证失败处理
                .successHandler(userLoginAuthenticationSuccessHandler)//验证成功处理
                //自定义成功登录页面
//                .defaultSuccessUrl("/toSuccess")
                .permitAll()
                //默认都会产生一个hiden标签 里面有安全相关的验证 防止请求伪造 这边我们暂时不需要 可禁用掉
                .and()
                .csrf().disable()
                .httpBasic();

               /* .and()
               .authorizeRequests()
               .antMatchers("/*").authenticated()
               .antMatchers("/CodeController").permitAll()
*/


    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
      /*  auth.inMemoryAuthentication()
                .passwordEncoder(passwordEncoder()).withUser("admin").password("123456").roles("ADMIN");*/
        auth.userDetailsService(userServer).passwordEncoder(passwordEncoder());


    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {


        web.ignoring().antMatchers("/css/**", "/js/**", "/fonts/**", "/images/**", "/layui/**",
                "/pages/**", "/css/weadmin.css.map");

    }
}
