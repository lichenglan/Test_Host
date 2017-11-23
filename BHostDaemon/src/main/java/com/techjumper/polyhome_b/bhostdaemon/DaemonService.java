package com.techjumper.polyhome_b.bhostdaemon;

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

import com.techjumper.corelib.rx.tools.RxBus;
import com.techjumper.corelib.rx.tools.RxUtils;
import com.techjumper.corelib.utils.common.JLog;
import com.techjumper.polyhome_b.bhostdaemon.tools.SilentInstaller;
import com.techjumper.polyhome_b.bhostdaemon.tools.UpdateChecker;
import com.techjumper.polyhome_b.bhostdaemon.tools.UpdateExecutor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.schedulers.Schedulers;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 16/7/6
 * * * * * * * * * * * * * * * * * * * * * * *
 **/
public class DaemonService extends Service implements Observable.OnSubscribe<Object> {

    public static final String ACTION_HOST_HEARTBEAT = "action_host_heartbeat";
    public static final String ACTION_HOST_HEARTBEAT_RECEIVE = "action_host_heartbeat_receive";
    public static final String ACTION_STOP_HOST_DAEMON = "action_stop_host_daemont";


    public static final String ACTION_BHOST_APP_HEARTBEAT = "action_bhost_app_heartbeat";
    public static final String ACTION_BHOST_APP_HEARTBEAT_RECEIVE = "action_bhost_app_heartbeat_receive";

    private static final int GRAY_SERVICE_ID = 910;

    private Subscription mTempSubs;
    private Subscription mTimerSubs;

    private BHostReceiver mReceiver = new BHostReceiver();
    private Subscription mSubs;
    private Subscription mDelaySubs;
    private NumberProcessController mNumberProcessC = new NumberProcessController();
    private Subscriber<? super Object> mSubscriber;
    private SilentInstaller mSilentInstaller = new SilentInstaller();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter intentFilter = new IntentFilter(ACTION_HOST_HEARTBEAT_RECEIVE);
        intentFilter.addAction(ACTION_STOP_HOST_DAEMON);
        intentFilter.addAction(ACTION_BHOST_APP_HEARTBEAT);
        registerReceiver(mReceiver, intentFilter);

        mSubs = RxBus.INSTANCE.asObservable()
                .subscribe(new Subscriber<Object>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Object o) {
                        if (!(o instanceof String))
                            return;
                        if (BHostDaemonReceiver.ACTION_UPDATE_BHOST.equalsIgnoreCase(o.toString())) {
                            if (mNumberProcessC.isIncreasing()) {
                                return;
                            }
                            systemTimer();
                            UpdateChecker.getInstance().execute(new UpdateChecker.IUpdateChecker() {
                                @Override
                                public void onDonwloadUrlsReceive(ArrayList<String> downloadUrls) {
                                    if (mNumberProcessC.isIncreasing()) {
                                        return;
                                    }
                                    mNumberProcessC.init(downloadUrls.size());
                                    UpdateExecutor.getInstance().execute(downloadUrls, DaemonService.this::installApk);
                                }

                                @Override
                                public void onAppAlreadyLatest() {
                                    JLog.d("没有系统app可以升级");
                                }
                            });
                        }
                    }
                });
    }

    private void systemTimer() {
        RxUtils.unsubscribeIfNotNull(mDelaySubs);
        mDelaySubs = Observable.timer(15, TimeUnit.MINUTES)
                .subscribe(aLong -> {
                    JLog.d("长时间无反应, 重启");
                    mNumberProcessC.reset();
                    reboot();
                });
        JLog.d("重启无操作计时");
    }

    private void reboot() {
        JLog.d("重启");
        try {
            FileOutputStream out = new FileOutputStream("/var/etc/watchdog");
            String s = "1";
            out.write(s.getBytes());
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void installApk(String donwloadedPath) {
        File file = new File(donwloadedPath);
        JLog.d("文件存在否:" + file.exists() + "; path=" + file.getPath());
        Intent it = new Intent("com.dnake.broadcast");
        it.putExtra("event", "com.dnake.talk.apk.upgrade");
        it.putExtra("url", donwloadedPath);
        it.putExtra("name", "PolyHome");
//        it.putExtra("mode", 1);
        sendBroadcast(it);
        systemTimer();
        mNumberProcessC.increase();

        Observable.timer(60, TimeUnit.SECONDS)
                .map(aLong -> {
                    if (file.exists()) {
                        file.delete();
                    }
                    return aLong;
                })
                .filter(aLong1 -> mNumberProcessC.isReachMaxSize())
                .subscribeOn(Schedulers.io())
                .subscribe(aLong -> {
                    JLog.d("安装完毕, 重启");
                    reboot();
                });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        JLog.d("启动了 DaemonService");
        Intent innerIntent = new Intent(this, FakeService.class);
        startService(innerIntent);
        startForeground(GRAY_SERVICE_ID, new Notification());

        timer();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mReceiver);
        cancelTimer();
        RxUtils.unsubscribeIfNotNull(mSubs);
        super.onDestroy();
    }

    private void timer() {
        cancelTimer();
        mTimerSubs = Observable.create(this)
                .delay(5, TimeUnit.SECONDS)
                .map(o -> {
                    sendBroadcast(new Intent(ACTION_HOST_HEARTBEAT));
                    return null;
                })
                .debounce(5, TimeUnit.SECONDS)
                .subscribe(obj -> {
                    startBHost();

                    RxUtils.unsubscribeIfNotNull(mTempSubs);
                    mSubscriber = null;
                    mTempSubs = Observable.timer(1, TimeUnit.SECONDS)
                            .subscribe(aLong1 -> {
                                timer();
                            });

                });
    }

    private void cancelTimer() {
        RxUtils.unsubscribeIfNotNull(mTimerSubs);
        RxUtils.unsubscribeIfNotNull(mTempSubs);
    }

    private void startBHost() {
        JLog.d("守护进程打开 BHost");
        try {
            PackageManager pm = getPackageManager();
            Intent intent = pm.getLaunchIntentForPackage(Config.BHOST_PACKAGE);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception e) {
            JLog.e("打开 BHost 失败: " + e);
        }
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

    private class NumberProcessController {
        private int mSize;
        private int mProcess;

        public void init(int size) {
            mSize = size;
            mProcess = 0;
        }

        public void increase() {

            if (++mProcess > mSize) {
                mProcess = mSize;
            }
        }

        public boolean isIncreasing() {
            return mSize != 0;
        }

        public void reset() {
            mSize = mProcess = 0;
        }

        public String getFormatString() {
            return "(" + mProcess + "/" + mSize + ")";
        }

        public boolean isReachMaxSize() {
            return mProcess >= mSize;
        }
    }


    public class BHostReceiver extends BroadcastReceiver {

        Intent mIntent = new Intent(ACTION_BHOST_APP_HEARTBEAT_RECEIVE);

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null)
                return;
            if (ACTION_STOP_HOST_DAEMON.equalsIgnoreCase(intent.getAction())) {
                stopForeground(true);
                stopSelf();
                new Thread(() -> {
                    SystemClock.sleep(1000);
                    System.exit(0);
                }).start();
            } else if (ACTION_BHOST_APP_HEARTBEAT.equalsIgnoreCase(intent.getAction())) {
                sendBroadcast(mIntent);
            } else {
                timer();
            }
        }
    }

}
