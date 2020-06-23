package com.huanyuenwei.exmaple.ffmpeg;

import com.huanyuenwei.util.FileUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RetspLocalVideo implements Runnable {

    private  ProcessFfmpeg processFfmpeg;

    public ProcessFfmpeg getProcessFfmpeg(){
        return processFfmpeg;
    }

    public void run() {
        String videopath = FileUtil.getPropertiesForName("videopath");
        String targetrtsp = FileUtil.getPropertiesForName("targetrtsp");
        String videopathname = FileUtil.getPropertiesForName("videopathname");
        String cmd = "ffmpeg -re -stream_loop -1 -i "+videopath+" -codec copy  -acodec copy -rtsp_transport tcp -f rtsp "+targetrtsp+"/"+videopathname;
        //执行命令
        if(processFfmpeg==null){
            processFfmpeg = new ProcessFfmpeg();
        }
        int i = processFfmpeg.sendMessage(cmd);
        log.info("命令运行完成"+i);
    }
}
