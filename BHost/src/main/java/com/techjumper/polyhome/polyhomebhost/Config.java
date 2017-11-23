package com.techjumper.polyhome.polyhomebhost;

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
    public static String sStatic;

    static {
        sHost = DEBUG ? "http://test.poly.ourjujia.com" : "http://api.polyhome.com";
        sStatic = sHost;
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
     * BLauncher包名
     */
    public static String sLauncherPackageName = "com.techjumper.polyhome.blauncher";
    /**
     * BLauncher的核心Service
     */
    public static String sLauncherServiceName = "com.techjumper.polyhome.blauncher.service.BLauncherService";
    /**
     * 首页的包名
     */
    public static String sLauncherServiceLauncher = "com.techjumper.polyhome.b.home";

    /**
     * BHostDaemon App 包名
     */
    public static final String BHOST_DAEMON_APP = "com.techjumper.polyhome_b.bhostdaemon";
//    public static String sLauncherServiceLauncher = "com.techjumper.pluginappdemo";

}
