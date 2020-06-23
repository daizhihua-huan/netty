package server;

import ch.qos.logback.core.net.server.Client;
import com.corundumstudio.socketio.SocketIOClient;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@ChannelHandler.Sharable
public class ServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("msg是"+msg);
        System.out.println(ctx.name());

        ChannelId cid=ctx.channel().id();
        Map<ChannelId,ChannelHandlerContext> map=new HashMap<>();
        map.put(cid,ctx);
        Set<ChannelId> set= map.keySet();

        for (ChannelId c:set){
            if(ctx.channel().id().equals(c)){

                ChannelHandlerContext ctx1= map.get(c);
                String respStr = new StringBuilder("来自服务器的响应").append("给客户端的响应。。。。。").append(c).toString();
                ctx1.writeAndFlush(Unpooled.copiedBuffer(respStr.getBytes()));

            }

        }





        ByteBuf bb = (ByteBuf)msg;

        byte[] bytes=new byte[bb.readableBytes()];
        bb.readBytes(bytes);
        String reqStr = new String(bytes,"UTF-8");

        System.out.println("客户端："  + reqStr+"上线了");



        String respStr = new StringBuilder("来自服务器的响应").append(reqStr).append("给客户端的响应。。。。。").toString();
        ctx.writeAndFlush(Unpooled.copiedBuffer(respStr.getBytes()));

    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.err.println("服务端读取数据完毕");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);

    }

    // 出现异常的处理
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.err.println("server 读取数据出现异常");
        ctx.close();
    }
}
