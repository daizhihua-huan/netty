package com.huanyuenwei.server;

import com.huanyuenwei.Entuty.FileUploadFile;
import com.huanyuenwei.util.FileUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

@Slf4j
public class FileUploadClientHandler  extends ChannelInboundHandlerAdapter {

    private int byteRead;
    private volatile int start = 0;
    private volatile int lastLength = 0;
    public RandomAccessFile randomAccessFile;


   private FileUploadFile fileUploadFile;



    public FileUploadClientHandler(FileUploadFile ef) {
        if (ef.getFile().exists()) {
            if (!ef.getFile().isFile()) {
                System.out.println("Not a file :" + ef.getFile());
                return;
            }
        }
        this.fileUploadFile = ef;
    }

    /**
     * 当连接成功调用nio 读取文件提交效率
     * @param ctx
     */
    public void channelActive(ChannelHandlerContext ctx) {
        try {
            randomAccessFile = new RandomAccessFile(fileUploadFile.getFile(), "r");
            randomAccessFile.seek(fileUploadFile.getStarPos());
            long totalLen=randomAccessFile.length();
            log.info("返回文件长度"+totalLen);
            //lastLength = (int) randomAccessFile.length() / 10;
            lastLength = (int) fileUploadFile.getFile().length();
            FileChannel channel=randomAccessFile.getChannel();
            ByteBuffer bf = ByteBuffer.allocate(lastLength);
//            byte[] bytes = new byte[lastLength];
            if ((byteRead = channel.read(bf)) != -1) {
                fileUploadFile.setEndPos(byteRead);
                fileUploadFile.setBytes(bf.array());
                fileUploadFile.setLength(fileUploadFile.getFile().length());
                ctx.writeAndFlush(fileUploadFile);
                randomAccessFile.close();
            } else {
                randomAccessFile.close();
                ctx.close();

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException i) {
            i.printStackTrace();
        }finally {
//            ctx.close();
        }

    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
       randomAccessFile.close();
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof String){
            log.info("输出消息:"+msg.toString());
        }
        if (msg instanceof Integer) {
            start = (Integer) msg;
            if (start != -1) {
                randomAccessFile = new RandomAccessFile(fileUploadFile.getFile(), "r");
                randomAccessFile.seek(start);
                System.out.println("块儿长度：" + (randomAccessFile.length() / 10));
                System.out.println("长度：" + (randomAccessFile.length() - start));
                int a = (int) (randomAccessFile.length() - start);
                int b = (int) (randomAccessFile.length() / 10);
                if (a < b) {
                    lastLength = a;
                }
                byte[] bytes = new byte[lastLength];
                System.out.println("-----------------------------" + bytes.length);
                if ((byteRead = randomAccessFile.read(bytes)) != -1 && (randomAccessFile.length() - start) > 0) {
                    System.out.println("byte 长度：" + bytes.length);
                    fileUploadFile.setEndPos(byteRead);
                    fileUploadFile.setBytes(bytes);
                    try {
                        ctx.writeAndFlush(fileUploadFile);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    log.info("文件已经读完");
                    /*fileUploadFile.setEndPos(byteRead);
                    fileUploadFile.setBytes(null);
                    log.info("最后写的文件是"+fileUploadFile);
                    ctx.writeAndFlush(fileUploadFile);*/
                    randomAccessFile.close();
                    ctx.close();
                    fileUploadFile =null;
                    log.info("文件已经读完--------" + byteRead);
                }
            }
        }
    }



    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        Throwable throwable = new Throwable(cause);
        throwable.printStackTrace();
        log.info("客户端发生异常");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("number", FileUtil.getPropertiesForName("number"));
        jsonObject.put("data",cause.toString());
        ctx.writeAndFlush(jsonObject.toString());
        cause.printStackTrace();
        ctx.close();
    }

}
