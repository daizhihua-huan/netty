package com.huanyuenwei.controller;


import com.huanyuenwei.result.Res;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author CHR
 * @version 1.0
 *
 */
@RestController
public class AllStartController {

    @Autowired
    private GreetingController greetingController;

    /**
     *
     * @param numbers
     * @return
     *
     * 开启所有方法
     */
    @RequestMapping("/allStartController")
    public Res allStartController(@RequestParam String numbers) {


        JSONArray jsonArr = JSONArray.fromObject(numbers);
        Map<String, String> map = new HashMap<>();
        Res start = Res.build();
        for (Object o : jsonArr) {
            start = greetingController.start((String) o, "1");
            System.out.println(start.getCode());
            if (start.getCode() == 200) {
                Res backVideoResult = greetingController.getBackVideoResult((String) o, "1");
                Map data = (Map) backVideoResult.getData();

                Object results = data.get("result");
                System.out.println(results);
                String result = String.valueOf(results);
                if (result.equalsIgnoreCase("true")) {
                    map.put((String) o, "开启成功");

                } else {

                    if (result.equalsIgnoreCase("false")) {
                        map.put((String) o, "开启失败");
                    }
                }


            } else if (start.getCode() == 201) {
                map.put((String) o, "请传入参数");

            } else if (start.getCode() == 202) {
                map.put((String) o, "此编号没有连接服务端，请检查是否开机");

            } else if (start.getCode() == 203) {
                map.put((String) o, "改设备已经启动请先关闭后再启动");

            }


        }


        start.setData(map);


        return start;


    }

    /**
     *
     * @param numbers
     * @return
     * 关闭所有方法
     */
    @RequestMapping("/allendController")
    public Res allendController(@RequestParam String numbers) {

        JSONArray jsonArr = JSONArray.fromObject(numbers);
        System.out.println(jsonArr);
        Map<String, String> map = new HashMap<>();
        Res start = Res.build();
        for (Object o : jsonArr) {
            start = greetingController.stop((String) o, "1");

            if (start.getCode() == 200) {
                Res backVideoResult = greetingController.getBackVideoResult((String) o, "1");
                Map data = (Map) backVideoResult.getData();

                Object results = data.get("result");
                System.out.println(results);
                String result = String.valueOf(results);
                if (result.equalsIgnoreCase("true")) {
                    map.put((String) o, "关闭成功");

                } else {

                    if (result.equalsIgnoreCase("false")) {
                        map.put((String) o, "关闭失败");
                    }
                }


            } else if (start.getCode() == 201) {
                map.put((String) o, "请传入参数");

            } else if (start.getCode() == 202) {
                map.put((String) o, "此编号没有连接服务端，请检查是否开机");

            } else if (start.getCode() == 203) {
                map.put((String) o, "当前设备已经处于关闭状态，请先打开然后关闭");

            } else if (start.getCode() == 204) {
                map.put((String) o, "传入的类型错误");

            }

        }

        start.setData(map);

        return start;
    }

}
