package com.huanyuenwei.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.huanyuenwei.Entuty.Users;
import com.huanyuenwei.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @author CHR
 * @version 1.0
 * 登录服务层
 */
@Service
public class UserServer  implements UserDetailsService{
    @Autowired
    private UserDao userDao;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {

        Users userdata = userDao.selectOne(
                new QueryWrapper<Users>().eq("username", s));


        User user=new User(userdata.getUsername(),passwordEncoder.encode(userdata.getPassword()) ,
                true,true,true,true,
                AuthorityUtils.commaSeparatedStringToAuthorityList("admin"));
        return user;
    }
    /**
     * 登录方法
     *
     * @param username
     * @param password
     * @return
     */
    public List<Users> checkLogin(String username, String password) {

        System.out.println(username);
        List<Users> userdata = userDao.selectList(
                new QueryWrapper<Users>().eq("username", username)
                        .eq("password", password));
        return userdata;

    }

    /**
     * 注册方法
     *
     * @param users
     * @return
     */
    public int userRegister(Users users) {


        return userDao.insert(users);

    }

    /**
     * 查重
     * @param username
     * @return
     */
    public Users checkUser(String username) {

            Users users = userDao.selectOne(new QueryWrapper<Users>().eq("username", username));
            System.out.println(users);
            return users;




    }


}
