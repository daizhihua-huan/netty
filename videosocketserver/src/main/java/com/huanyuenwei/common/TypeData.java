package com.huanyuenwei.common;

/**
 * 配置项
 * @author Administrator
 *
 */
public interface TypeData {

    //客户端连接
    byte CLINE = 0;

    //心跳包
    byte PING = 1;

    byte PONG = 2;
    //内容
    byte CUSTOMER = 3;

    //视屏回看
    byte FILENUMBER = 4;

    byte LIVENUMBER = 5;

    byte LIVELOCAL = 6;
}
