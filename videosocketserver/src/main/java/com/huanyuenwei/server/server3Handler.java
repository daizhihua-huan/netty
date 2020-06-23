package com.huanyuenwei.server;

import com.huanyuenwei.Entuty.NanoEntity;
import com.huanyuenwei.common.Middleware;
import com.huanyuenwei.common.Model;
import com.huanyuenwei.common.TypeData;
import com.huanyuenwei.linster.ResultLinster;
import com.huanyuenwei.util.NanoConfigUtil;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import java.io.IOException;
@Slf4j
public class server3Handler extends Middleware {

    SimpMessagingTemplate template;


    public server3Handler(SimpMessagingTemplate template) {
        super("server");
        // TODO Auto-generated constructor stub
        this.template = template;
    }
    @Override
    protected void handlerData(ChannelHandlerContext ctx, Object msg) {
        // TODO Auto-generated method stub
        Model model  = (Model) msg;
        System.out.println("server 接收数据 ： " +  model.toString());
        model.setType(TypeData.CUSTOMER);
        model.setBody("---------------");
        ctx.channel().writeAndFlush(model);
        System.out.println("server 发送数据： " + model.toString());
    }

    protected void fileHanlerData(ChannelHandlerContext ctx, Object msg) throws IOException {

    }


    //说明客户端没有找到录像返回结果
    @Override
    protected void getMessageData(ChannelHandlerContext ctx, Object msg) {
        Model model = (Model)msg;
        JSONObject jsonObject = JSONObject.fromObject(model.getBody());
        log.info("拿到没有视频的数据或者报错的数据"+model);
        String number = jsonObject.getString("number");
        NanoEntity numberByNanoEntity = NanoConfigUtil.getNumberByNanoEntity(number);
        if(numberByNanoEntity==null){
            numberByNanoEntity = new NanoEntity();
            numberByNanoEntity.setNumber(number);
            numberByNanoEntity.setVideoFlag(jsonObject.getBoolean("result"));
            numberByNanoEntity.setVideoData(jsonObject.getString("data"));
            NanoConfigUtil.add(number,numberByNanoEntity);
        }
        numberByNanoEntity.setVideoFlag(jsonObject.getBoolean("result"));
        numberByNanoEntity.setVideoData(jsonObject.getString("data"));
        template.convertAndSendToUser("1","luban","2");
//        resultLinster.sendResult(false);

    }

    /**
     * 客户端返回直播开启的结果
     * @param ctx
     * @param msg
     */
    @Override
    protected void liveMessageData(ChannelHandlerContext ctx, Object msg) {
        Model model = (Model) msg;
        JSONObject jsonObject = JSONObject.fromObject(model.getBody());
        NanoEntity nanoEntity = NanoConfigUtil.getNumberByNanoEntity(jsonObject.getString("number"));
        if(nanoEntity==null){
            nanoEntity = new NanoEntity();
            nanoEntity.setNumber(jsonObject.getString("number"));
            nanoEntity.setData(jsonObject.getString("data"));
            nanoEntity.setFlag(jsonObject.getBoolean("result"));
            NanoConfigUtil.add(jsonObject.getString("number"),nanoEntity);
        }
        nanoEntity.setData(jsonObject.getString("data"));
        nanoEntity.setFlag(jsonObject.getBoolean("result"));
    }

    @Override
    protected void localMessageData(ChannelHandlerContext ctx, Object msg) {
        Model model = (Model) msg;
        JSONObject jsonObject = JSONObject.fromObject(model.getBody());
        NanoEntity nanoEntity = NanoConfigUtil.getNumberByNanoEntity(jsonObject.getString("number"));
        if(nanoEntity==null){
            nanoEntity = new NanoEntity();
            nanoEntity.setNumber(jsonObject.getString("number"));
            nanoEntity.setLocalData(jsonObject.getString("data"));
            nanoEntity.setLocaLflag(jsonObject.getBoolean("result"));
            NanoConfigUtil.add(jsonObject.getString("number"),nanoEntity);
        }
        nanoEntity.setLocalData(jsonObject.getString("data"));
        nanoEntity.setFlag(jsonObject.getBoolean("result"));

    }

    @Override
    protected void localMessageSaveData(ChannelHandlerContext ctx, Object msg) {
        Model model = (Model) msg;
        JSONObject jsonObject = JSONObject.fromObject(model.getBody());
        NanoEntity nanoEntity = NanoConfigUtil.getNumberByNanoEntity(jsonObject.getString("number"));
        if(nanoEntity==null){
            nanoEntity = new NanoEntity();
            nanoEntity.setNumber(jsonObject.getString("number"));
            nanoEntity.setLocalSaveData(jsonObject.getString("data"));
            nanoEntity.setLocaLflag(jsonObject.getBoolean("result"));
            NanoConfigUtil.add(jsonObject.getString("number"),nanoEntity);
        }
        nanoEntity.setLocalSaveData(jsonObject.getString("data"));
        nanoEntity.setLocaLflag(jsonObject.getBoolean("result"));

    }

    /**
     * 返回客户端异常的结果
     * @param ctx
     * @param msg
     */
    @Override
    protected void errorMessageData(ChannelHandlerContext ctx, Object msg) {
        Model model = (Model) msg;
        JSONObject jsonObject = JSONObject.fromObject(model.getBody());
        String number = jsonObject.getString("number");
        String type = jsonObject.getString("type");
        NanoEntity numberByNanoEntity = NanoConfigUtil.getNumberByNanoEntity(number);
        if(numberByNanoEntity==null){
            numberByNanoEntity = new NanoEntity();
            numberByNanoEntity.setNumber(number);
        }
        switch (type){
            case "1":
                numberByNanoEntity.setFlag(false);
                numberByNanoEntity.setData(jsonObject.getString("data"));
                break;
            case "2":
                numberByNanoEntity.setVideoFlag(false);
                numberByNanoEntity.setVideoData(jsonObject.getString("data"));
                break;
            default:
                break;
        }
    }

    @Override
    protected void handlerReaderIdle(ChannelHandlerContext ctx) {
        // TODO Auto-generated method stub
        super.handlerReaderIdle(ctx);
        System.err.println(" ---- client "+ ctx.channel().remoteAddress().toString() + " reader timeOut, --- close it");
        ctx.close();
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        Throwable throwable = new Throwable(cause);
        log.info("---------------------出错了");
        throwable.printStackTrace();
        System.err.println( name +"  exception" + cause.toString());
    }
}
