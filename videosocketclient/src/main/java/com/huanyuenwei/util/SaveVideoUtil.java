package com.huanyuenwei.util;

import com.huanyuenwei.exmaple.ffmpeg.ProcessFfmpeg;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@Slf4j
public class SaveVideoUtil implements Runnable{


    public  void moveVideopath(){
        //拿到videopath
        String videopath = FileUtil.getPropertiesForName("videopath");
        //拿到istranspose
        String istranspose = FileUtil.getPropertiesForName("istranspose");
        File file = new File(videopath); //做文件关联

        File targetFile = new File(FileUtil.getPropertiesForName("filepath")+"/"+DateUtil.getStringForDate()+"/"+"localvideo");
        if(file.exists()) {  //文件是否存在
            if (!targetFile.exists()) {
                targetFile.mkdirs();
                log.info("文件创建完成" + targetFile.getPath());
            }
            File souceFile = new File(targetFile.getPath()+"/"+file.getName());
            if(!souceFile.exists()){//不为空保存
                String cmd = "ffmpeg -i "+ videopath +" -s 640x480  -vcodec h264 -threads 5 -preset ultrafast -strict -2  -y -movflags faststart  -intra "+souceFile.getPath();
                if(istranspose.equals("0")){
                    cmd = "ffmpeg -i "+ videopath +" -s 640x480  -vcodec h264 -threads 5 -preset ultrafast -strict -2 -y  -intra -movflags faststart -vf transpose=2 "+souceFile.getPath();
                }
                ProcessFfmpeg process = new ProcessFfmpeg();
                process.sendMessage(cmd);  //执行命令
            }else{
                log.info("----------已经保存过了就不再保存了");
            }

        }else{
            log.info("文件不存在");
        }
              /* FileChannel inChannel = null;
            FileChannel outChennel = null;
            try {
                inChannel = FileChannel.open(Paths.get(videopath), StandardOpenOption.READ);
                outChennel = FileChannel.open(Paths.get(targetFile.getPath()+"/"+file.getName()),StandardOpenOption.WRITE,StandardOpenOption.READ,
                        StandardOpenOption.TRUNCATE_EXISTING,StandardOpenOption.CREATE);
                outChennel.transferFrom(inChannel,0,inChannel.size());
                log.info("保存完成");
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    if(inChannel==null){
                        inChannel.close();
                    }
                    if(outChennel!=null){

                    }
                    outChennel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }*/
    }

    @Override
    public void run() {
        moveVideopath();
    }

    public static void main(String[] args) {
        //ffmpeg -i 1.mp4 -s 640x480 -r 29.97 -b 1024k -vcodec h264 -threads 5 -preset ultrafast -strict -2 -y 1.mp4
//        System.out.println(System.currentTimeMillis());
//        long startTime =  System.currentTimeMillis();
//        String cmd = "ffmpeg -i E:\\node\\1.mp4 -s 640x480 -r 29.97 -b 1024k -vcodec h264 -threads 5 -preset ultrafast -strict -2 -y E:\\node\\out.mp4";
//        ProcessFfmpeg process = new ProcessFfmpeg();
//        process.sendMessage(cmd);
//        System.out.println(System.currentTimeMillis());
//        long endTime = System.currentTimeMillis();
//        System.out.println(((endTime - startTime) / 1000));
        SaveVideoUtil saveVideoUtil = new SaveVideoUtil();
        saveVideoUtil.moveVideopath();

    }
}
