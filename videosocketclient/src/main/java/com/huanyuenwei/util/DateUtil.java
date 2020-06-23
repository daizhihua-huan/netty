package com.huanyuenwei.util;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日期操作工具类
 */
@Slf4j
public class DateUtil {

    private static String data;

    /**
     * 获得每次的生成时间
     * @return
     */
    public static String getData() {
        return data;
    }

    public static String getNoewData(){
        SimpleDateFormat smf = new SimpleDateFormat("yyyyMMdd");
        String nowData = smf.format(new Date());
        return nowData;
    }

    public static Date getDateByString(String data){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd HHmmss");
        return getData(data,simpleDateFormat);
    }


    public static String getStringForDate(){
        Date date = new Date();
        SimpleDateFormat smf = new SimpleDateFormat("yyyyMMdd");
        String format = smf.format(date);
        data = format;
        return format;
    }



    public static boolean getResultByTime(String end,String start){
        long time = getDateByString(end).getTime() - getDateByString(start).getTime();
        log.info("-----------------开始时间和结束时间的插值"+time);
        long proerTime = Long.parseLong(DateUtil.
                getSecondByMinute(FileUtil.getPropertiesForName("time")));
        log.info("-----------------设置的时间范围"+proerTime);
        if(time>proerTime){
            return true;
        }
        return false;

    }


    /**
     * 获取日期
     * @param firtsName
     * @param lastName
     * @return
     */
    public static long getMillisecondByName(String firtsName,String lastName){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HHmmss");
        long time = Math.abs(getData(firtsName,simpleDateFormat).getTime()-getData(lastName,simpleDateFormat).getTime());
        System.out.println("yime是"+time);
        return time;
    }


    public static long getLongByTime(String time){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HHmmss");
        long lengthLong = 0;
        try {
             lengthLong = simpleDateFormat.parse(time).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println("yime是"+time);
        return lengthLong;
    }

    private static Date getData(String data,SimpleDateFormat simpleDateFormat){
        Date parse = null;
        try {
            parse = simpleDateFormat.parse(data);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return parse;
    }


    public static String getSecondByMinute(String time){
        int minute = Integer.parseInt(time);

        int second = minute*60*1000;
        return String.valueOf(second);
    }

    public static String getSecondByMinute(int time){
        return String.valueOf(time*60);
    }


}
