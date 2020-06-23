package com.huanyuenwei.util;

import lombok.Data;

import java.util.Vector;

@Data
public class HttpRespons {

    String urlString;

    int defaultPort;

    String file;

    String host;

    String path;

    int port;

    String protocol;

    String query;

    String ref;

    String userInfo;

    String contentEncoding;

    String content;

    String contentType;

    int code;

    String message;

    String method;

    int connectTimeout;

    int readTimeout;

    Vector<String> contentCollection;
}
