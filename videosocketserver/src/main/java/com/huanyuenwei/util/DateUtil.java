package com.huanyuenwei.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日期格式换工具类
 */
public class DateUtil {

    public static Date getByStringForDate(String data){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd HHmmss");
        try {
            Date parse = simpleDateFormat.parse(data);
            return parse;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static Date getByStringForTimeDate(String time){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HHmmss");
        try {
            Date parse = simpleDateFormat.parse(time);
            return parse;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getByTadyForDate(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date= new Date();
        String format = simpleDateFormat.format(date);
        return format;
    }

}
