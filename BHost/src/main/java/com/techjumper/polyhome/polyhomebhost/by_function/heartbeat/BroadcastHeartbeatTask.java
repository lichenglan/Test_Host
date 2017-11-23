package com.techjumper.polyhome.polyhomebhost.by_function.heartbeat;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.techjumper.corelib.rx.tools.RxUtils;

import java.lang.ref.SoftReference;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 2016/10/27
 * * * * * * * * * * * * * * * * * * * * * * *
 **/
public class BroadcastHeartbeatTask implements IHeartbeatTask {

    private static final long DEFAULT_INTERVAL_TIME = 3L;

    private String mHeartbeat;
    private long mCheckIntervalTime = DEFAULT_INTERVAL_TIME;
    private Subscription mSubs;
    private SoftReference<Context> mContextReference;
    private IHeartbeatTaskListener iHeartbeatTaskListener;
    private boolean mIsDied;

    public BroadcastHeartbeatTask(Context ctx, String heartbeat, IHeartbeatTaskListener iHeartbeatTaskListener) {
        this(ctx, heartbeat, DEFAULT_INTERVAL_TIME, iHeartbeatTaskListener);
    }

    public BroadcastHeartbeatTask(Context ctx, String heartbeat, long delay, IHeartbeatTaskListener iHeartbeatTaskListener) {
        if (TextUtils.isEmpty(heartbeat))
            throw new NullPointerException("heartbeat string can't be null or empty.");
        this.iHeartbeatTaskListener = iHeartbeatTaskListener;
        mHeartbeat = heartbeat;
        mCheckIntervalTime = delay;
        mContextReference = new SoftReference<>(ctx);
    }

    private Context getContext() {
        return mContextReference.get();
    }

    @Override
    public boolean checkHeartbeat(String heartbeat) {
        if (!mHeartbeat.equals(heartbeat))
            return false;

        if (iHeartbeatTaskListener != null)
            iHeartbeatTaskListener.onHeartbeatReceive(heartbeat);
        timer();
        return true;
    }

    @Override
    public void unsubscribe() {
        Log.d(HeartbeatConstants.TAG_HEARTBEAT, "unsubscribe：" + mHeartbeat);
        RxUtils.unsubscribeIfNotNull(mSubs);
        mContextReference.clear();
        mIsDied = true;
        iHeartbeatTaskListener = null;
    }

    private void timer() {
        RxUtils.unsubscribeIfNotNull(mSubs);
        mSubs = Observable
                .timer(mCheckIntervalTime, TimeUnit.SECONDS)
                .map(o -> {
                    sendBroadcast();
                    return null;
                })
                .delay(mCheckIntervalTime, TimeUnit.SECONDS)
                .subscribe(obj -> {
                    if (iHeartbeatTaskListener != null) {
                        iHeartbeatTaskListener.onHeartbeatTimeout(mHeartbeat);
                        if (!mIsDied)
                            timer();
                    }
                });
    }

    private void sendBroadcast() {
        Context context = getContext();
        if (context == null)
            return;
        context.sendBroadcast(new Intent(mHeartbeat));
    }

    public interface IHeartbeatTaskListener {
        void onHeartbeatReceive(String heartbeat);

        void onHeartbeatTimeout(String heartbeat);
    }
}
