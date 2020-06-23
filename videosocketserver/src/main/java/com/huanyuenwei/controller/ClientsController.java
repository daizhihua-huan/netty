package com.huanyuenwei.controller;

import com.huanyuenwei.result.Res;
import com.huanyuenwei.util.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 *
 *
 */
@RestController
public class ClientsController {
    @Autowired
    private GreetingController greetingController;

    /**
     *
     * @param PageNumber
     * @return
     * 分页方法
     */
    @RequestMapping("/getClientForPage")
    public Res getClient(String PageNumber) {

        Res clinet = greetingController.getClinet();

        Set data = (Set) clinet.getData();

        List<String> list = new ArrayList<String>(data);

        list.add("20");
        list.add("20");
        list.add("13");
        list.add("14");
        list.add("15");
        list.add("16");
        list.add("17");
        list.add("18");
        list.add("19");
        list.add("45");
        list.add("21");
        list.add("22");
        list.add("23");
        list.add("24");
        list.add("25");
        list.add("26");
        list.add("27");
        list.add("28");
        list.add("29");
        list.add("30");

        int i = Integer.parseInt(PageNumber);
        int sum=list.size();
        List list1 = PageUtil.startPage(list, i, 4);
        clinet.msg(String.valueOf(sum));
        clinet.setData(list1);
        return clinet;


    }
}
