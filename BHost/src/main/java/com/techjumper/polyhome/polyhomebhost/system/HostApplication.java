package com.techjumper.polyhome.polyhomebhost.system;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.morgoo.droidplugin.PluginHelper;
import com.techjumper.lib2.others.Lib2Application;
import com.techjumper.polyhome.polyhomebhost.Config;
import com.techjumper.polyhome.polyhomebhost.net.ServiceAPI;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 16/4/19
 * * * * * * * * * * * * * * * * * * * * * * *
 **/
public class HostApplication extends Lib2Application {


    public static Handler sMainHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        PluginHelper.getInstance().applicationOnCreate(getBaseContext());
//        JPushInterface.setDebugMode(true);
//        JPushInterface.init(this);

//        new ANRWatchDog().start();
        sMainHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    protected void attachBaseContext(Context base) {
//        TurboDex.enableTurboDex();
        PluginHelper.getInstance().applicationAttachBaseContext(base);
        super.attachBaseContext(base);
    }

    @Override
    public void onCrash(Throwable ex) {
    }

    @Override
    public String[] fetchCrashFolderName() {
        return new String[]{"polyhome_b_host", "log"};
    }

    @Override
    protected int getDbVersion() {
        return Config.DEFAULT_DB_VERSION;
    }

    @Override
    protected String getDefaultBaseUrl() {
        return Config.sBaseUrl;
    }

    @Override
    protected Class getDefaultInterfaceClass() {
        return ServiceAPI.class;
    }
}
