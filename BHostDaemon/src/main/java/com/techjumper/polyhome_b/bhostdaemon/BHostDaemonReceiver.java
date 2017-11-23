package com.techjumper.polyhome_b.bhostdaemon;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.techjumper.corelib.rx.tools.RxBus;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 16/8/3
 * * * * * * * * * * * * * * * * * * * * * * *
 **/
public class BHostDaemonReceiver extends BroadcastReceiver {

    public static final String ACTION_UPDATE_BHOST = "action_update_bhost";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ACTION_UPDATE_BHOST.equalsIgnoreCase(intent.getAction())) {
            RxBus.INSTANCE.send(ACTION_UPDATE_BHOST);
        }
    }
}
