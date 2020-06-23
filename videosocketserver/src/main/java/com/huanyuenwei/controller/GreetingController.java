package com.huanyuenwei.controller;

import com.huanyuenwei.Entuty.NanoEntity;
import com.huanyuenwei.common.Model;
import com.huanyuenwei.common.TypeData;
import com.huanyuenwei.linster.LinsterByte;
import com.huanyuenwei.linster.ResultLinster;
import com.huanyuenwei.result.Res;
import com.huanyuenwei.util.DateUtil;
import com.huanyuenwei.util.NanoConfigUtil;
import com.huanyuenwei.util.NettConfigUtil;
import com.sun.org.apache.xpath.internal.operations.Mod;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.swagger.annotations.*;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@RestController
@Slf4j
@ApiSort(value = 209)
@Api(value = "视频接口查询",tags = {"视频管理"})
public class GreetingController implements LinsterByte, ResultLinster {



    @Autowired
    private RestTemplate restTemplate;


    @RequestMapping(value = "/getClinet",method = RequestMethod.GET)
    @ApiOperationSupport(order = 0)
    @ApiOperation(value = "查询连接的客户端",
                  httpMethod = "GET",
            notes = "查询当前连接的客户端"
    )
    @ApiResponses({
            @ApiResponse(code = 200,message = "返回连接的客户端",response = Res.class)
    })
    public Res getClinet(){
        ConcurrentMap<String, ChannelHandlerContext> all
                = NettConfigUtil.getAll();

        return Res.build()
                .code(200)
                .data(all.keySet());


    }


    /**
     * 开启视屏推流
     * @param number 每个设备的编号
     * @return
     */
    @RequestMapping(value = "/start",method = RequestMethod.GET)
    @ApiOperationSupport(order=1)
    @ApiOperation(value = "开启推流",
            httpMethod = "GET",
            notes = "开始视频推流"
    )
    @ApiResponses({
            @ApiResponse(code = 200,message = "开启成功",response = Res.class),
            @ApiResponse(code = 201, message = "请传入参数",response = Res.class),
            @ApiResponse(code = 202, message = "此编号没有连接服务端，请检查是否开机",response = Res.class),
            @ApiResponse(code = 203, message = "改设备已经启动请先关闭后再启动",response = Res.class)
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name="number",value="设备编号",required=true,paramType = "form"),
            @ApiImplicitParam(name="type",value="类型 1推送rtsp摄像头 2启动本地视频",required=true,paramType = "form")
    })
    public Res start(String number,String type){
        log.info("传过来的编号是"+number);
        if(StringUtils.isEmpty(number)){
            return Res.build()
                    .code(201)
                    .msg("请传入参数");
        }
        if(StringUtils.isEmpty(type)){
            return Res.build()
                    .code(201)
                    .msg("请传入参数");
        }

        ChannelHandlerContext key = NettConfigUtil.getKey(number);
        if(key==null){
            return Res.build()
                    .code(202)
                    .msg("此编号没有连接服务端，请检查是否开机");
        }

        Model model = new Model();
        NanoEntity numberByNanoEntity =null;
        switch (type){
            case "1":
                numberByNanoEntity = NanoConfigUtil.getNumberByNanoEntity(number);
                if(numberByNanoEntity==null){
                    NanoConfigUtil.add(number);
                }else{
                    /*if(numberByNanoEntity.isFlag()){
                        return Res.build()
                                .code(203)
                                .error("改设备已经启动请先关闭后再启动");
                    }*/
                }
                model.setType(TypeData.LIVENUMBER);
                model.setBody("true");
                key.writeAndFlush(model);
                return Res.build()
                        .code(200)
                        .msg("开启成功");
            case "2":
                numberByNanoEntity = NanoConfigUtil.getNumberByNanoEntity(number);
                if(numberByNanoEntity==null){
                    NanoConfigUtil.add(number);
                }else{
                    if(numberByNanoEntity.isLocaLflag()){
                        return Res.build()
                                .code(203)
                                .error("改设备已经启动请先关闭后再启动");
                    }
                }
                model.setBody("true");
                model.setType(TypeData.LIVELOCAL);
                key.writeAndFlush(model);
                return Res.build()
                        .code(200)
                        .msg("开启成功");
        }
        return Res.build()
                .code(204)
                .msg("传入的类型错误");

    }

    /**
     *  关闭视频推流
     * @param number 每个设备的编号
     * @return
     */
    @RequestMapping(value = "/stop",method = RequestMethod.GET)
    @ApiOperationSupport(order=2)
    @ApiOperation(

            value = "关闭推流",
            notes = "关闭视频流",
            httpMethod = "GET")
    @ApiResponses({
            @ApiResponse(code = 200, message = "停止成功"),
            @ApiResponse(code = 201, message = "请传入参数"),
            @ApiResponse(code = 202, message = "此编号没有连接服务端，请检查是否开机"),
            @ApiResponse(code = 203, message = "当前设备已经处于关闭状态，请先打开然后关闭"),
            @ApiResponse(code = 204, message = "传入的类型错误")
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name="number",value="设备编号",required=true,paramType = "form"),
            @ApiImplicitParam(name="type",value="类型 1关闭推送rtsp流 2关闭推送本地视频",required=true,paramType = "form")
    })
    public Res stop(String number,String type){
        log.info("停止的编号是"+number);
        if(StringUtils.isEmpty(number)){
            return Res.build()
                    .code(201)
                    .msg("请传入参数");
        }
        ChannelHandlerContext key = NettConfigUtil.getKey(number);
        if(key==null){
            return Res.build()
                    .code(202)
                    .msg("此编号没有连接服务端，请检查是否开机");
        }
        NanoEntity numberByNanoEntity = NanoConfigUtil.getNumberByNanoEntity(number);
        if(numberByNanoEntity==null){
            NanoConfigUtil.add(number);
            return Res.build()
                    .code(203)
                    .msg("当前设备已经处于关闭状态，请先打开然后关闭");
        }else{
            /*if(!numberByNanoEntity.isFlag()){
                return Res.build()
                        .code(203)
                        .error("当前设备已经，请先打开然后关闭");
            }*/
        }
        Model model = new Model();
        switch (type){
            case "1":
                model.setType(TypeData.LIVENUMBER);
                model.setBody("false");
                key.writeAndFlush(model);
                return Res.build()
                        .code(200)
                        .msg("停止成功");
            case "2":
                model.setType(TypeData.LIVELOCAL);
                model.setBody("false");
                key.writeAndFlush(model);
                return Res.build()
                        .code(200)
                        .msg("停止成功");
        }
        return Res.build()
                .code(204)
                .msg("传入的类型错误");

    }



    @GetMapping("/send")
    @ApiOperationSupport(order=3)
    @ApiOperation(
            value = "筛选摄像头视频",
            notes = "根据日期筛选视频",
            httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name="startTime",value="开始时间",required=true,paramType = "form"),
            @ApiImplicitParam(name="number",value="设备编号",required=true,paramType = "form"),

            @ApiImplicitParam(name="endTime",value="结束时间",required=true,paramType = "form")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "sucees",response = Res.class),
            @ApiResponse(code = 201, message = "请输入设备编号",response = Res.class),
            @ApiResponse(code = 202, message = "日期传入的格式错误"),
            @ApiResponse(code = 203, message = "服务端断开连接"),
            @ApiResponse(code = 204,message = "开始时间大于结束时间")
    })
    public Res send(@RequestParam(value = "startTime") String startTime,
                    @RequestParam(value = "endTime") String endTime,
                    @RequestParam(value = "number")String number){
        ConcurrentMap<String, ChannelHandlerContext> maps = NettConfigUtil.getAll();
        log.info("获取的开始时间是------"+startTime);
        log.info("获取的结束时间为-------"+endTime);
        if(StringUtils.isEmpty(number)){
            return Res.build()
                    .code(201)
                    .error("请输入设备编号");
        }
        NanoEntity numberByNanoEntity = NanoConfigUtil.getNumberByNanoEntity(number);
        if(numberByNanoEntity==null){
            NanoConfigUtil.add(number);
        }
        if(DateUtil.getByStringForDate(startTime)==null || DateUtil.getByStringForDate(endTime)==null){
            return Res.build()
                    .code(202)
                    .error("日期传入的格式错误");
        }

        //如果结束时间小于开始时间那么报错
        if(DateUtil.getByStringForDate(startTime).getTime()>=
                DateUtil.getByStringForDate(endTime).getTime()){
            return Res.build()
                    .code(204)
                    .error("开始时间大于结束时间");
        }


        System.out.println("da小"+maps.size());
        if(maps.size()<=0||maps.get(number)==null){
            return Res.build()
                    .error("服务端断开连接")
                    .code(203);
        }

        ChannelHandlerContext ctx = maps.get(number);
        log.info("ctx的值是"+ctx);
        Model model = new Model();
        model.setType(TypeData.FILENUMBER);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("start",startTime);
        jsonObject.put("end",endTime);
        log.info("============jons是"+jsonObject.toString());
        model.setBody(jsonObject.toString());
        ChannelFuture channelFuture = ctx.writeAndFlush(model);
        System.out.println(channelFuture.isSuccess());
        return Res.build().code(200).msg("success");
    }



    @GetMapping(value = "/getVideos")
    @ApiOperationSupport(order=4)
    @ApiOperation(value = "获取摄像头视频流",
            notes = "获取视频完整字节流，需要先进行摄像头视频筛选",
            httpMethod = "GET"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="number",value="设备编号",required=true,paramType = "form")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "视频流"),
    })
    public void getVideos(HttpServletRequest request,
                          HttpServletResponse response,
                          String number) {
        if(StringUtils.isEmpty(number)){
            return;
        }
        NanoEntity numberByNanoEntity = NanoConfigUtil.getNumberByNanoEntity(number);
        if(numberByNanoEntity==null){
            return;
        }

        if(numberByNanoEntity.getBytes()==null){
            return;
        }
        numberByNanoEntity.setVideoFlag(false);

        OutputStream os = null ;
        try {
            response.setContentType("video/mp4"); // 设置返回的文件类型
            os = response.getOutputStream();
            log.info("byte的长度是"+numberByNanoEntity.getBytes().length);
            log.info("byte的"+numberByNanoEntity.getBytes().toString());
            os.write(numberByNanoEntity.getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                os.flush();
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }







    @RequestMapping(value = "/video",method = RequestMethod.GET)
    @ApiOperationSupport(order=5)
    @ApiOperation(value = "视频回看",
            notes = "查询设备的回看视频",
            httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name="number",value="设备编号",required=true,paramType = "form")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "sucees")
    })
    public void download(String number,HttpServletRequest request, HttpServletResponse response) throws IOException {
        if(StringUtils.isEmpty(number)){
            return;
        }
        NanoEntity numberByNanoEntity = NanoConfigUtil.getNumberByNanoEntity(number);
        if(numberByNanoEntity==null){
            NanoConfigUtil.add(number);
            return;
        }
        if(numberByNanoEntity.getBytes()==null){
            return;
        }

        //清空缓存
        response.reset();
        //获取响应的输出流
        OutputStream outputStream = response.getOutputStream();
        System.out.println("获取响应的字节"+numberByNanoEntity.getBytes());
        //创建随机读取文件对象
        long fileLength = numberByNanoEntity.getLength();
        //获取从那个字节开始读取文件
        String rangeString = request.getHeader("Range");
      //如果rangeString不为空，证明是播放视频发来的请求
        if(StringUtils.isEmpty(rangeString)){
            return;
        }
        //;
        int range = Integer.valueOf(rangeString.substring(rangeString.indexOf("=") + 1, rangeString.indexOf("-")));
        log.info("请求视频播放流，从字节："+range+" 开始");
        log.info("文件长度是"+numberByNanoEntity.getBytes().length);
        log.info("文件filelength是"+fileLength);
        //设置内容类型
        response.setHeader("Content-Type", "video/mp4");
        //设置此次相应返回的数据长度
        response.setHeader("Content-Length", String.valueOf(fileLength - range));
        //设置此次相应返回的数据范围
        response.setHeader("Content-Range", "bytes "+range+"-"+(fileLength-1)+"/"+fileLength);
        //返回码需要为206，而不是200
        response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
        log.info("转换获得字节长度"+range);
        //设定文件读取开始位置（以字节为单位）
        byte[] cache = new byte[1024 * 300];
        log.info("要写的长度"+cache.length);
        outputStream.write(numberByNanoEntity.getBytes(),  range, numberByNanoEntity.getBytes().length-range-1);
    }

    @RequestMapping(value = "/clearByte",method = RequestMethod.GET)
    @ApiOperationSupport(order=6)
    @ApiOperation(value = "清除视频缓存",
            notes = "清除缓存",
            httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name="number",
                    value="设备编号",
                    required=true,
                    paramType = "form")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "sucees"),
            @ApiResponse(code = 201, message = "请输入设备编号"),
            @ApiResponse(code = 202, message = "请先进行视频筛选然后在清除缓存"),
            @ApiResponse(code = 203, message = "当前的缓存已经被清空请勿多次清空")
    })
    public Res getFile(String number){
        if(StringUtils.isEmpty(number)){
            return Res.build()
                    .code(201)
                    .msg("请输入设备编号");
        }
        NanoEntity numberByNanoEntity = NanoConfigUtil.getNumberByNanoEntity(number);
        if(numberByNanoEntity==null){
            NanoConfigUtil.add(number);
            return Res.build()
                    .code(202)
                    .msg("请先进行视频筛选然后在清除缓存");
        }else{
            if(numberByNanoEntity.getBytes()==null){
                return Res.build()
                        .code(203)
                        .msg("当前的缓存已经被清空请勿多次清空");
            }
            numberByNanoEntity.setBytes(null);
        }
        log.debug("清除完成");
        return Res.build()
                .code(200)
                .msg("清除成功");
    }

    @RequestMapping(value = "/getBackVideoResult")
    @ApiOperationSupport(order=7)
    @ApiOperation(value = "查询此设备返回的信息",
            notes = "查询当前设备客户端返回的结果",
            httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name="number",value="设备编号",required=true,paramType = "form"),
            @ApiImplicitParam(name = "type",value = "类型 1、rtsp视频流 2、视频回看",required = true,paramType = "form")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "sucees"),
            @ApiResponse(code = 201, message = "请输入设备编号"),
            @ApiResponse(code = 202, message = "请输入查询的类型"),
            @ApiResponse(code = 203, message = "请输入查询的类型")
    })
    public Res getBackVideoResult(String number,String type){
        if(StringUtils.isEmpty(number)){
            return Res.build()
                    .code(201)
                    .error("请输入编号");
        }
        NanoEntity nanoEntity = NanoConfigUtil.getNumberByNanoEntity(number);
        if(nanoEntity==null){
            return Res.build()
                    .code(202)
                    .error("请输入有效的编号");
        }
        if(StringUtils.isEmpty(type)){
            return Res.build()
                    .code(203)
                    .error("请输入查询的类型");
        }
        Map<String,Object> map = new HashMap<>();
        switch (type){
            //查询直播的结果
            case "1":
                map.put("number",nanoEntity.getNumber());
                map.put("data",nanoEntity.getData());
                map.put("result",nanoEntity.isFlag());
                break;
            case "2":
                map.put("number",nanoEntity.getNumber());
                map.put("data",nanoEntity.getVideoData());
                map.put("result",nanoEntity.isVideoFlag());
                break;
            case "3":
                map.put("number",nanoEntity.getNumber());
                map.put("result",nanoEntity.isVideoFlag());
                map.put("data",nanoEntity.getVideoData());
                break;
            case "4":
                map.put("number",nanoEntity.getNumber());
                map.put("data",nanoEntity.getLocalSaveData());
                map.put("result",nanoEntity.isLocalSaveFlag());
                break;

        }
        return Res.build()
                .code(200)
                .data(map);
    }




    @RequestMapping(value = "/getUrlVideoList", method = RequestMethod.GET)
    @ApiOperationSupport(order=8)
    @ApiOperation(value = "获取推流列表",
            notes = "查询当前所以推流的列表",
            httpMethod = "GET")

    @ApiResponses({
            @ApiResponse(code = 200, message = "推流的列表"),
    })
    public Res getUrlVideoList(){


        Object forObject = restTemplate.getForObject("http://39.104.177.194:10008/api/v1/pushers", Object.class);
        log.info(forObject.toString());
        return Res.build()
                .code(200)
                .data(forObject);
    }



   /* @RequestMapping(value = "/startSaveVideo", method = RequestMethod.GET)
    @ApiOperationSupport(order=9)
    @ApiOperation(value = "开启视频存储",
            notes = "开启摄像头的视频流和本地视频流的保存",
            httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name="number",value="设备编号",required=true,paramType = "form"),
            @ApiImplicitParam(name = "type",value = "类型 1、保存rtsp视频流  2、保存本地视频",required = true,paramType = "form")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "推流的列表"),
            @ApiResponse(code = 201, message = "传入的编号不能为空"),
            @ApiResponse(code = 202, message = "传入的类型不能为空"),
            @ApiResponse(code = 203, message = "当前客户端端口连接请检查是否开机"),
    })
    public Res startLocalVideo(String number,
                              String type){

        if (StringUtils.isEmpty(number)) {
            return Res.build()
                    .code(201)
                    .error("传入的编号不能为空");
        }
        NanoEntity numberByNanoEntity = NanoConfigUtil.getNumberByNanoEntity(number);
        if(numberByNanoEntity==null){
            NanoConfigUtil.add(number);
        }
        if(StringUtils.isEmpty(type)){
            return Res.build()
                    .code(202)
                    .error("传入的类型不能为空");
        }
        if(NettConfigUtil.getKey(number)==null){
            return Res.build()
                    .code(203)
                    .error("当前客户端端口连接请检查是否开机");
        }
        ChannelHandlerContext ctx = NettConfigUtil.getKey(number);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("flag",true);
        jsonObject.put("type",Integer.parseInt(type));
        Model model = new Model();
        model.setType(7);
        model.setBody(jsonObject.toString());
        ctx.writeAndFlush(model);

        return Res.build()
                .code(200)
                .msg("success");
    }*/


   /* @RequestMapping(value = "/stopLocalVideo", method = RequestMethod.GET)
    @ApiOperationSupport(order=10)
    @ApiOperation(value = "关闭视频存储",
            notes = "关闭摄像头的视频流和本地视频流的保存",
            httpMethod = "GET")
    @ApiResponses({
            @ApiResponse(code = 200, message = "推流的列表"),
            @ApiResponse(code = 201, message = "传入的编号不能为空"),
            @ApiResponse(code = 202, message = "传入的类型不能为空"),
            @ApiResponse(code = 203, message = "当前客户端端口连接请检查是否开机"),
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name="number",value="设备编号",required=true,paramType = "form")
    })
    public Res stopLocalVideo(String number){
        if (StringUtils.isEmpty(number)) {
            return Res.build()
                    .code(201)
                    .error("传入的编号不能为空");
        }
        if(NettConfigUtil.getKey(number)==null){
            NanoEntity numberByNanoEntity = NanoConfigUtil.getNumberByNanoEntity(number);
            if(numberByNanoEntity==null){
                NanoConfigUtil.add(number);
            }
            return Res.build()
                    .code(203)
                    .error("当前客户端端口连接请检查是否开机");
        }
        ChannelHandlerContext ctx = NettConfigUtil.getKey(number);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("flag",false);
        Model model = new Model();
        model.setType(7);
        model.setBody(jsonObject.toString());
        ctx.writeAndFlush(model);

        return Res.build()
                .code(200)
                .msg("success");
    }*/

    @RequestMapping(value = "/backLocalVideo",method = RequestMethod.POST)
    @ApiOperationSupport(order=11)
    @ApiOperation(value = "筛选本地的视频",
            notes = "开启本地视频存储后调用此接口筛选本地视频",
            httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name="number",value="设备编号",required=true,paramType = "form"),
            @ApiImplicitParam(name = "time",value = "本地视频创建时间 HHmmss",required = true,paramType = "form"),
            @ApiImplicitParam(name = "startTime",value = "开始时间 HHmmss",required = true,paramType = "form"),
            @ApiImplicitParam(name = "endTime",value = "结束时间 HHmmss",required = true,paramType = "form")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "success"),
            @ApiResponse(code = 201, message = "传入的编号不能为空"),
            @ApiResponse(code = 202, message = "当前客户端端口连接请检查是否开机"),
            @ApiResponse(code = 203, message = "传入的时间参数错误"),
            @ApiResponse(code = 204, message = "结束时间小于文件创建时间"),
            @ApiResponse(code = 205, message = "结束时间小于开始时间")
    })
    public Res backLocalVideo(String number, String time, String startTime,
                              String endTime) throws InterruptedException {
        if (StringUtils.isEmpty(number)) {
            return Res.build()
                    .code(201)
                    .msg("传入的编号不能为空");
        }
        log.info("穿过来的编号"+number);
        if(NettConfigUtil.getKey(number)==null){

            return Res.build()
                    .code(202)
                    .msg("当前客户端端口连接请检查是否开机");
        }
        NanoEntity numberByNanoEntity = NanoConfigUtil.getNumberByNanoEntity(number);
        if(numberByNanoEntity==null){
            NanoConfigUtil.add(number);
        }

        if(DateUtil.getByStringForTimeDate(time)==null||
                DateUtil.getByStringForTimeDate(startTime)==null||
                DateUtil.getByStringForTimeDate(endTime)==null){
            return Res.build()
                    .code(203)
                    .msg("传入的时间参数错误");
        }

        //开始时间大于结束时间
        if(Integer.parseInt(time)>=Integer.parseInt(endTime)){
            return Res.build()
                    .code(204)
                    .msg("结束时间小于文件创建时间");
        }
        numberByNanoEntity = NanoConfigUtil.getNumberByNanoEntity(number);
        numberByNanoEntity.setVideoFlag(false);
        numberByNanoEntity.setVideoData(null);
        if(Integer.parseInt(startTime)>=Integer.parseInt(endTime)){
            return Res.build()
                    .code(205)
                    .msg("结束时间小于开始时间");
        }
        ChannelHandlerContext ctx = NettConfigUtil.getKey(number);
        Model model = new Model();
        model.setType(8);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("time",time);
        jsonObject.put("startTime",startTime);
        jsonObject.put("endTime",endTime);
        log.info("序列话"+jsonObject.toString());
        model.setBody(jsonObject.toString());
        numberByNanoEntity.setBytes(null);
        log.info(numberByNanoEntity.toString());
        ctx.writeAndFlush(model);
       /* while (true){
            Thread.sleep(60);
            if(NanoConfigUtil.getNumberByNanoEntity(number).isVideoFlag()){
                break;
            }
            if(NanoConfigUtil.getNumberByNanoEntity(number).getVideoData()!=null){
                log.info("数据维"+NanoConfigUtil.getNumberByNanoEntity(number));
                return Res.build()
                        .code(204)
                        .msg(NanoConfigUtil.getNumberByNanoEntity(number).getVideoData());
            }

        }*/

        //return Res.build()
        //                        .code(204)
        //                        .msg(NanoConfigUtil.getNumberByNanoEntity(number).getVideoData());
        return Res.build()
                .code(200)
                .msg("success");

    }






    @Override
    public void sendByte(byte[] bytes,long length) {

    }



    @Override
    public void sendFile(RandomAccessFile targetFile,long length) {

    }

    @Override
    public void sendResult(String result) {

    }
}
