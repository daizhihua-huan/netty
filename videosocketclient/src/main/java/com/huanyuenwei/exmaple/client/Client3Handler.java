package com.huanyuenwei.exmaple.client;

import com.huanyuenwei.Entuty.FileUploadFile;
import com.huanyuenwei.exmaple.common.Middleware;
import com.huanyuenwei.exmaple.common.Model;
import com.huanyuenwei.exmaple.ffmpeg.*;
import com.huanyuenwei.server.NeetyCline;
import com.huanyuenwei.util.DateUtil;
import com.huanyuenwei.util.FileUtil;
import com.huanyuenwei.util.HttpUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import java.io.File;


@Slf4j
@ChannelHandler.Sharable
public class Client3Handler  extends Middleware  {
    private Client client;
    /**
     * rtsp的视频流
     */
    private Thread ffmpegRts;
    //保存视频流的线程
    private Thread rtspVoidThread;

    //推送本地视频流
    private Thread rtspLocalvideoThread;

    //直播开启的命令
    private Boolean flag;

    //本地视频开启
    private boolean localFlag;

    /**
     * 视频保存的开关
     */
//    private boolean saveFlag;


    private RtspSaveVideo rtspSaveVideo;

    private RetspLocalVideo retspLocalVideo;

    //视频摄像头的流保存
    private RtspVideo rtspVideo;

    private RetspSaveLocalVideo retspSaveLocalVideo;

    private Thread localVideoThread;

    /*public Client3Handler(Client client,Thread rtspVoidThread,RtspSaveVideo rtspSaveVideo
            ,RetspSaveLocalVideo retspSaveLocalVideo,Thread localVideoThread) {
        super("client");
        this.client = client;
        this.flag = false;
        this.localFlag = false;
        this.rtspVoidThread = rtspVoidThread;
        this.rtspSaveVideo = rtspSaveVideo;
        this.retspSaveLocalVideo = retspSaveLocalVideo;
        this.localVideoThread = localVideoThread;
    }*/

    public Client3Handler(Client client,Thread rtspVoidThread,RtspSaveVideo rtspSaveVideo) {
        super("client");
        this.client = client;
        this.flag = false;
        this.localFlag = false;
        this.rtspVoidThread = rtspVoidThread;
        this.rtspSaveVideo = rtspSaveVideo;
    }



    @Override
    protected void handlerData(ChannelHandlerContext ctx, Object msg) {
        // TODO Auto-generated method stub
        Model model = (Model) msg;
        String body = model.getBody();
    }

    protected void fileHandlerData(ChannelHandlerContext ctx, Object msg) throws Exception {
        Model model = (Model) msg;
        String body = model.getBody();
        JSONObject jsonObject = JSONObject.fromObject(body);
        log.info("收到文件数据" + model.toString());
        log.info("获取的开始时间为"+jsonObject.get("start").toString().trim());
        log.info("获取的结束时间是"+jsonObject.get("end").toString().trim());
        String start = jsonObject.getString("start").trim();

        //截取日期的开始时间
        String dicerStatr = start.substring(0,start.indexOf(" "));
        log.info("日期的开始时间为"+dicerStatr);
        String end = jsonObject.getString("end").trim();
        //截取日期的结束时间
        String dicerEnd = end.substring(0,end.indexOf(" "));
        log.info("日期的结束时间为"+dicerEnd);
        File dicerFile = FileUtil.isFileExiets(dicerStatr);
        FileUtil.getDicerPath(dicerStatr);
       /* long time = DateUtil.getDateByString(end).getTime()-DateUtil.getDateByString(start).getTime();
        log.info("-----------------开始时间和结束时间的插值"+time);
        long proerTime = Long.parseLong(DateUtil.
                getSecondByMinute(FileUtil.getPropertiesForName("time")));
        log.info("-----------------设置的时间范围"+proerTime);*/
        JSONObject resultJson = new JSONObject();
        if(DateUtil.getResultByTime(end,start)){
            model.setType(5);
            resultJson.put("data","时间范围超过"+FileUtil.getPropertiesForName("time")+"分钟");
            resultJson.put("result",false);
            resultJson.put("number",FileUtil.getPropertiesForName("number"));
            model.setBody(resultJson.toString());
            ctx.writeAndFlush(model);
            log.info("-------------时间范围超了");
            return;
        }
        /**
         * 如果文件目录不存在直接返回服务器告诉响应结果
         */
        if(!dicerFile.exists()){
           sendResult(ctx,model,jsonObject);
            return;
        }
        String path = FileUtil.getPath(dicerFile.getPath(),dicerStatr, start.substring(start.indexOf(" ")+1), end.substring(end.indexOf(" ")+1),
                DateUtil.getDateByString(end).getTime() - DateUtil.getDateByString(start).getTime());
        if(StringUtils.isEmpty(path)){
            sendResult(ctx,model,jsonObject);
            return;
        }
        sendFileHandle(new File(path),"1");
    }


    //本地视频回看
    /*@Override
    protected void selectLocalVideo(ChannelHandlerContext ctx, Object msg) {
        Model model = (Model) msg;
        JSONObject jsonObject = JSONObject.fromObject(model.getBody());
        *//*String time = jsonObject.getString("time");
        log.info("本地视频的开始时间"+time);*//*
        String startTime = jsonObject.getString("startTime");
        log.info("日期的开始时间为"+startTime);
        String[] statrDicers = startTime.split(" ");
        String endTime = jsonObject.getString("endTime");
        String[] endDicers= endTime.split(" ");
        if(endDicers==null){
            sendMessage(ctx,"参数格式错误",false,5);
            return;
        }
        log.info("结束时间的分割"+Arrays.toString(endDicers));
        if(statrDicers==null){
            sendMessage(ctx,"参数格式错误",false,5);
            return;
        }
        log.info("本地视频筛选的结束时间"+Arrays.toString(statrDicers));

       *//* if(Integer.parseInt(endTime)<=Integer.parseInt(time)){
            sendMessage(ctx,"结束的时间不能小于文件的开始时间",false,5);
            return;
        }
*//*
        if(Integer.parseInt(endDicers[1])<=Integer.parseInt(statrDicers[1])){
            sendMessage(ctx,"结束时间必须大于开始时间",false,5);
            return;
        }
        if(DateUtil.getResultByTime(endTime,startTime)){
            sendMessage(ctx,"时间范围超过"+FileUtil.getPropertiesForName("time")+"分钟",false,5);
            return;
        }
        String name = "localvideo";
        String path = FileUtil.isFileExiets(endDicers[0].trim()).getPath()+"/"+name;
        String pathResult = FileUtil.getPath(path, endDicers[0].trim(), statrDicers[1].trim(), endDicers[1].trim(), DateUtil.getDateByString(endTime).getTime() - DateUtil.getDateByString(startTime).getTime());
//        File localVideoFile = FileUtil.getLocalVideoPath(FileUtil.isFileExiets(endDicers[1]).getPath(),statrDicers[1], endDicers[1],endDicers[0]);
        if(StringUtils.isEmpty(pathResult)){
            sendMessage(ctx,"文件查找失败",false,5);
            return;
        }
        sendFileHandle(new File(pathResult),"2",ctx);

        *//*if(localVideoFile==null){
            sendMessage(ctx,"传入的视频名称错误",false,5);
        }else{
            sendFileHandle(localVideoFile,"2");

        }*//*

    }*/


    //本地视频回看
    @Override
    protected void selectLocalVideo(final ChannelHandlerContext ctx, Object msg) {
        Model model = (Model) msg;
        JSONObject jsonObject = JSONObject.fromObject(model.getBody());
        final String time = jsonObject.getString("time");
        log.info("本地视频的开始时间"+time);
        final String startTime = jsonObject.getString("startTime");
        log.info("本地视频筛选的开始时间"+startTime);
        final String endTime = jsonObject.getString("endTime");
        log.info("本地视频筛选的结束时间"+endTime);
        if(Integer.parseInt(endTime)<=Integer.parseInt(time)){
            sendMessage(ctx,"结束的时间不能小于文件的开始时间",false,5);
            return;
        }

        if(Integer.parseInt(endTime)<=Integer.parseInt(startTime)){
            sendMessage(ctx,"结束时间必须大于开始时间",false,5);
            return;
        }

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                File localVideoFile = FileUtil.getLocalVideoPath(time, startTime, endTime);
                if(localVideoFile==null){
                    sendMessage(ctx,"传入的视频名称错误",false,5);
                }else{
                    sendFileHandle(localVideoFile,"2");

                }
            }
        });
        thread.start();


    }



    private void sendResult(ChannelHandlerContext ctx, Model model,JSONObject jsonObject){
        model.setType(5);
        jsonObject.put("data","视频不存在");
        jsonObject.put("result",false);
        jsonObject.put("number",FileUtil.getPropertiesForName("number"));
        model.setBody(jsonObject.toString());
        ctx.writeAndFlush(model);

    }

    protected void sendLive(ChannelHandlerContext ctx, Object msg) {
        log.info("-----------关于直播的开起和关闭");
        Model model = (Model) msg;
        log.info("------------传过来的参数"+model);
        String body = model.getBody();
        this.flag = Boolean.valueOf(body);
        if(body.equals("true")){
            if(ffmpegRts==null){
                log.info("-------------第一次启动视频推送");
                if(rtspVideo == null){
                    rtspVideo = new RtspVideo();
                }
                rtspVideo.setFlag(this.flag);
                ffmpegRts = new Thread(rtspVideo);
                sendMessage(ctx,"启动成功",true,6);
                ffmpegRts.start();
            }else{
                if(!ffmpegRts.isAlive()){
                    log.info("----------关闭中重新启动");
                    if(rtspVideo==null){
                        rtspVideo = new RtspVideo();
                    }
                    rtspVideo.setFlag(flag);
                    ffmpegRts=new Thread(rtspVideo);
                    ffmpegRts.start();
                    sendMessage(ctx,"启动成功",true,6);
                }
            }
        }else{
            String id = HttpUtil.sendHttp("1");
            if(!StringUtils.isEmpty(id)){
                if(HttpUtil.stopRtsp(id)){
                    rtspVideo.desory();
                   sendMessage(ctx,"停止成功",true,6);
                }else{
                    sendMessage(ctx,"停止失败",false,6);
                }
            }
        }
    }

    protected void sendLocalVideo(ChannelHandlerContext ctx, Object msg) {
        log.info("-----------------------循环推送本地视频");
        Model model = (Model)msg;
        String body = model.getBody();
        this.localFlag = Boolean.valueOf(body);
        if(body.equals("true")){
            if(rtspLocalvideoThread==null){
                if(retspLocalVideo==null){
                    retspLocalVideo = new RetspLocalVideo();
                }
                rtspLocalvideoThread = new Thread(retspLocalVideo );
                sendMessage(ctx,"启动成功",true,8);
                rtspLocalvideoThread.start();

            }else{
                sendMessage(ctx,"启动失败,当前有正在推送的视频请关闭后重新开启",false,8);
            }
        }else{
            String id = HttpUtil.sendHttp("2");
            boolean flag = stopRtsp(id, ctx);
            if(flag){
                ProcessFfmpeg processFfmpeg = retspLocalVideo.getProcessFfmpeg();
                if (processFfmpeg==null) {
                    sendMessage(ctx,"该设备已经停止推送本地视频",false,8);
                    return;
                }
                processFfmpeg.destory();
                sendMessage(ctx,"停止成功",true,8);
            }else{
                sendMessage(ctx,"该设备已经停止推送本地视频",false,8);
            }

        }
    }


    private boolean stopRtsp(String id,ChannelHandlerContext ctx){
        if(!StringUtils.isEmpty(id)){
            return HttpUtil.stopRtsp(id);
        }
        return false;
    }

    protected void startLocalSaveVideo(ChannelHandlerContext ctx, Object msg) {
        log.info("开启保存视频的命令");
        Model model = (Model) msg;
        JSONObject json = JSONObject.fromObject(model.getBody());
//        this.saveFlag = json.getBoolean("flag");
        if(json.getBoolean("flag")){
            //开启保存视频
            switch (json.getInt("type")){
                //保存实时流
                case 1:
                    sendLocalSave(ctx);
                    break;
                //保存本地流
                case 2:
                    moveVideopath();
                    break;
                default:
                    sendMessage(ctx,"传入的参数错误",false,9);
                    break;

            }
        }else{
            //关闭保存视频
            ProcessFfmpeg processFfmpeg = rtspSaveVideo.getProcessFfmpeg();
            if(processFfmpeg==null){
                sendMessage(ctx,"本地视频保存已经完成不需要停止",false,9);
                return;
            }
            processFfmpeg.destory();
            sendMessage(ctx,"关闭成功",false,9);
        }

    }




    private void sendFileHandle(File file,String type){
        FileUploadFile uploadFile = new FileUploadFile();
        String fileMd5 = file.getName();// 文件名
        uploadFile.setFile(file);
        uploadFile.setFile_md5(fileMd5);
        uploadFile.setStarPos(0);// 文件开始位置
        uploadFile.setNumber(FileUtil.getPropertiesForName("number"));
        int port = 10002;
        NeetyCline neetyCline = new NeetyCline();
        //39.107.254.170
        try {
            neetyCline.connect(port, FileUtil.getPropertiesForName("ipaddress"), uploadFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.debug("执行完成");
        //删除临时文件
        FileUtil.delete(type);
    }


    //将本地摄像头的流保存在 指定路径下
    private void moveVideopath(){
        /*String videopath = FileUtil.getPropertiesForName("videopath");
        File file = new File(videopath);
        File targetFile = new File(FileUtil.getPropertiesForName("filepath")+"/"+DateUtil.getStringForDate()+"/"+"localvideo");
        if(file.exists()){
            if(!targetFile.exists()){
                targetFile.mkdirs();
                log.info("文件创建完成"+targetFile.getPath());
            }
            FileChannel inChannel = null;
            FileChannel outChennel = null;
            try {
                inChannel = FileChannel.open(Paths.get(videopath), StandardOpenOption.READ);
                outChennel = FileChannel.open(Paths.get(targetFile.getPath()+"/"+file.getName()),StandardOpenOption.WRITE,StandardOpenOption.READ,
                        StandardOpenOption.TRUNCATE_EXISTING,StandardOpenOption.CREATE);
                outChennel.transferFrom(inChannel,0,inChannel.size());
               log.info("保存完成");
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    if(inChannel==null){
                        inChannel.close();
                    }
                    if(outChennel!=null){

                    }
                    outChennel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }*/
    }


    //保存摄像头的流
    private void sendLocalSave(ChannelHandlerContext ctx){
        if(rtspVoidThread==null){
            if(rtspSaveVideo==null){
                rtspSaveVideo = new RtspSaveVideo();
            }
            rtspVoidThread = new Thread(rtspSaveVideo);
            rtspVoidThread.start();
            sendMessage(ctx,"保存摄像头的视频流启动成功",true,9);
        }else{
            sendMessage(ctx,"当前有保存视频流正在进行请先关闭",false,9);
        }
    }


    private void sendMessage(ChannelHandlerContext ctx,
                             String data,
                             boolean flag,
                             int type){
        Model model = new Model();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data",data);
        jsonObject.put("result",flag);
        jsonObject.put("number",FileUtil.getPropertiesForName("number"));
        model.setBody(jsonObject.toString());
        model.setType(type);
        ctx.writeAndFlush(model);

    }





    @Override
    protected void handlerAllIdle(ChannelHandlerContext ctx) {
        // TODO Auto-generated method stub
        log.info("-----------------------------------");
        if(ffmpegRts!=null&& flag){
            log.info("推送视频直播的线程运行状态是"+ffmpegRts.isAlive());
            if(!ffmpegRts.isAlive()){
                //说明线程中断发生异常 重新推送
                //new RtspVideo()
                if(rtspVideo==null){
                    rtspVideo = new RtspVideo();
                }
                rtspVideo.setFlag(flag);
                ffmpegRts=new Thread(rtspVideo);
                ffmpegRts.start();

            }
        }
        /*if(localVideoThread!=null){
            log.info("保存本地视频流运行状态是"+localVideoThread.isAlive());
            if(!localVideoThread.isAlive()){
                if(retspSaveLocalVideo==null){
                    retspSaveLocalVideo = new RetspSaveLocalVideo();
                }
                localVideoThread = new Thread(retspSaveLocalVideo);
                localVideoThread.start();
            }
        }*/

        if(rtspLocalvideoThread!=null&&this.localFlag){
            log.info("推送本地视频流的运行状态是"+rtspLocalvideoThread.isAlive());
            if(!rtspLocalvideoThread.isAlive()){
                if(retspLocalVideo==null){
                    retspLocalVideo = new RetspLocalVideo();
                }
                rtspLocalvideoThread = new Thread(retspLocalVideo);
                rtspLocalvideoThread.start();
            }

        }
        if(FileUtil.getPropertiesForName("isrtspvideo").equals("0")){
            log.info("保存rtsp视频流的线程运行状态是"+rtspVoidThread.isAlive());
            if(rtspSaveVideo==null){
                log.info("--------------只运行一次");
                rtspSaveVideo = new RtspSaveVideo();
            }
            if(!rtspVoidThread.isAlive()){
                rtspVoidThread = new Thread(rtspSaveVideo);
                rtspVoidThread.start();
            }
            //说明跑了一夜没有断开设备 所以需要在新的日期断开重新生成
            if(!DateUtil.getData().equals(DateUtil.getNoewData())){
                log.info("---------销毁当前的进程重新绑定 ");
                rtspSaveVideo.getProcessFfmpeg().destory();
            }
        }

        log.info("-------------------------------------");
        super.handlerAllIdle(ctx);
        sendPingMsg(ctx);
    }
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // TODO Auto-generated method stub
        super.channelInactive(ctx);
        client.doConnect();
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("发生异常"+cause);
        Throwable throwable = new Throwable(cause);
        throwable.printStackTrace();
        Model model = new Model();
        model.setType(7);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("number",FileUtil.getPropertiesForName("number"));
        jsonObject.put("type",this.type);
        jsonObject.put("data",cause.toString());
        model.setBody("服务器异常");
        ctx.writeAndFlush(model);
        log.error(name + "exception :"+ cause.toString());
    }

}
