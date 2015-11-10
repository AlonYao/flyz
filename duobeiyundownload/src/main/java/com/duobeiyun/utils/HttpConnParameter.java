package com.duobeiyun.utils;

public enum HttpConnParameter {
    POST("GET"),
    ACCEPT("Accept", "image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*"),
    ACCEPT_LANGUAGE("Accept-Language", "zh-CN"),
    ACCEPT_RANGE("Accept-Ranges", "bytes"),
    CHARSET("Charset", "UTF-8"),
    CONNECT_TIMEOUT("5000"),
    KEEP_CONNECT("Connection", "Keep-Alive"),
    LOCATION("location"),
    REFERER("referer");

    public String header;
    public String content;

    private HttpConnParameter(String header, String content) {
        this.header = header;
        this.content = content;
    }

    private HttpConnParameter(String content) {
        this.content = content;
    }
}
