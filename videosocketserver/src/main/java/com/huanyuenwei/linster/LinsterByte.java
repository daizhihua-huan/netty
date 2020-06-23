package com.huanyuenwei.linster;

import java.io.File;
import java.io.RandomAccessFile;

public interface LinsterByte {

    void sendByte(byte[] bytes,long length);

    void sendFile(RandomAccessFile file,long length);
}
