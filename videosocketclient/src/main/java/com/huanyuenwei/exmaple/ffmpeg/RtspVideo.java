package com.huanyuenwei.exmaple.ffmpeg;


import com.huanyuenwei.util.FileUtil;
import com.huanyuenwei.util.HttpUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

@Data
@Slf4j
public class RtspVideo implements Runnable {

    private  ProcessFfmpeg processFfmpeg;

    private boolean flag;



    /**
     * 1.首先判断系统
     * 2.执行ffmpeg的流保存系统
     * 4.执行ffmpeg的推流操作
     * 3.判断保存是否断开
     */
    @Override
    public void run() {
        String soucertsp = FileUtil.getPropertiesForName("soucertsp");
        String targetrtsp = FileUtil.getPropertiesForName("targetrtsp");
        String targetrrtspName = FileUtil.getPropertiesForName("targetrtspname");
        String stimeout = FileUtil.getPropertiesForName("stimeout");
        String cmd = "D:\\ffmpeg\\ffmpeg-20200612-38737b3-win64-static\\bin\\ffmpeg -stimeout "+stimeout+"  -i "+soucertsp+" -codec copy -acodec copy  -rtsp_transport tcp -f rtsp "+targetrtsp+"/"+targetrrtspName;
        log.info("cmd是：{}",cmd);
        //执行命令
        if(processFfmpeg==null){
            processFfmpeg = new ProcessFfmpeg();
        }
        int i = processFfmpeg.sendMessage(cmd);
        if(i==1&&flag){
            String id = HttpUtil.sendHttp("1");
            if(!StringUtils.isEmpty(id)){
                if(HttpUtil.stopRtsp(id)){
                    processFfmpeg.sendMessage(cmd);
                }
            }
        }
    }


    public void desory(){
        if(processFfmpeg!=null){
            processFfmpeg.destory();
        }
    }



    /*private boolean stopRtsp(String id){
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", id);
        String address = FileUtil.getPropertiesForName("ipaddress");

        try {
            HttpURLConnection urlConnection = (HttpURLConnection)
                    HttpUtil.sendGetRequest("http://" + address + ":10008/api/v1/stream/stop", params, null);
            if(urlConnection.getResponseCode()==200){
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }


    *//**
     * 查询当前的推送列表获取当前nano的推送终端
     * @return
     *//*
    private String sendHttp(){
        Map<String, String> params = new HashMap<String, String>();
        params.put("path", FileUtil.getPropertiesForName("targetrtspname"));
        String address = FileUtil.getPropertiesForName("ipaddress");
        String targetrtspName= FileUtil.getPropertiesForName("targetrtspname");
        try {
            HttpURLConnection conn = (HttpURLConnection)
                    HttpUtil.sendGetRequest("http://"+address+":10008/api/v1/pushers", params, null);
            int code = conn.getResponseCode();
            if (code==200){
                InputStream in = conn.getInputStream();
                String result = HttpUtil.read2String(in);
                return getByJsonArray(result, targetrtspName);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



    private String getByJsonArray(String result,String targetrtspName){
        JSONObject jsonObject = JSONObject.fromObject(result);
        System.out.println(jsonObject.get("rows").toString());
        JSONArray jsonArray = JSONArray.fromObject(jsonObject.get("rows"));
        for (Object json : jsonArray) {
            JSONObject fromObject = JSONObject.fromObject(json);
            if(fromObject.get("path").toString().equals("/"+targetrtspName)){
                return fromObject.get("id").toString();

            }
        }
        return null;
    }*/
    public static void main(String[] args) {
        Thread thread = new Thread(new RtspVideo());
        thread.start();
    }


}
