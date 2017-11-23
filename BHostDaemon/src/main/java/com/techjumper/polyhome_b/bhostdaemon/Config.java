package com.techjumper.polyhome_b.bhostdaemon;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 16/2/23
 * * * * * * * * * * * * * * * * * * * * * * *
 **/
public class Config {
    /**
     * 是否是调试环境
     */
    public static final boolean DEBUG = true;

    public static String sHost;

    static {
        sHost = DEBUG ? "http://poly.techjumper.com" : "http://api.ourjujia.com";
    }

    /**
     * 默认接口地址
     */
    public static String sBaseUrl = sHost + "/api/v1b/";

    /**
     * 默认数据库的版本
     */
    public static final int DEFAULT_DB_VERSION = 1;

    /**
     * BHost的包名
     */
    public static final String BHOST_PACKAGE = "com.techjumper.polyhome.polyhomebhost";

}
