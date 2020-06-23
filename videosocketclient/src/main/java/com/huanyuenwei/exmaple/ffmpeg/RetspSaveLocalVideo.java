package com.huanyuenwei.exmaple.ffmpeg;

import com.huanyuenwei.util.DateUtil;
import com.huanyuenwei.util.FileUtil;
import com.huanyuenwei.util.OSinfo;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

@Slf4j
public class RetspSaveLocalVideo implements Runnable {

    private ProcessFfmpeg processFfmpeg;
    @Override
    public void run() {

        String videopath = FileUtil.getPropertiesForName("videopath");
        String path = "%H%M%S.mp4";
        File targetFile = new File(FileUtil.getPropertiesForName("filepath")+"/"+ DateUtil.getStringForDate()+"/"+"localvideo");
        if (!targetFile.exists()) {
            targetFile.mkdirs();
            log.info("文件创建完成" + targetFile.getPath());
        }
        String csvName = "";
        File fileCsv = new File(targetFile.getPath()+"/"+DateUtil.getStringForDate());
        if(!fileCsv.exists()){
            fileCsv.mkdirs();
        }
        if(OSinfo.isWindows()){
            csvName = fileCsv.getPath()+"\\0.csv";
        }
        if(OSinfo.isLinux()){
            csvName = fileCsv.getPath()+"/0.csv";
        }
        File[] files = fileCsv.listFiles();
        if(files.length>0){
            File lastfiles = files[files.length-1];
            int index = Integer.parseInt(lastfiles.getName().substring(0, lastfiles.getName().indexOf(".")));
            csvName = fileCsv.getPath()+"/"+(++index) +".csv";
        }
        String time = DateUtil.getSecondByMinute(Integer.parseInt(FileUtil.getPropertiesForName("time")));
        ////>ffmpeg -re -stream_loop -1 -i E:\node\1.mp4  -vcodec  copy -f segment -segment_list D:\test\20200414\localvideo\1.csv -segment_time 10 -reset_timestamps 1 -strftime 1 D:\test\20200414\localvideo\%H%M%S.mp4
        String cmd="ffmpeg "+" -re  -stream_loop -1 -i "+videopath+
                " -f segment -segment_list "+csvName+" -segment_time  "+ time
                +" -reset_timestamps 1 -strftime 1  "+targetFile.getPath() +"/"+ path;
        if(processFfmpeg==null){
            processFfmpeg = new ProcessFfmpeg();
        }
        int i = processFfmpeg.sendMessage(cmd);
        log.info("---------执行完成"+i);
    }


    public static void main(String[] args){
        RetspSaveLocalVideo rtspSaveVideo = new RetspSaveLocalVideo();
        Thread thread = new Thread(rtspSaveVideo);
        thread.start();

    }
}
