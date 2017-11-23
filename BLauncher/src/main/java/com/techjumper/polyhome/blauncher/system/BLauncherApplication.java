package com.techjumper.polyhome.blauncher.system;

import android.content.Context;
import android.content.Intent;

import com.techjumper.lib2.others.Lib2Application;
import com.techjumper.polyhome.blauncher.Config;
import com.techjumper.polyhome.blauncher.net.ServiceAPI;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 16/4/19
 * * * * * * * * * * * * * * * * * * * * * * *
 **/
public class BLauncherApplication extends Lib2Application {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    @Override
    public void onCrash(Throwable ex) {
        sendBroadcast(new Intent("action_start_host_daemon"));
    }

    @Override
    public String[] fetchCrashFolderName() {
        return new String[]{"polyhome_b_launcher", "log"};
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
