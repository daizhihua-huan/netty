package com.huanyuenwei.util;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.csvreader.CsvReader;
import com.huanyuenwei.Entuty.RtspEntity;
import com.huanyuenwei.exmaple.ffmpeg.ProcessFfmpeg;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
public class FileUtil {

    //System.getProperty("java.io.tmpdir");"D:\\file\\"
    private static String tempPath = System.getProperty("java.io.tmpdir");
    // "/"
    private static String filePath = getPath("file.txt");

    private static String fileLocalPath = getPath("filelocal.txt");

    private static String fileOutPut = getPath("out.mp4");

    private static String fileOutLocalPath = getPath("outlocal.mp4");

    private static boolean flag;



    private static String getPath(String name){
        if(OSinfo.isWindows()){
            return tempPath + name;
        }else if(OSinfo.isLinux()){
            System.out.println(tempPath);
            return tempPath +"/"+ name;
        }
        return null;
    }

    /**
     * 读取filepath的文件
     *
     * @return
     */
    public static String getPath() {

        return getProperties().getProperty("filepath");
    }

    public static String getPropertiesForName(String name) {
        return getProperties().getProperty(name);
    }


    /**
     * 读取配置文件如果项目路劲下有文件就读取 没有读取默认配置
     * @return
     */
    private static Properties getProperties() {
        Properties properties = new Properties();
        String property="/file.properties";
        if(OSinfo.isLinux()){
            property = System.getProperty("user.dir")+"/"+"file.properties";
        }
        if(OSinfo.isWindows()){
            property = System.getProperty("user.dir")+"\\"+"file.properties";
        }

        try {
            InputStream inputStream = new BufferedInputStream(new FileInputStream(property));
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }


    public static String getPath(String dicerFilePath,String dicer,String stratTime, String endTime,long time) {
        flag = false;
        File file = new File(filePath);
        if(file.exists()){
            file.delete();
        }
        File fileout = new File(fileOutPut);
        if(fileout.exists()){
            fileout.delete();
        }
        boolean flagFile = false;
        try {
            flagFile = getCsv(dicerFilePath,dicer, stratTime, endTime,time);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("false"+flagFile);
        if (flagFile) {
            long statrt = System.currentTimeMillis();
            System.out.println("开始时间"+statrt);
            log.info("获取临时文件夹" + filePath);
            // String cmd = "ffmpeg -f  concat -safe 0 -i  " + fileLocalPath + " -vcodec copy -movflags faststart  -y " + fileOutLocalPath;
            String cmd = "ffmpeg -f  concat -safe 0 -i  " + filePath + "  -vcodec copy  -acodec copy -movflags faststart  -y " + fileOutPut;
            ProcessFfmpeg processFfmpeg = new ProcessFfmpeg();
            int i = processFfmpeg.sendMessage(cmd);
            if(i==0){
                long end = System.currentTimeMillis();
                System.out.println("结束时间"+end);
                System.out.println("差值"+(end-statrt));
                return fileOutPut;
            }else{
                return null;
            }
        }
        return "";
    }

    public static void delete(String type) {
        switch (type){
            case "1":
                new File(filePath).delete();
                new File(fileOutPut).delete();
                break;
            case "2":
                new File(fileLocalPath).delete();
                new File(fileOutLocalPath).delete();
                break;
        }

    }

    //, Date endTime, Date dicerStartTime, Date dicerEndTime

    private static boolean getFiles(File file, String stratTime,String endTime) {
        try {
            //如果存在
            if (file.isDirectory()) {
                /**
                 * 1、获取该目录下所有的视频ts文件
                 * 2、通过找到开始时间在文件目录中的位置 由于视频每2秒存存一个所以只需要找到开始时间对应时间 或者比开始时间早一秒的时间
                 * 3、通过开始时间和结束数据的时间范围直接每次加2秒写入文件中提高效率
                 * 4.写人零时目录
                 * 5.合成MP4
                 */
                File[] files = file.listFiles();
                Arrays.sort(files,new  Comparator<File>(){
                    public int compare(File o1, File o2) {
                        if(o1.isDirectory()&&o2.isDirectory()){
                            if(Integer.parseInt(o1.getName())<Long.parseLong(o2.getName())){
                                return -1;
                            }else if(Long.parseLong(o1.getName())>Long.parseLong(o2.getName())){
                                return 1;
                            }else{
                                return 0;
                            }
                        }else{
                            String name =  o1.getName().substring(0,o1.getName().indexOf(".")).trim();
                            String oldName = o2.getName().substring(0,o2.getName().indexOf(".")).trim();
                            if(Long.parseLong(name) < Long.parseLong(oldName)){
                                return -1;
                            }else if(Long.parseLong(name) > Long.parseLong(oldName)){
                                return 1;
                            }else {
                                return 0;
                            }
                        }
                    }
                });
                System.out.println("集合长度是"+files.length);
                for (File fileIndex : files) {
                    //如果这个文件是目录，则进行递归搜索
                    if (fileIndex.isDirectory()) {
                        String name = fileIndex.getName();
//                        if (dicerStartTime.getTime() <= dataByFileNameString.getTime() && dataByFileNameString.getTime() <= dicerEndTime.getTime()) {
//                            //说明是 符合文件的 在此文件下查找
//                            log.info("符合文件的路径是" + fileIndex.getPath());
//                            String folder = System.getProperty("java.io.tmpdir");
//                            log.info("获取零时目录" + folder);
////                            getFiles(fileIndex.getPath(), stratTime, endTime, dicerStartTime, dicerEndTime);
//                        }
                    } else {
                        //如果文件是普通
                        log.info("文件转换的名称" + fileIndex.getName());

                        int index = Integer.parseInt(fileIndex.getName().substring(0,fileIndex.getName().indexOf(".")).trim());


                        //筛选符合条件的视频流1152   1154   1153
                        /**
                         * 筛选半个小时的 可能的事件
                         * 1.开始时间等于当前创建的时间 由于间隔不能超过半个小时 直接 写如return
                         * 2.遍历的目录大于开始时间小于结束时间 判断大于开始时间多少
                         * 3.
                         *
                         */


                        File tempFile = new File(filePath);
                        if(Integer.parseInt(stratTime.trim())==index){
                            log.error("开始的时间和创建时间相等=================================");
                                    //154926-162525
                                    //160000-162000
                        }else if (Integer.parseInt(stratTime.trim()) <= index && index<= Integer.parseInt(endTime.trim())) {
                            //说明是 夸时间端了
                            log.error("找到符合的视频流"+fileIndex.getName());


                            FileOutputStream fos = getFileInputStreamByFile(tempFile);
                            if (fileIndex.length() == 0) {
                                log.info("当程序异常退出时候文件没有保存上视频");
                                fileIndex.delete();
                                continue;
                            }
                            //使用JavaNio技术写流 提高效率
                            String filename = "file  " + "'" + fileIndex.getPath() + "'" +"\r\n";
                            getWriteFileNio(fos,filename);
                            /*ByteBuffer bbf = ByteBuffer.wrap(filename.getBytes());
                            FileChannel fc = fos.getChannel();
                            bbf.put(filename.getBytes()) ;
                            bbf.flip();
                            fc.write(bbf) ;
                            fc.close();*/
                            flag = true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }


    //通过查询csv文件获取文件名
    public static boolean getCsv(String path,String dicStart,String startTime,String endTime,long time) throws IOException {
        List<RtspEntity> rtspEntities = readCSV(path,dicStart);
        if(rtspEntities==null){
            return false;
        }
        File tempFile = new File(filePath);
        log.debug("获得临时路径是"+tempFile.getPath());
        for (int i = 0; i < rtspEntities.size(); i++) {
            String name = rtspEntities.get(i).getFileNmae().substring(0,rtspEntities.get(i).getFileNmae().indexOf("."));
            String nextNmae = null;
            String lastName = null;
            if(i+1<rtspEntities.size()-1){
                nextNmae = rtspEntities.get(i+1).getFileNmae().substring(0,rtspEntities.get(i+1).getFileNmae().indexOf("."));
            }

            if(i+2<rtspEntities.size()-1){
                lastName = rtspEntities.get(i+2).getFileNmae().substring(0,rtspEntities.get(i+2).getFileNmae().indexOf("."));
            }
            String linesPath = "\\";
            if(OSinfo.isLinux()){
                linesPath = "/";
            }

            if(Integer.parseInt(startTime)==Integer.parseInt(name)){
                log.info("--------------开始的时间等于创建的时间");
                FileOutputStream fos = getFileInputStreamByFile(tempFile);
                //使用JavaNio技术写流 提高效率
                String filename = "file  " + "'" + path + linesPath + rtspEntities.get(i).getFileNmae() + "'" +"\r\n"
                        +"inpoint 00:00:00.000" +"\r\n"
                        +"outpoint "+getGapTime(time);
                getWriteFileNio(fos,filename);
                return true;
                //开始的时间早于生成时间 结束的时间大于创建时间
            }else if(Integer.parseInt(startTime)<Integer.parseInt(name)&&
                    Integer.parseInt(endTime)>=Integer.parseInt(name)){
                log.info("--------------要查看的流在被这半小时包含");
                FileOutputStream fos = getFileInputStreamByFile(tempFile);
                long millisecondByName = DateUtil.getMillisecondByName(endTime, name);
                String value = "file  " + "'" + path+ linesPath +rtspEntities.get(i).getFileNmae() + "'" +"\r\n"
                        +"inpoint 00:00:00.000 " +"\r\n"
                        +"outpoint "+getGapTime(millisecondByName)  +"\r\n";
                getWriteFileNio(fos,value);
                return true;
                //开始时间晚于生成时间 结束的时间小于创建时间的半个小时
            }else if(nextNmae!=null&&Integer.parseInt(startTime)>
                    Integer.parseInt(name)&&
                    Integer.parseInt(endTime)<
                    Integer.parseInt(nextNmae)){
                log.info("--------------筛选的时间在2个流的时间");
                FileOutputStream fos = getFileInputStreamByFile(tempFile);
                long millisecondByName = DateUtil.getMillisecondByName(startTime, name);
                //此视频的玩结时间和结束时间
//                long milliendByName = DateUtil.getMillisecondByName(nextNmae, endTime);
                String value = "file  " + "'" + path+ linesPath +rtspEntities.get(i).getFileNmae() + "'" +"\r\n"
                        +"inpoint " + getGapTime(millisecondByName) +"\r\n"
                        +"outpoint "+ getGapTime(millisecondByName+time)+"\r\n";
                getWriteFileNio(fos,value);
                return true;
                //在两个流的之间
            }else if(nextNmae!=null
                    &&lastName!=null
                    &&Integer.parseInt(startTime)>Integer.parseInt(name)
                    &&Integer.parseInt(startTime)<Integer.parseInt(nextNmae)
                    &&Integer.parseInt(endTime)>Integer.parseInt(nextNmae)
                    &&Integer.parseInt(endTime)<Integer.parseInt(lastName)
            ){
                /**
                 * 筛选的时间跨了2个视频流
                 */
                FileOutputStream fos = getFileInputStreamByFile(tempFile);
                long millisecondStart = DateUtil.getMillisecondByName(startTime, name);
                String value = "file  " + "'" + path+ linesPath +rtspEntities.get(i).getFileNmae() + "' " +"\r\n"
                        +"inpoint " + getGapTime(millisecondStart) +"\r\n";
                getWriteFileNio(fos,value);
                long millisecondout = DateUtil.getMillisecondByName(nextNmae, endTime);
                String valueof = "file  " + "'" + path+ linesPath +nextNmae+".avi" + "'" +"\r\n"
                        +"inpoint 00:00:00.000 " +"\r\n"
                        +"outpoint "+getGapTime(millisecondout);
                fos = getFileInputStreamByFile(tempFile);
                getWriteFileNio(fos,valueof);
                return true;
            }

        }

        return false;
    }

    public static void getWriteFileNio(FileOutputStream fos, String value) throws IOException {
        ByteBuffer bbf = ByteBuffer.wrap(value.getBytes());
        FileChannel fc = fos.getChannel();
        bbf.put(value.getBytes()) ;
        bbf.flip();
        fc.write(bbf) ;
        fc.close();
    }


    public static FileOutputStream getFileInputStreamByFile(File tempFile) throws IOException {
        FileOutputStream fos = null;
        if (!tempFile.exists()) {
            tempFile.createNewFile();//如果文件不存在，就创建该文件
            fos = new FileOutputStream(tempFile);//首次写入获取
        } else {
            //如果文件已存在，那么就在文件末尾追加写入
            fos = new FileOutputStream(tempFile, true);//这里构造方法多了一个参数true,表示在文件末尾追加写入
        }
        return fos;
    }

    /**
     * 由于生成的录像视频是
     * 每天新建一个目录名称是当前的日期 在每个日期目录下存着
     * 对应的日期录像前端规定 只能回看一个小时的并不会超过一天所以只需要
     * 判断这个日期的目录是不是存在
     * 如果存在那么就去这个目录下找 如果不存在说明没有保存直接返回给服务器响应
     * @param startTimeName
     * @return
     */
    public static File isFileExiets(String startTimeName){
        //getpath 获取录像的存放位置
        File file = new File(getPath()+"//"+startTimeName);
        return file;
    }

    public static String getDicerPath(String startTimeName){
        if(OSinfo.isLinux()){
            return  getPath()+"//"+startTimeName;
        }
        return getPath()+"\\"+startTimeName;
    }


    public static void deleteFile(File file){
        //判断文件不为null或文件目录存在
        if (file == null || !file.exists()){

            System.out.println("文件删除失败,请检查文件路径是否正确");
            return;
        }
        //取得这个目录下的所有子文件对象
        File[] files = file.listFiles();
        //遍历该目录下的文件对象
        for (File f: files){
            //打印文件名
            String name = file.getName();
            System.out.println(name);
            //判断子目录是否存在子目录,如果是文件则删除
            if (f.isDirectory()){
                deleteFile(f);
            }else {
                f.delete();
            }
        }
        //删除空文件夹  for循环已经把上一层节点的目录清空。
        file.delete();
    }


    /**
     * 通过目录路径和
     * @param path
     * @param dicStart
     * @return
     */
    public static List<RtspEntity> readCSV(String path,String dicStart) {
        try {
            ArrayList<RtspEntity> list = new ArrayList<RtspEntity>();

            File file = new File(path+"/"+dicStart);
            File[] files = file.listFiles();
            Arrays.sort(files, new Comparator<File>() {
                public int compare(File o1, File o2) {
                    String name =  o1.getName().substring(0,o1.getName().indexOf(".")).trim();
                    String oldName = o2.getName().substring(0,o2.getName().indexOf(".")).trim();
                    if(Long.parseLong(name) < Long.parseLong(oldName)){
                        return -1;
                    }else if(Long.parseLong(name) > Long.parseLong(oldName)){
                        return 1;
                    }else {
                        return 0;
                    }
                }
            });
            for (File filecsv : files) {
                // 创建CSV读对象 例如:CsvReader(文件路径，分隔符，编码格式);
                CsvReader reader = new CsvReader(filecsv.getPath(), ',', Charset.forName("UTF-8"));
                // 跳过表头 如果需要表头的话，这句可以忽略
//            reader.readHeaders();
                // 逐行读入除表头的数据
                // 用来保存数据
                ArrayList<String[]> csvFileList = new ArrayList<String[]>();
                while (reader.readRecord()) {
                    System.out.println(reader.getRawRecord());
                    csvFileList.add(reader.getValues());
                }
                reader.close();
                // 遍历读取的CSV文件
                for (int row = 0; row < csvFileList.size(); row++) {
                    RtspEntity rtspEntity = new RtspEntity();
                    int i =0;
                    for (String value : csvFileList.get(row)) {
                        switch (i){
                            case 0:
                                rtspEntity.setFileNmae(value);
                                break;
                            case 1:
                                rtspEntity.setStartTime(value);
                                break;
                            case 2:
                                rtspEntity.setEndTime(value);
                                break;
                        }
                        i++;
                    }
                    list.add(rtspEntity);
                }
            }

            return list;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getGapTime(long time){
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
        String hms = formatter.format(time);
        return hms;
    }





    public static File getLocalVideoPath(String time,String startTime,String endTime){
        String videopath = getPropertiesForName("videopath");
        String videoName = "";
        if(OSinfo.isLinux()){
            videoName=videopath.substring(videopath.lastIndexOf("/"));
        }
        if(OSinfo.isWindows()){
            videoName = videopath.substring(videopath.lastIndexOf("\\"));
        }

        File file = new File(FileUtil.getPropertiesForName("filepath")+"/"+DateUtil.getStringForDate()+"/"+"localvideo"+"/"+videoName);
        File tempFile = new File(fileLocalPath);
        if(file.exists()){
            String inpoint;
            if(Integer.parseInt(startTime)<=Integer.parseInt(time)){
                inpoint = "00:00:00.000 ";
            }else {
                long millisecondByName = DateUtil.getMillisecondByName(startTime, time);
                inpoint = getGapTime(millisecondByName);
            }
            long millisecondByName = DateUtil.getMillisecondByName(endTime, time);
            String outint = getGapTime(millisecondByName);
            try {
                String value = "file  " + "'" + file.getPath() + "'" +"\r\n"
                        +"inpoint "+inpoint +"\r\n"
                        +"outpoint "+outint+"\r\n";
                FileOutputStream fos = getFileInputStreamByFile(tempFile);
                getWriteFileNio(fos,value);
                //movflags faststart -r 20 -b:v 1024k
                String cmd = "ffmpeg -f  concat -safe 0 -i  " + fileLocalPath + " -c copy   -movflags faststart  -y " + fileOutLocalPath;
                ProcessFfmpeg processFfmpeg = new ProcessFfmpeg();
                if( processFfmpeg.sendMessage(cmd)==1){
                    return null;
                }

                return new File(fileOutLocalPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        return null;
    }


}
