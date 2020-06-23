package com.huanyuenwei.controller;


import com.huanyuenwei.Entuty.Users;
import com.huanyuenwei.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Random;


@Controller
public class CodeController {

    @Autowired
    private UserDao userDao;

    /**
     *
     * @param request
     * @param response
     */
    @RequestMapping("/code")
    public void check(HttpServletRequest request, HttpServletResponse response) {

        //定义BufferedImage（图像数据缓冲区）对象
        BufferedImage bi = new BufferedImage(68, 22, BufferedImage.TYPE_INT_RGB);
        //绘制图片
        Graphics g = bi.getGraphics();
        //背景色
        Color c = new Color(200, 150, 255);
        g.setColor(c);
        //图片坐标
        g.fillRect(0, 0, 68, 22);
        //验证码选取
        char[] ch = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
        Random r = new Random();
        int len = ch.length, index;
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 4; i++) {
            index = r.nextInt(len);
            g.setColor(new Color(r.nextInt(88), r.nextInt(188), r.nextInt(255)));
            Font ft = new Font(Font.SANS_SERIF, Font.BOLD, 16);
            g.setFont(ft);
            g.drawString(ch[index] + "", (i * 15) + 3, 18);
            sb.append(ch[index]);
        }
        //打印验证码，项目中用日志

        request.getSession().setAttribute("checkCode", sb.toString());
        //ImageIO写出图片
        try {
            ImageIO.write(bi, "JPG", response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/gets")
    public void getDate(){

        List<Users> list= userDao.selectList(null);
        for (Users users : list) {
            System.out.println(users);
        }


    }
}
