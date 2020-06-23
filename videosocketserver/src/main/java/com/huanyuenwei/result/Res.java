package com.huanyuenwei.result;

import lombok.Data;

/**
 * Created by wolf on 2020-03-28
 */
@Data
public class  Res<T> {

    private int code = 0;
    private T data;
    private String msg = "";
    private String error = "";

    public static Res fail = new Res().code(-1);

    public static Res build() {
        return new Res();
    }

    public static <T> Res build(T data){
        return new Res().data(data);
    }

    public Res code(int code) {
        this.code = code;
        return this;
    }

    public Res data(T data) {
        this.data = data;
        return this;
    }

    public Res msg(String msg) {
        this.msg = msg;
        return this;
    }

    public Res error(String error) {
        this.error = error;
        return this;
    }
    //省略getter
}

