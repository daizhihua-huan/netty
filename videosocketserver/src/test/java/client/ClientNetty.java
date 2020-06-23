package client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.UnsupportedEncodingException;
import java.net.SocketAddress;
import java.util.Scanner;

public class ClientNetty {
    // 要请求的服务器的ip地址
    private String ip;
    // 服务器的端口
    private int port;

    public ClientNetty(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public void start() throws InterruptedException, UnsupportedEncodingException {


        EventLoopGroup bossGroup = new NioEventLoopGroup();

        Bootstrap bs = new Bootstrap();

        bs.group(bossGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ClientHandler());


        ChannelFuture cf = bs.connect(ip, port).sync();

        while (true) {

            Scanner s = new Scanner(System.in);

            String reqStr = s.nextLine();
            if (reqStr.equals("0")) {
               // cf.channel().closeFuture().sync();
                break;
            }
            SocketAddress socketAddress=cf.channel().localAddress();
            ChannelId cid=cf.channel().id();
            String loc=socketAddress.toString();
            String scid=cid.toString();

            String locandcid=loc+":"+scid;

            // String reqStr = "我是客户端请求1$_";
            cf.channel().writeAndFlush(Unpooled.copiedBuffer(reqStr.getBytes("UTF-8")));
            cf.channel().writeAndFlush(Unpooled.copiedBuffer(locandcid.getBytes("UTF-8")));


        }


       /*  String reqStr = "我是客户端请求1$_";
        cf.channel().writeAndFlush(Unpooled.copiedBuffer(reqStr.getBytes("UTF-8")));

        cf.channel().closeFuture().sync();*/

    }

    public static void main(String[] args) throws UnsupportedEncodingException, InterruptedException {


            Thread t=new Thread(new ClientThread());
            t.run();




    }


}
