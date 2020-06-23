package com.huanyuenwei.util;

import io.netty.channel.ChannelHandlerContext;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class NettConfigUtil {

    private static ConcurrentMap<String, ChannelHandlerContext> map = new ConcurrentHashMap<String, ChannelHandlerContext>();

    public static void add(String key,ChannelHandlerContext channelHandlerContext){
        map.put(key,channelHandlerContext);
    }

    public static void remove(ChannelHandlerContext ctx){
        for (String key :map.keySet()){
            if(map.get(key)==ctx){
                map.remove(key);
            }
        }

    }

    public static ChannelHandlerContext getKey(String key){
        if(map.get(key)==null){
            return null;
        }
        return map.get(key);
    }

    public static ConcurrentMap<String, ChannelHandlerContext> getAll(){
        return map;
    }
}
