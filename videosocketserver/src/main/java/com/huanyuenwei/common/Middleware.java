package com.huanyuenwei.common;

import com.huanyuenwei.Entuty.FileUploadFile;
import com.huanyuenwei.util.NettConfigUtil;
import com.sun.org.apache.xpath.internal.operations.Mod;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

@Slf4j
public abstract class Middleware extends ChannelInboundHandlerAdapter{


    protected String name;
    //记录次数
    private int heartbeatCount = 0;

    //获取server and client 传入的值
    public Middleware(String name) {
        this.name = name;
    }
    /**
     *继承ChannelInboundHandlerAdapter实现了channelRead就会监听到通道里面的消息
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        if(msg instanceof Model){
            Model m = (Model) msg;

            int type = m.getType();
            switch (type) {
                //0
                case TypeData.CLINE:
                    saveClient(ctx,m);
                    //1
                case TypeData.PING:
                    sendPongMsg(ctx,m);
                    break;
                    //2
                case TypeData.PONG:
                    //发送心跳包
//                    System.out.println(name + " get  pong  msg  from" + ctx.channel().remoteAddress());
                    break;
                    //3
                case TypeData.CUSTOMER:
                    handlerData(ctx,msg);
                    break;
                    //4
                case TypeData.FILENUMBER:
                    //查询客户端视屏
                    fileHanlerData(ctx,msg);
                    break;
                //说明返回错误信息
                //5
                case 5:
                    getMessageData(ctx,msg);
                    break;
                 //开启和关闭直播返回的消息结果
                case 6:
                    liveMessageData(ctx,msg);
                    break;
                case 7:
                    //服务端异常
                    errorMessageData(ctx,msg);
                    break;
                case 8:
                    //推送本地视频
                    localMessageData(ctx,msg);
                    break;
                case 9:
                    localMessageSaveData(ctx,msg);
                    break;
                default:
                    break;
            }
        }
    }

    protected abstract void handlerData(ChannelHandlerContext ctx,Object msg);

    protected abstract void fileHanlerData(ChannelHandlerContext ctx,Object msg) throws IOException;

    protected abstract void getMessageData(ChannelHandlerContext ctx,Object msg);

    //直播开启后的消息返回
    protected abstract void liveMessageData(ChannelHandlerContext ctx,Object msg);

    protected abstract void localMessageData(ChannelHandlerContext ctx,Object msg);

    //开启视频保存
    protected abstract void localMessageSaveData(ChannelHandlerContext ctx,Object msg);

    //异常错误
    protected abstract void errorMessageData(ChannelHandlerContext ctx,Object msg);

    protected void sendPingMsg(ChannelHandlerContext ctx){
        Model model = new Model();

        model.setType(TypeData.PING);

        ctx.channel().writeAndFlush(model);

        heartbeatCount++;

        System.out.println(name + " send ping msg to " + ctx.channel().remoteAddress() + "count :" + heartbeatCount);
    }

    //将连接的客户端保存在全局的静态变量值
    private void saveClient(ChannelHandlerContext ctx, Model model){
        log.info("-----------有编号为"+model.getBody()+"的设备连接");
        NettConfigUtil.add(model.getBody(),ctx);
    }



    private void sendPongMsg(ChannelHandlerContext ctx, Model parentModel) {
        log.info("心跳包接收"+parentModel);
        if(parentModel.getBody()!=null){
            if(NettConfigUtil.getKey(parentModel.getBody())==null){
                log.info("------------中途错误需要重连");
                NettConfigUtil.add(parentModel.getBody(),ctx);
            }
        }

        Model model = new Model();

        model.setType(TypeData.PONG);

        ctx.channel().writeAndFlush(model);

        heartbeatCount++;

//        System.out.println(name +" send pong msg to "+ctx.channel().remoteAddress() +" , count :" + heartbeatCount);
    }

    /**
     * 当时间失效的时候调用
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
            throws Exception {
        IdleStateEvent stateEvent = (IdleStateEvent) evt;

        switch (stateEvent.state()) {
            case READER_IDLE:
                handlerReaderIdle(ctx);
                break;
            case WRITER_IDLE:
                handlerWriterIdle(ctx);
                break;
            case ALL_IDLE:
                handlerAllIdle(ctx);
                break;
            default:
                break;
        }
    }

    protected void handlerAllIdle(ChannelHandlerContext ctx) {
        System.err.println("---ALL_IDLE---");
    }

    protected void handlerWriterIdle(ChannelHandlerContext ctx) {
        System.err.println("---WRITER_IDLE---");
    }


    protected void handlerReaderIdle(ChannelHandlerContext ctx) {
        System.err.println("---READER_IDLE---");
    }

    //客户端和服务端连接成功时候触发
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // TODO Auto-generated method stub
        System.err.println(" ---"+ctx.channel().remoteAddress() +"----- is  action" );
        Model model = new Model();
        model.setBody("连接成功");
        model.setType(3);
        ctx.writeAndFlush(model);
    }

    //客户端和服务端连接断开的时候被触发
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // TODO Auto-generated method stub
        System.err.println(" ---"+ctx.channel().remoteAddress() +"----- is  inAction");
        log.info("------------------------------断开连接");

        NettConfigUtil.remove(ctx);
    }

    //消息读取完毕后触发
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx){

    }


}
