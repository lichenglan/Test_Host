package com.techjumper.corelib.others;

import android.app.Application;

import com.jakewharton.threetenabp.AndroidThreeTen;
import com.techjumper.corelib.interfaces.IApplication;
import com.techjumper.corelib.utils.Utils;
import com.techjumper.corelib.utils.crash.CrashExceptionHandler;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 16/2/5
 * * * * * * * * * * * * * * * * * * * * * * *
 **/
public abstract class JumperApplication extends Application
        implements CrashExceptionHandler.CrashExceptionRemoteReport, IApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        Utils.init(this);
        AndroidThreeTen.init(this);

        initCrashConfig();

    }

    protected void initCrashConfig() {
        String[] crashFolders = fetchCrashFolderName();
        String folderName, crashFolderName;
        if (crashFolders == null || crashFolders.length < 2) {
            folderName = "TechJumper";
            crashFolderName = "log";
        } else {
            folderName = crashFolders[0];
            crashFolderName = crashFolders[1];
        }
        CrashExceptionHandler.getInstance(folderName, crashFolderName)
                .setDefaultHandler()
                .configRemoteReport(this);
    }
}
