package com.techjumper.polyhome.blauncher;

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
    public static final boolean DEBUG = false;

    public static String sHost;

    static {
        sHost = DEBUG ? "http://test.poly.ourjujia.com" : "http://service.polyhome.com";
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
     * 启动页插件的PackageName
     */
    public static String sLauncherPackageName = "com.techjumper.polyhome.b.home";
//    public static String sLauncherPackageName = "com.techjumper.pluginappdemo";
//    public static String sLauncherPackageName = "com.ryg.dynamicload.sample.mainhost";

}
