package com.huanyuenwei.Entuty;

import lombok.Data;

import java.io.File;
import java.io.Serializable;

@Data
public class FileUploadFile implements Serializable {
    private static final long serialVersionUID = 1L;
    private String number;//
    private File file;// 文件
    private String file_md5;// 文件名
    private int starPos;// 开始位置
    private byte[] bytes;// 文件字节数组
    private int endPos;// 结尾位置
    private long length;
}