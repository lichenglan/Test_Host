package com.techjumper.lib2.others;

import com.techjumper.corelib.others.JumperApplication;
import com.techjumper.lib2.utils.RetrofitHelper;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 16/2/15
 * * * * * * * * * * * * * * * * * * * * * * *
 **/
public abstract class Lib2Application extends JumperApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        Config.sDefaultBaseUrl = getDefaultBaseUrl();
        RetrofitHelper.sDefaultInterface = getDefaultInterfaceClass();

    }

    protected abstract int getDbVersion();

    protected abstract String getDefaultBaseUrl();

    protected abstract Class getDefaultInterfaceClass();
}
