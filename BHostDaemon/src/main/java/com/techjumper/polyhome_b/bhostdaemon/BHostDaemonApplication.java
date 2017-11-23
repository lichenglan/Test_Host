package com.techjumper.polyhome_b.bhostdaemon;

import com.techjumper.lib2.others.Lib2Application;
import com.techjumper.polyhome_b.bhostdaemon.net.ServiceAPI;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 16/8/3
 * * * * * * * * * * * * * * * * * * * * * * *
 **/
public class BHostDaemonApplication extends Lib2Application {
    @Override
    protected int getDbVersion() {
        return 1;
    }

    @Override
    protected String getDefaultBaseUrl() {
        return Config.sBaseUrl;
    }

    @Override
    protected Class getDefaultInterfaceClass() {
        return ServiceAPI.class;
    }

    @Override
    public void onCrash(Throwable ex) {

    }

    @Override
    public String[] fetchCrashFolderName() {
        return new String[]{"BHostDaemon", "log"};
    }
}
