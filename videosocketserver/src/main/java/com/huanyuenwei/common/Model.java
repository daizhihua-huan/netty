package com.huanyuenwei.common;

import java.io.File;
import java.io.Serializable;

import com.huanyuenwei.Entuty.FileUploadFile;
import lombok.Data;
import org.msgpack.annotation.Message;
/**
 * 消息类型分离器
 * @author Administrator
 *
 */
@Message
public class Model implements Serializable{

    private static final long serialVersionUID = 1L;

    //类型
    private int type;

    //内容
    private String body;

//    private File file;// 文件
//    private String file_md5;// 文件名
//    private int starPos;// 开始位置
//    private byte[] bytes;// 文件字节数组
//    private int endPos;// 结尾位置

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }



    @Override
    public String toString() {
        return "Model [type=" + type + ", body=" + body + "]";
    }
}
