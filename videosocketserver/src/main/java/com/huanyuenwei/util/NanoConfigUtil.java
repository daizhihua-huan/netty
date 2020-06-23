package com.huanyuenwei.util;

import com.huanyuenwei.Entuty.NanoEntity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class NanoConfigUtil {
    private static  Map<String,NanoEntity> concurrentHashMap=new ConcurrentHashMap<String,NanoEntity>();


    public static void add(String number){
        NanoEntity nanoEntity = new NanoEntity();
        nanoEntity.setNumber(number);
        concurrentHashMap.put(number,nanoEntity);
    }

    public static void add(String number,NanoEntity nanoEntity){
        concurrentHashMap.put(number,nanoEntity);
    }


    public static NanoEntity getNumberByNanoEntity(String number){

        if(concurrentHashMap.get(number)==null){
            return null;
        }
        return concurrentHashMap.get(number);
    }


    public static void remove(){


    }


}
