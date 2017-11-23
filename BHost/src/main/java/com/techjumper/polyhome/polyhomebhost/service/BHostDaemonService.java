package com.techjumper.polyhome.polyhomebhost.service;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;

import com.techjumper.corelib.rx.tools.RxUtils;
import com.techjumper.corelib.utils.common.JLog;
import com.techjumper.polyhome.polyhomebhost.Config;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 16/7/7
 * * * * * * * * * * * * * * * * * * * * * * *
 **/
public class BHostDaemonService extends Service implements Observable.OnSubscribe<Object> {

    public static final String ACTION_BHOST_APP_HEARTBEAT = "action_bhost_app_heartbeat";
    public static final String ACTION_BHOST_APP_HEARTBEAT_RECEIVE = "action_bhost_app_heartbeat_receive";
    public static final String ACTION_START_BHOST_APP_DAEMON = "action_start_bhost_app_daemon";
    public static final String ACTION_STOP_BHOST_APP_DAEMON = "action_stop_bhost_app_daemon";
    public static final String ACTION_STOP = "action_bhost_app_daemon_stop";
    private static final int GRAY_SERVICE_ID = 901;

    private Subscription mTempSubs;
    private Subscription mTimerSubs;

    private BHostDaemonReceive mReceiver = new BHostDaemonReceive();
    private Subscriber<? super Object> mSubscriber;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter intentFilter = new IntentFilter(ACTION_BHOST_APP_HEARTBEAT_RECEIVE);
        intentFilter.addAction(ACTION_START_BHOST_APP_DAEMON);
        intentFilter.addAction(ACTION_STOP_BHOST_APP_DAEMON);
        intentFilter.addAction(ACTION_STOP);
        registerReceiver(mReceiver, intentFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent innerIntent = new Intent(this, FakeService.class);
        startService(innerIntent);
        startForeground(GRAY_SERVICE_ID, new Notification());

        timer();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        JLog.d("BHostDaemon进程退出");
        stopSelf();
        unregisterReceiver(mReceiver);
        cancelTimer();
        new Thread(() -> {
            SystemClock.sleep(1000);
            System.exit(0);
        }).start();
        super.onDestroy();
    }

    private void timer() {
        cancelTimer();
        mTimerSubs = Observable.create(this)
                .delay(3, TimeUnit.SECONDS)
                .map(o -> {
                    sendBroadcast(new Intent(ACTION_BHOST_APP_HEARTBEAT));
                    return null;
                })
                .debounce(3, TimeUnit.SECONDS)
                .subscribe(obj -> {
                    if (startHome()) {
                        cancelTimer();
                    } else {
                        RxUtils.unsubscribeIfNotNull(mTempSubs);
                        mSubscriber = null;
                        mTempSubs = Observable.timer(1, TimeUnit.SECONDS)
                                .subscribe(aLong1 -> {
                                    timer();
                                });
                    }

                });
    }

    private void cancelTimer() {
        RxUtils.unsubscribeIfNotNull(mTimerSubs);
        RxUtils.unsubscribeIfNotNull(mTempSubs);
    }

    private boolean startHome() {
        boolean b = true;
        JLog.d("守护进程打开 BHostDaemonApp");
        try {
            PackageManager pm = getPackageManager();
            Intent intent = pm.getLaunchIntentForPackage(Config.BHOST_DAEMON_APP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception e) {
            b = false;
            JLog.e("打开 BHostDaemonApp 失败: " + e);
        }
        return b;
    }

    @Override
    public void call(Subscriber<? super Object> subscriber) {
        this.mSubscriber = subscriber;
        subscriber.onNext(null);
    }

    public static class FakeService extends Service {

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            startForeground(GRAY_SERVICE_ID, new Notification());
            stopForeground(true);
            stopSelf();
            return super.onStartCommand(intent, flags, startId);
        }

        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

    }


    public class BHostDaemonReceive extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACTION_STOP_BHOST_APP_DAEMON.equalsIgnoreCase(intent.getAction())) {
                stopForeground(true);
                stopSelf();
                new Thread(() -> {
                    SystemClock.sleep(1000);
                    System.exit(0);
                }).start();
            } else if (ACTION_START_BHOST_APP_DAEMON.equalsIgnoreCase(intent.getAction())) {
                JLog.d("BHostDaemon 开始发送心跳包");
                timer();
            } else if (ACTION_BHOST_APP_HEARTBEAT_RECEIVE.equalsIgnoreCase(intent.getAction())) {
                timer();
            } else if (ACTION_STOP.equalsIgnoreCase(intent.getAction())) {
                JLog.d("BHostDaemon 停止接收心跳包");
                cancelTimer();
            }
        }
    }
}
