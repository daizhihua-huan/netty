package com.huanyuenwei.exmaple.ffmpeg;

import com.huanyuenwei.Entuty.TimeType;
import com.huanyuenwei.util.DateUtil;
import com.huanyuenwei.util.FileUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import java.io.File;

import java.io.File;

public class RtspSaveVideo2 extends RtspSaveVideo implements Runnable{

    private String path;

    private String time;

    private ProcessFfmpeg processFfmpeg;

    private static int index;
    @Override
    public void run() {

            save();

    }


    public synchronized void save(){


        String soucertsp=FileUtil.getPropertiesForName("soucertsp2");
        String path = "%H%M%S.avi";
        //1800
//        TimeType time1 = TimeType.valueOf(FileUtil.getPropertiesForName("time"));

        String time = DateUtil.getSecondByMinute(Integer.parseInt(FileUtil.getPropertiesForName("time")));
        File file = new File(FileUtil.getPath() +"/"+ DateUtil.getStringForDate());
        if(!file.exists()){  //若文件不存在则创建文件
            //
            file.mkdirs();
        }




        if(!StringUtils.isEmpty(this.path)){
            path = this.path;
        }
        if(!StringUtils.isEmpty(this.time)){
            time = this.time;
        }


        File fileCsv = new File(FileUtil.getPath() +"/"+ DateUtil.getStringForDate()+"/"+DateUtil.getStringForDate());
        //做关联，获取文件路径，路径为filepath+当前时间yyyyMMdd（两次）
        if(!fileCsv.exists()){//若文件不存在则创建文件
            fileCsv.mkdirs();
        }
        String filePath = file.getPath();
        File firstfile=new File(filePath);
        File[] firstFiles = firstfile.listFiles();
        String rtspFileName;

       if(firstFiles.length<=1){

           rtspFileName="rtsp_1";

       }else{

           File rtsplistfiles = firstFiles[firstFiles.length-1];

           int parseInt = Integer.parseInt(rtsplistfiles.getName().substring(rtsplistfiles.getName().length() - 1));

           rtspFileName="rtsp_"+(++parseInt);

       }

        File rtspfilesave =new File(file.getPath()+"/"+rtspFileName);
        if(!rtspfilesave.exists()){//若文件不存在则创建文件
            rtspfilesave.mkdirs();
        }
        File[] files = fileCsv.listFiles();   //以File对象的形式返回当前路径下的所有文件和文件夹名称
        String csvPath = fileCsv.getPath()+"/"+"0.csv";// 获取File对象中封装的路径 也就是上述值（拼装csvPath）
        if(files.length>0){
            File lastfiles = files[files.length-1];  //拿到最新的文件

            int index = Integer.parseInt(lastfiles.getName().substring(0, lastfiles.getName().indexOf(".")));
            csvPath = fileCsv.getPath()+"/"+(++index) +".csv";//0,1,2,3，.....

        }

        //ffmpeg -rtsp_transport tcp -i rtsp://... -vcodec copy -acodec copy /tmp/1080p.flv
        String size = FileUtil.getPropertiesForName("rate");
        String qscale = FileUtil.getPropertiesForName("qscale");
        String stimeout = FileUtil.getPropertiesForName("stimeout");//超时时间
        //ffmpeg -rtsp_transport tcp -reorder_queue_size 8000 -vsync drop -i rtsp://admin:1q2w3e4r@192.168.0.64 -map 0 -r 30 -vcodec copy -acodec
        // copy -f segment -segment_time 30 -reset_timestamps 1 -strftime 1 -movflags faststart D:\test\%H%M%S.avi
        String cmd="D:\\ffmpeg\\ffmpeg-20200612-38737b3-win64-static\\bin\\ffmpeg -stimeout "+stimeout+" -rtsp_transport tcp -reorder_queue_size 8000 -vsync drop  -i "
                + soucertsp+ " -r "+size+" -vcodec copy -acodec copy -f segment -segment_list "+csvPath+" -segment_time  "+ time
                +" -reset_timestamps 1 -strftime 1  -qscale "+qscale+" "+rtspfilesave.getPath() +"/"+ path;

        if(processFfmpeg==null){
            processFfmpeg = new ProcessFfmpeg();
        }

//
        int i = processFfmpeg.sendMessage(cmd);//执行命令


    }
}
