package com.huanyuenwei.controller;
import com.huanyuenwei.Entuty.NanoEntity;
import com.huanyuenwei.common.Model;
import com.huanyuenwei.common.TypeData;
import com.huanyuenwei.result.Res;
import com.huanyuenwei.util.DateUtil;
import com.huanyuenwei.util.NanoConfigUtil;
import com.huanyuenwei.util.NettConfigUtil;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiSort;
import lombok.extern.log4j.Log4j2;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ConcurrentMap;


@Controller
@ResponseBody
@Log4j2
@ApiSort(value = 208)
@Api(value = "API注释",tags = {"测试swagger"})
public class DataController {

    @Autowired
    private SimpMessagingTemplate template;



    @RequestMapping(value = "/getVideo")
    public String getVidoe(){
        return "pages/index.html";
    }


    @GetMapping(value = "/get")
    public String getVideos(HttpServletRequest request, HttpServletResponse response)
    {
        try {
            FileInputStream fis = null;
            OutputStream os = null ;
            //C:\video\datasouce\data\20200414201352
            //D:\BaiduNetdiskDownload\spring websocket.mp4
            fis = new FileInputStream("C:\\video\\datasouce\\data\\20200414201352\\out.mp4");
            int size = fis.available(); // 得到文件大小
            byte data[] = new byte[size];
            fis.read(data); // 读数据
            fis.close();
            fis = null;
            response.setContentType("video/mp4"); // 设置返回的文件类型
            os = response.getOutputStream();
            os.write(data);
            os.flush();
            os.close();
            os = null;


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
