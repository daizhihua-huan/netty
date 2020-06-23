package com.huanyuenwei.exmaple.ffmpeg;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
@Slf4j
public class ProcessFfmpeg {

    private Process process;



    public int sendMessage(String cmd){
        //执行命令
        try {
            System.out.println("cmd是"+cmd);
            process = Runtime.getRuntime().exec(cmd);
            BufferedReader br= new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line;
            boolean flag = false;
            while ((line = br.readLine()) != null) {
                log.info("-------输入日志"+line);
                if(line.contains("Unknown error")){
                    flag = true;
                }
            }
            if(flag){
                return 0;
            }
            int i = process.waitFor();
           log.info("运行完成执行的-----------------------------"+i);
            return i;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void destory(){
        this.process.destroy();
    }




}
