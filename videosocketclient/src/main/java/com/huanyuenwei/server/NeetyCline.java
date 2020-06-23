package com.huanyuenwei.server;

import com.huanyuenwei.Entuty.FileUploadFile;
import com.huanyuenwei.exmaple.common.MsgPckDecode;
import com.huanyuenwei.exmaple.common.MsgPckEncode;
import com.huanyuenwei.util.FileUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.io.File;

public class NeetyCline  {
    private FileUploadClientHandler fileUploadClientHandler;

    public void connect(int port, String host,FileUploadFile fileUploadFile) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        fileUploadClientHandler = new FileUploadClientHandler(fileUploadFile);
        try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<Channel>() {

                @Override
                protected void initChannel(Channel ch) throws Exception {
                    ch.pipeline().addLast(new ObjectEncoder());
//                    ch.pipeline().addLast(new IdleStateHandler(10,0,0));
//                    ch.pipeline().addLast(new MsgPckDecode());
                    ch.pipeline().addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.weakCachingConcurrentResolver(null))); //
//                    ch.pipeline().addLast(new MsgPckEncode());
//                    ch.pipeline().addLast(new ObjectDecoder(ClassResolvers.weakCachingConcurrentResolver(null)));
                    ch.pipeline().addLast(fileUploadClientHandler);
                }
            });
            ChannelFuture f = b.connect(host, port).sync();
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }

    }



}
