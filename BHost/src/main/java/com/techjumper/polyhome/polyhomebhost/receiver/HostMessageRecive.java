package com.techjumper.polyhome.polyhomebhost.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 16/7/21
 * * * * * * * * * * * * * * * * * * * * * * *
 **/
public class HostMessageRecive extends BroadcastReceiver {

    public static final String ACTION_BHOST_LOCK_SCREEN = "action_bhost_lock_screen";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ACTION_BHOST_LOCK_SCREEN.equalsIgnoreCase(intent.getAction())) {
//            PolicyUtil.lockDirectly();
//            JLog.d("BHost关闭屏幕");
        }
    }
}
