package com.techjumper.polyhome.polyhomebhost.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.techjumper.corelib.utils.Utils;
import com.techjumper.polyhome.polyhomebhost.by_function.log.LogUtils;
import com.techjumper.polyhome.polyhomebhost.utils.HostRTPIUtils;
import com.techjumper.polyhome.polyhomebhost.utils.PollingUtils;

import java.util.Calendar;
import java.util.Random;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import rx.Observable;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 16/6/14
 * * * * * * * * * * * * * * * * * * * * * * *
 **/
public class AlarmReceiveService extends Service {

    public static final String ACTION_UPDATE_APK = "action_update_apk";
    public static final String KEY_IS_HOST = "key_is_host";

    //    private Subscription mAlarmReceiveSubs;
    private boolean isWaitingForPluginInfo;

    private boolean mIsHost;
    private static boolean mIsFirst = true;

    public static void startUpdateAlarm() {
        startBootUpdate();

        Calendar c = Calendar.getInstance();
//        long oneHourLater = System.currentTimeMillis() + 1000L * 5;
        c.setTimeZone(TimeZone.getTimeZone("GMT+8"));
//        long interval = 60 * 60 * 1000L;
        long interval = 24 * 60 * 60 * 1000L;
//        long interval = 0L;

//        boolean preSpecifyTime = isPreSpecifyTime();
//        if (!preSpecifyTime) {
        c.setTimeInMillis(System.currentTimeMillis() + interval);
//        }
        c.set(Calendar.HOUR_OF_DAY, 3);
        c.set(Calendar.MINUTE, /*0*/30 + getRandomNum(0, 10));
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        PollingUtils.startPollingServiceBySet(Utils.appContext
                , c.getTimeInMillis(), AlarmReceiveService.class
                , ACTION_UPDATE_APK
                , true, 2003, true);

//        LogUtils.insertLog("开启定时更新，每" + interval / 60 / 1000L + "分钟一次");
//        String log = preSpecifyTime ? "今天16点整更新" : "明天16点整更新";
//        LogUtils.insertLog("开启定时更新，" + log);
        LogUtils.insertLog("开启定时更新，第二天凌晨的3点30 - 3点40之间开始更新");
    }

    private static boolean isPreSpecifyTime() {
        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        c.setTimeInMillis(System.currentTimeMillis());
        c.set(Calendar.HOUR_OF_DAY, 16);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return System.currentTimeMillis() < c.getTimeInMillis();
    }

    private static void startBootUpdate() {
        if (!mIsFirst) {
            return;
        }
        mIsFirst = false;

//        Calendar c = Calendar.getInstance();
////        long oneHourLater = System.currentTimeMillis() + 1000L * 5;
//        c.setTimeZone(TimeZone.getTimeZone("GMT+8"));
////        long interval = 60 * 60 * 1000L;
//        long interval = 5 * 60 * 1000L;
//        c.setTimeInMillis(System.currentTimeMillis() + interval);
//        PollingUtils.startPollingServiceBySet(Utils.appContext
//                , c.getTimeInMillis(), AlarmReceiveService.class
//                , ACTION_UPDATE_APK
//                , true, 24, true);
        int delayMinute = 5;
        Observable.timer(delayMinute, TimeUnit.MINUTES)
                .subscribe(aLong -> {
                    LogUtils.insertLog("定时更新任务启动了");
                    HostRTPIUtils.updatePlugin();
                });
        LogUtils.insertLog("开启定时更新，" + delayMinute + "分钟后开始更新");
    }

    private static int getRandomNum(int min, int max) {
        return new Random().nextInt(max) % (max - min + 1) + min;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        mAlarmReceiveSubs = RxBus.INSTANCE.asObservable()
//                .subscribe(new Subscriber<Object>() {
//                    @Override
//                    public void onCompleted() {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//
//                    }
//
//                    @Override
//                    public void onNext(Object o) {
//                        if (o instanceof PluginInfoReceiveEvent) {
//                            if (!isWaitingForPluginInfo) return;
//                            isWaitingForPluginInfo = false;
//                            PluginInfoReceiveEvent event = (PluginInfoReceiveEvent) o;
//                            ArrayList<PackageInfo> pluginInfoList = event.getPluginInfoList();
//                            UpdateExecutor.getInstance().execute(pluginInfoList, mIsHost);
//                        }
//                    }
//                });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) return super.onStartCommand(null, flags, startId);

        switch (intent.getAction()) {
            case ACTION_UPDATE_APK:
                startUpdateAlarm();
//                if (isWaitingForPluginInfo) return super.onStartCommand(null, flags, startId);
//                JLog.d("定时更新触发, 开始获取最新APK信息");
//                isWaitingForPluginInfo = true;
//                mIsHost = intent.getBooleanExtra(KEY_IS_HOST, false);
                LogUtils.insertLog("定时更新任务启动了");
                HostRTPIUtils.updatePlugin();
                break;
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
//        RxUtils.unsubscribeIfNotNull(mAlarmReceiveSubs);
        super.onDestroy();
    }


}
