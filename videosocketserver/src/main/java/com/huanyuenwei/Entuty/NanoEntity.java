package com.huanyuenwei.Entuty;

import lombok.Data;

/**
 * 此实体类用来存放nano的静态变量客户端
 */
@Data
public class NanoEntity {

    //设备编号
    private String number;
    //结果
    private boolean flag;
    //客户端返回的消息
    private String data;

    //本地推放结果
    private boolean locaLflag;
    //本地数据
    private String localData;

    //视频保存结果
    private boolean localSaveFlag;

    //视频保存数据
    private String localSaveData;



    //结果
    private boolean videoFlag;
    //客户端视屏返回的结果
    private String videoData;
    //客户端返回的流
    private byte[] bytes;

    private long length;

}
