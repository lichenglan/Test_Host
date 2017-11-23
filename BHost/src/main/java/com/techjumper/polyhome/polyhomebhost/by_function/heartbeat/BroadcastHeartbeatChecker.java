package com.techjumper.polyhome.polyhomebhost.by_function.heartbeat;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.f2prateek.rx.receivers.RxBroadcastReceiver;
import com.techjumper.corelib.rx.tools.RxUtils;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Iterator;

import rx.Subscription;
import rx.functions.Action1;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 2016/10/27
 * * * * * * * * * * * * * * * * * * * * * * *
 **/
public class BroadcastHeartbeatChecker implements Action1<Intent> {

    public static final String ACTION_HEARTBEAT_CHECKER = "action_heartbeat_checker";
    public static final String KEY_HEARTBEAT = "key_heartbeat";

    private SoftReference<Context> mContextReference;
    private Subscription mSubs;
    private ArrayList<IHeartbeatTask> mHeartbeatTasks = new ArrayList<>();

    private BroadcastHeartbeatChecker(Context ctx) {
        mContextReference = new SoftReference<>(ctx);
        mSubs = RxBroadcastReceiver.create(ctx, new IntentFilter(ACTION_HEARTBEAT_CHECKER))
                .subscribe(this);
    }

    public static BroadcastHeartbeatChecker registe(Context ctx) {
        return new BroadcastHeartbeatChecker(ctx);
    }

    private boolean contextIsNull() {
        return getContext() == null;
    }

    private Context getContext() {
        return mContextReference.get();
    }

    public void unregiste() {
        RxUtils.unsubscribeIfNotNull(mSubs);
        mContextReference.clear();
        removeAllTask();
    }

    @Override
    public void call(Intent intent) {
        if (intent == null
                || intent.getExtras() == null
                || intent.getExtras().get(KEY_HEARTBEAT) == null) {

            Log.d(HeartbeatConstants.TAG_HEARTBEAT, "收到了心跳广播但是没有具体内容");
            return;
        }

        String heartbeat = intent.getExtras().getString(KEY_HEARTBEAT);
        checkHeartbeat(heartbeat);
    }

    public boolean taskExist(IHeartbeatTask iHeartbeatTask) {
        for (IHeartbeatTask task : mHeartbeatTasks) {
            if (iHeartbeatTask == task)
                return true;
        }
        return false;
    }

    public void addHeartbeatTask(IHeartbeatTask iHeartbeatTask) {
        if (!taskExist(iHeartbeatTask))
            mHeartbeatTasks.add(iHeartbeatTask);
    }

    public void removeHeartbeatTask(IHeartbeatTask iHeartbeatTask) {
        Iterator<IHeartbeatTask> it = mHeartbeatTasks.iterator();
        while (it.hasNext()) {
            IHeartbeatTask next = it.next();
            if (next == iHeartbeatTask) {
                next.unsubscribe();
                it.remove();
                return;
            }
        }
    }

    public void removeAllTask() {
        Iterator<IHeartbeatTask> it = mHeartbeatTasks.iterator();
        while (it.hasNext()) {
            IHeartbeatTask next = it.next();
            next.unsubscribe();
            it.remove();
        }
    }


    private void checkHeartbeat(String heartbeat) {
        for (IHeartbeatTask task : mHeartbeatTasks) {
            task.checkHeartbeat(heartbeat);
        }
    }

}
