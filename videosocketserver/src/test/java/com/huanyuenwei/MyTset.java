package com.huanyuenwei;


import com.huanyuenwei.dao.UserDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.sql.SQLException;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MyTset.class)
@Component
public class MyTset extends Application{
//    @Resource
//    private UserDao userDao;
//
//    @Test
//    public void test() throws SQLException {
//
//        List a=userDao.selectList(null);
//        System.out.println(a);
//    }
}
