package com.techjumper.polyhome.polyhomebhost.by_function.heartbeat;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.f2prateek.rx.receivers.RxBroadcastReceiver;
import com.techjumper.corelib.rx.tools.RxUtils;

import java.lang.ref.SoftReference;

import rx.Subscription;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 2016/10/27
 * * * * * * * * * * * * * * * * * * * * * * *
 **/
public class BroadcastHeartbeatClient {

    public static final String ACTION_HEARTBEAT_CHECKER = "action_heartbeat_checker";
    public static final String KEY_HEARTBEAT = "key_heartbeat";

    private SoftReference<Context> mContextReference;
    private Subscription mSubs;

    public BroadcastHeartbeatClient(Context ctx) {
        mContextReference = new SoftReference<>(ctx);
    }

    private Context getContext() {
        return mContextReference.get();
    }

    public void startHeartbeat(String heartbeat) {
        if (getContext() == null)
            return;
        RxUtils.unsubscribeIfNotNull(mSubs);
        sendBroadcastToService(heartbeat);
        mSubs = RxBroadcastReceiver.create(getContext(), new IntentFilter(heartbeat))
                .subscribe(intent -> {
                    sendBroadcastToService(heartbeat);
                });
    }

    private void sendBroadcastToService(String heartbeat) {
        if (getContext() == null)
            return;
        Intent intent = new Intent(ACTION_HEARTBEAT_CHECKER);
        intent.putExtra(KEY_HEARTBEAT, heartbeat);
        getContext().sendBroadcast(intent);
    }

    public void stop() {
        mContextReference.clear();
        RxUtils.unsubscribeIfNotNull(mSubs);
    }

}
