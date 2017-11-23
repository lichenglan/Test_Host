package com.techjumper.polyhome.polyhomebhost.by_function.log.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.techjumper.corelib.utils.Utils;
import com.techjumper.corelib.utils.common.JLog;
import com.techjumper.polyhome.polyhomebhost.by_function.log.LogUtils;
import com.techjumper.polyhome.polyhomebhost.by_function.log.tasks.PolyLogDbTaskQueue;
import com.techjumper.polyhome.polyhomebhost.by_function.log.tasks.UpdateAndCleanTask;
import com.techjumper.polyhome.polyhomebhost.utils.PollingUtils;

import java.util.Calendar;
import java.util.Random;
import java.util.TimeZone;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 16/6/14
 * * * * * * * * * * * * * * * * * * * * * * *
 **/
public class LogAlarmService extends Service {

    public static final String ACTION_UPLOAD_LOG = "action_update_log";

    public static void startUpdateAlarm() {
        Calendar c = Calendar.getInstance();
//        long oneHourLater = System.currentTimeMillis() + 1000L * 5;
        c.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        long interval = 45 * 60 * 1000L;
        c.setTimeInMillis(System.currentTimeMillis() + interval);
//        c.set(Calendar.HOUR_OF_DAY, 2);
//        c.set(Calendar.MINUTE, 30 + getRandomNum(0, 10));
//        c.set(Calendar.SECOND, 0);
//        c.set(Calendar.MILLISECOND, 0);
        PollingUtils.startPollingServiceBySet(Utils.appContext
                , c.getTimeInMillis(), LogAlarmService.class
                , ACTION_UPLOAD_LOG
                , true, 26, true);
        LogUtils.insertLog("开启定时上传Log，每" + interval / 60 / 1000L + "分钟一次");
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
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) return super.onStartCommand(null, flags, startId);

        switch (intent.getAction()) {
            case ACTION_UPLOAD_LOG:
                startUpdateAlarm();
                JLog.d("<log> 触发定时任务，准备上传日志");
                LogUtils.insertLog("触发定时任务，准备上传日志");
                PolyLogDbTaskQueue.getInstance().addToQueue(new UpdateAndCleanTask());
                break;
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}
