package com.techjumper.polyhome.blauncher.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.techjumper.corelib.utils.Utils;
import com.techjumper.polyhome.blauncher.manager.ServiceMessengerManager;
import com.techjumper.polyhome.blauncher.utils.LogUtils;
import com.techjumper.polyhome.blauncher.utils.PollingUtils;

import java.util.Calendar;
import java.util.Random;
import java.util.TimeZone;

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
        long interval = 24 * 60 * 60 * 1000L;
        long currentTime = System.currentTimeMillis();
        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        c.setTimeInMillis(currentTime);
        boolean inTime = c.get(Calendar.HOUR_OF_DAY) >= 22;
        if (inTime) {
            currentTime += interval;
            c.setTimeInMillis(currentTime);
        }
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        long start = c.getTimeInMillis();

        c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        c.setTimeInMillis(currentTime + interval);
        c.set(Calendar.HOUR_OF_DAY, 8);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        long end = c.getTimeInMillis();


        PollingUtils.startPollingServiceBySet(Utils.appContext
                , getRandomNum(start, end), AlarmReceiveService.class
                , ACTION_UPDATE_APK
                , true, 23, true);

        LogUtils.insertLog("blauncher开启定时更新，晚上11点到早上8点更新");
//        LogUtils.insertLog("开启定时更新，每" + interval / 60 / 1000L + "分钟一次");
//        LogUtils.insertLog("开启定时更新，第二天凌晨的3点30 - 3点40之间开始更新");
    }


    private static int getRandomNum(int min, int max) {
        return new Random().nextInt(max) % (max - min + 1) + min;
    }

    private static long getRandomNum(long min, long max) {
        return ((long) (Math.random() * max)) % (max - min + 1) + min;
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
                LogUtils.insertLog("blauncher定时更新任务启动了");
                ServiceMessengerManager.getInstance().send(ServiceMessengerManager.CODE_UPDATE_PLUGIN);
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
