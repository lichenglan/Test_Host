package com.techjumper.polyhome.blauncher.mvp.p.activity;

import android.content.Intent;
import android.os.Bundle;

import com.techjumper.corelib.utils.Utils;
import com.techjumper.corelib.utils.common.JLog;
import com.techjumper.polyhome.blauncher.mvp.v.activity.MainActivity;
import com.techjumper.polyhome.blauncher.service.BLauncherService;
import com.techjumper.polyhome.blauncher.service.PluginCommunicateService;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 16/4/19
 * * * * * * * * * * * * * * * * * * * * * * *
 **/
public class MainActivityPresenter extends AppBaseActivityPresenter<MainActivity> {
    @Override
    public void initData(Bundle savedInstanceState) {
        getView().startService(new Intent(getView(), BLauncherService.class));
        getView().startService(new Intent(getView(), PluginCommunicateService.class));

        Utils.mainHandler.postDelayed(() -> {
            getView().finish();
            JLog.d("启动 BLauncherService 和 PluginCommunicateService, 并关掉页面");
        }, 1000);
    }




    @Override
    public void onViewInited(Bundle savedInstanceState) {
    }

}
