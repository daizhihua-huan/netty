package com.huanyuenwei.exmaple.client;

import com.huanyuenwei.exmaple.common.Model;
import com.huanyuenwei.exmaple.common.MsgPckDecode;
import com.huanyuenwei.exmaple.common.MsgPckEncode;
import com.huanyuenwei.exmaple.common.TypeData;
import com.huanyuenwei.exmaple.ffmpeg.RetspSaveLocalVideo;
import com.huanyuenwei.exmaple.ffmpeg.RtspSaveVideo;
import com.huanyuenwei.exmaple.ffmpeg.RtspSaveVideo2;
import com.huanyuenwei.exmaple.ffmpeg.RtspVideo;
import com.huanyuenwei.util.FileUtil;
import com.huanyuenwei.util.SaveVideoUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;
@Slf4j
public class Client {

    private NioEventLoopGroup worker = new NioEventLoopGroup();

    private Channel channel;

    private Bootstrap bootstrap;

    private Client3Handler client3Handler;
    private Client3Handler client3Handler2;


    private Thread rtspVoidThread;
    private Thread rtspVoidThread2;

    private Thread localVideoThread;

    /**
     * 保存rtsp视频类
     */
    private RtspSaveVideo rtspSaveVideo;
    private RtspSaveVideo2 rtspSaveVideo2;
    private RetspSaveLocalVideo retspSaveLocalVideo;



    public static void main(String[] args) {
        Client  client = new Client();
        client.start();

    }

    private void start() {
        bootstrap = new Bootstrap();
        bootstrap.group(worker)   //注册线程池
                .channel(NioSocketChannel.class) // 使用NioSocketChannel来作为连接用的channel类
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        // TODO Auto-generated method stub
                        ChannelPipeline pipeline = ch.pipeline();
                        //心跳机制
                        pipeline.addLast(new IdleStateHandler(0,0,5));

                        //编码解码器
                        pipeline.addLast(new MsgPckDecode());
                        pipeline.addLast(new MsgPckEncode());
                        //存储本地视频
                        /**
                         * 存储本地视频 根据配置文件 开启或者关闭
                         */
                       /* if(FileUtil.getPropertiesForName("islocalvideo").equals("0")){
                            if(localVideoThread==null){
                                if(retspSaveLocalVideo==null){
                                    retspSaveLocalVideo = new RetspSaveLocalVideo();
                                }
                                localVideoThread = new Thread(retspSaveLocalVideo);
                                localVideoThread.start();
                            }
//                            SaveVideoUtil.moveVideopath();
                        }*/


                        if(FileUtil.getPropertiesForName("islocalvideo").equals("0")){
                            if(localVideoThread==null){
                                localVideoThread = new Thread(new SaveVideoUtil());
                                //启动本地视频保存
                                localVideoThread.start();
                            }
//                            SaveVideoUtil.moveVideopath();
                        }

                        /**
                         * 存储rtsp的视频流
                         */
                        if(FileUtil.getPropertiesForName("isrtspvideo").equals("0")){
                            if(rtspVoidThread == null){
                                if(rtspSaveVideo==null){
                                    rtspSaveVideo = new RtspSaveVideo();
                                }
                                rtspVoidThread = new Thread(rtspSaveVideo);

                                rtspVoidThread.start();
                            }
                        }

                        if(FileUtil.getPropertiesForName("isrtspvideo").equals("0")){
                            if(rtspVoidThread2 == null){
                                if(rtspSaveVideo2==null){
                                    rtspSaveVideo2 = new RtspSaveVideo2();
                                }
                                rtspVoidThread2 = new Thread(rtspSaveVideo2);

                                rtspVoidThread2.start();
                            }
                        }

                        if(client3Handler==null){
                            client3Handler = new Client3Handler(Client.this,rtspVoidThread,rtspSaveVideo);

                        }
                        if(client3Handler2==null){
                            client3Handler2 = new Client3Handler(Client.this,rtspVoidThread2,rtspSaveVideo2);
                        }
                        //自定义助手类
                        pipeline.addLast(client3Handler);
                        pipeline.addLast(client3Handler2);
                    }
                });

        doConnect();
    }

    /**
     * 连接服务端 and 重连
     */
    protected void doConnect() {

        if (channel != null && channel.isActive()){
            return;
        }
        //39.107.254.170
        ChannelFuture connect = bootstrap.connect(FileUtil.getPropertiesForName("ipaddress"), 10001);
        //实现监听通道连接的方法
        connect.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if(channelFuture.isSuccess()){
                    channel = channelFuture.channel();
                    log.info("连接成功");
                    Model model = new Model();
                    model.setType(TypeData.cline);
                    model.setBody(FileUtil.getPropertiesForName("number"));
                    channel.writeAndFlush(model);
                }else{
                    log.info("每隔2s重连....");
                    channelFuture.channel().eventLoop().schedule(new Runnable() {
                        public void run() {
                            // TODO Auto-generated method stub
                            doConnect();
                        }
                    },2, TimeUnit.SECONDS);
                }
            }
        });
    }


}
