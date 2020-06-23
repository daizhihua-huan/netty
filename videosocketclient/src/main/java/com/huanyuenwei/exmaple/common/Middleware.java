package com.huanyuenwei.exmaple.common;

import com.huanyuenwei.util.FileUtil;
import com.sun.org.apache.xpath.internal.operations.Mod;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;

public abstract class Middleware extends ChannelInboundHandlerAdapter{



    protected String name;
    //记录次数
    private int heartbeatCount = 0;
    //区分是查找直播还是视频回看
    protected String type;

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
        System.out.println("msg是："+msg);
        if(msg instanceof Model){    //如果是Model类型
            Model m = (Model) msg;
            int type = m.getType();
            switch (type) {
                case 1:
                    sendPongMsg(ctx);
                    break;
                case 2:
                    System.out.println(name + " get  pong  msg  from" + ctx.channel().remoteAddress());
                    break;
                case 3:
                    handlerData(ctx,msg);
                    break;
                case 4:
                    //处理文件传输的
                    this.type="1";
                    fileHandlerData(ctx,msg);
                    break;
                case 5:
                    //开启和关闭直播推送
                    this.type="2";
                    sendLive(ctx,msg);
                    break;
                case 6:
                    this.type="2";
                    //推送本地的视频
                    sendLocalVideo(ctx,msg);
                    break;
                case 7:
                    //开启保存视频
                    startLocalSaveVideo(ctx,msg);
                    break;
                case 8:
                    selectLocalVideo(ctx,msg);
                    break;
                default:

                    break;
        }

        }
    }

    protected abstract void handlerData(ChannelHandlerContext ctx,Object msg);

    protected abstract void fileHandlerData(ChannelHandlerContext ctx,Object msg) throws Exception;

    protected abstract void sendLive(ChannelHandlerContext ctx,Object msg);

    protected abstract void sendLocalVideo(ChannelHandlerContext ctx,Object msg);

    protected abstract void startLocalSaveVideo(ChannelHandlerContext ctx,Object msg);


    protected abstract void selectLocalVideo(ChannelHandlerContext ctx,Object msg);

    protected void sendPingMsg(ChannelHandlerContext ctx){
        Model model = new Model();
        model.setType(TypeData.PING);
        model.setBody(FileUtil.getPropertiesForName("number"));
        ctx.channel().writeAndFlush(model);// 把响应刷到客户端

        heartbeatCount++;

        System.out.println(name + " send ping msg to " + ctx.channel().remoteAddress() + "count :" + heartbeatCount);
    }

    private void sendPongMsg(ChannelHandlerContext ctx) {
        Model model = new Model();
        model.setType(TypeData.PONG);
        model.setBody(FileUtil.getPropertiesForName("number"));//拿到设备编号
        ctx.channel().writeAndFlush(model);
        heartbeatCount++;

        System.out.println(name +" send pong msg to "+ctx.channel().remoteAddress() +" , count :" + heartbeatCount);
    }

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

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // TODO Auto-generated method stub
        /*Model model = new Model();
        model.setType(TypeData.cline);
        model.setBody(FileUtil.getPropertiesForName("number"));
        ctx.writeAndFlush(model);*/
        System.err.println(" ---"+ctx.channel().remoteAddress() +"----- is  action" );
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // TODO Auto-generated method stub
        System.err.println(" ---"+ctx.channel().remoteAddress() +"----- is  inAction");
    }


}
