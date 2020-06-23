package com.huanyuenwei.controller;


import com.huanyuenwei.result.Res;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @author CHR
 * @version 1.0
 * 页面控制
 */
@Controller
public class ViewController {

    @Autowired
    private GreetingController greetingController;

    /**
     * @return
     */
    @RequestMapping(value = "/toLogin", method = RequestMethod.GET)

    public String toLogin() {

        return "newlogin";
    }

    /**
     * @return
     */
    @RequestMapping("/toSuccess")
    public String toSuccess() {

        return "newindex";
    }

    /**
     * @return
     */

    @RequestMapping("/toError")
    public String toError() {
        return "error";
    }

    /**
     *
     * @return
     */
    @RequestMapping("/toRegister")
    public String toRegister(){
        return "newregister";
    }


    @RequestMapping("/toIndex")
    public String toIndex(){
        return "pages/index.html";
    }
}
