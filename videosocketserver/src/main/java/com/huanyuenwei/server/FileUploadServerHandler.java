package com.huanyuenwei.server;

import com.huanyuenwei.Entuty.FileUploadFile;
import com.huanyuenwei.Entuty.NanoEntity;
import com.huanyuenwei.linster.LinsterByte;
import com.huanyuenwei.linster.ResultLinster;
import com.huanyuenwei.util.DateUtil;
import com.huanyuenwei.util.NanoConfigUtil;
import com.huanyuenwei.util.NettConfigUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

@Slf4j
public class FileUploadServerHandler extends ChannelInboundHandlerAdapter {

    private int byteRead;
    private volatile int start = 0;
//    private String file_dir="c:\\video\\datasouce\\data";
    private String file_dir = getFilePath("c:\\video\\datasouce\\data");
    private String key;


    private SimpMessagingTemplate template;
    private RandomAccessFile randomAccessFile = null;


    FileUploadServerHandler( SimpMessagingTemplate template){
        this.template = template;
    }

    private String getFilePath(String path){

        delete(path);

        File file = new File(path+"\\"+DateUtil.getByTadyForDate());
        if(!file.exists()){
            file.mkdirs();
        }
        return file.getPath();
    }


    private void delete(String path){
        //删除当前目录下的所有文件
        File file = new File(path);
        if(file.exists()){
            File[] files = file.listFiles();
            for (File fileindex : files) {
                if(fileindex.isDirectory()){
                    delete(fileindex.getPath());
                }else{
                    fileindex.delete();
                }
            }
            file.delete();
        }

    }

    private byte[] bytes;

    @Override
    public void channelActive(ChannelHandlerContext ctx){
        System.out.println("客户端连接开始");
        ctx.writeAndFlush("连接成功");
        if(randomAccessFile!=null){
            try {
                randomAccessFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //todo 将连接的客户端放在map中
    }



    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
       //todo 将连接的客户端移除
        if(randomAccessFile!=null){
            try {
                randomAccessFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ctx.close();

    }


    @Override
    public void channelReadComplete(ChannelHandlerContext ctx)  {

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(StringUtils.isEmpty(msg)){
            ctx.writeAndFlush("连接成功");
        }
        if (msg instanceof FileUploadFile) {
            FileUploadFile ef = (FileUploadFile) msg;
            byte[] bytes = ef.getBytes();

            byteRead = ef.getEndPos();
            log.info("写文件的数"+byteRead);
            NanoEntity numberByNanoEntity = NanoConfigUtil.getNumberByNanoEntity(ef.getNumber());
            if(numberByNanoEntity==null){
                numberByNanoEntity = new NanoEntity();
                numberByNanoEntity.setNumber(ef.getNumber());
                numberByNanoEntity.setBytes(bytes);
                numberByNanoEntity.setLength(ef.getLength());
                numberByNanoEntity.setVideoFlag(true);
                numberByNanoEntity.setVideoData("success");
                NanoConfigUtil.add(ef.getNumber(),numberByNanoEntity);
            }else{
                numberByNanoEntity.setBytes(bytes);
                numberByNanoEntity.setVideoFlag(true);
                numberByNanoEntity.setVideoData("success");
                numberByNanoEntity.setLength(ef.getLength());
            }
            template.convertAndSendToUser("1","luban",file_dir.substring(file_dir.lastIndexOf("\\")+1));
            if(byteRead!=0){
                String md5 = ef.getFile_md5();//文件名
                String path = file_dir + File.separator + md5;
                File file = new File(path);
                //如果文件存在
                if(file.exists()){
                    file.delete();
                }
                if(randomAccessFile==null){
                    randomAccessFile = new RandomAccessFile(file, "rw");
                }
                ByteBuffer byteBuffer =  ByteBuffer.wrap(bytes);
                FileChannel fos = randomAccessFile.getChannel();
                byteBuffer.put(bytes);
                byteBuffer.flip();
                fos.write(byteBuffer);
                fos.close();
                start = start + byteRead;
                ctx.writeAndFlush(start);
            } else {
                log.info("发送消息的类是"+template);
                log.info("文件目录是"+file_dir);
                if(randomAccessFile!=null){
                    randomAccessFile.close();
                }
                ctx.writeAndFlush("文件接收完了------开始通知客户端读取视屏");
                ctx.close();
            }
        }else if(msg instanceof JSONObject){
            String number = ((JSONObject) msg).getString("number");
            NanoEntity numberByNanoEntity = NanoConfigUtil.getNumberByNanoEntity(number);
            if(numberByNanoEntity==null){
                numberByNanoEntity.setNumber(number);
                numberByNanoEntity.setVideoFlag(false);
                numberByNanoEntity.setVideoData(((JSONObject) msg)
                        .getString("data"));
                NanoConfigUtil.add(number,numberByNanoEntity);
            }
            numberByNanoEntity.setVideoFlag(false);
            numberByNanoEntity.setVideoData(((JSONObject) msg)
                    .getString("data"));

        }

        else{
            System.out.println("msg是"+msg);
            if(msg.toString().equals("error")){
                //服务器异常
                template.convertAndSendToUser("1","luban","3");
//                resultLinster.sendResult(false);
            }
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("----------------------报错了");
        template.convertAndSendToUser("1","luban","3");
//        resultLinster.sendResult(false);
        Throwable throwable = new Throwable(cause);
        throwable.printStackTrace();
        System.out.println(throwable.toString());
        cause.printStackTrace();
        ctx.close();

    }



}
