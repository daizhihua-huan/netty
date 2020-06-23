package com.huanyuenwei.server;

import com.huanyuenwei.common.MsgPckDecode;
import com.huanyuenwei.common.MsgPckEncode;
import com.huanyuenwei.controller.GreetingController;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class Server implements Runnable, InitializingBean {

    @Autowired
    private SimpMessagingTemplate template;



    public void init(){
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);

        EventLoopGroup workerGroup = new NioEventLoopGroup(4);
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();

            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(10001)
                    .childHandler(new ChannelInitializer<Channel>() {

                        protected void initChannel(Channel ch) throws Exception {
                            // TODO Auto-generated method stub
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new IdleStateHandler(60,0,0));
//                            pipeline.addLast(new LengthFieldBasedFrameDecoder(65535,0,2,0,2));
                            pipeline.addLast(new MsgPckDecode());
                            //pipeline.addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.weakCachingConcurrentResolver(null))); //
                            pipeline.addLast(new MsgPckEncode());
//                            pipeline.addLast(new ObjectEncoder());
                            pipeline.addLast(new server3Handler(template));
                        }
                    });
            ChannelFuture sync = serverBootstrap.bind().sync();
            sync.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            //优雅的关闭资源
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
    @Override
    public void run() {
        init();
    }
    @Override
    public void afterPropertiesSet() throws Exception {
        Thread thread = new Thread(this);
        thread.start();
    }
}
