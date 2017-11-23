package com.techjumper.polyhome.blauncher.service;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.techjumper.corelib.utils.common.JLog;
import com.techjumper.polyhome.blauncher.Config;
import com.techjumper.polyhome.blauncher.entity.core.StartPluginActivityEntity;
import com.techjumper.polyhome.blauncher.heartbeat.BroadcastHeartbeatClient;
import com.techjumper.polyhome.blauncher.interfaces.IServiceMessenger;
import com.techjumper.polyhome.blauncher.manager.HostDataBuilder;
import com.techjumper.polyhome.blauncher.manager.ServiceMessengerManager;
import com.techjumper.polyhome.blauncher.utils.LogUtils;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 16/5/17
 * * * * * * * * * * * * * * * * * * * * * * *
 **/
public class BLauncherService extends Service implements IServiceMessenger {

    public static final String PLUGIN_QUIT = "plugin_quit";
    public static final String ACTION_BHOST_RESTART = "bhost_restart";
    public static final String ACTION_INSTALL_COMPLETE = "action_install_complete";
    public static final String ACTION_UPDATE_PLUGIN = "action_blauncher_update_plugin";

    //激光推送action
    public static String ACTION_CUSTOM_MESSAGE_RECEIVE = "action_push_receive";
    public static String KEY_EXTRA = "key_extra";
    public static String UPDATE = "update";

    private static final int GRAY_SERVICE_ID = 910;
    private BroadcastHeartbeatClient mHeartbeatClient;
    private BroadcastReceiver mJPushReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            if (extras == null || extras.get(KEY_EXTRA) == null)
                return;
            if (!UPDATE.equalsIgnoreCase(extras.getString(KEY_EXTRA)))
                return;

            LogUtils.insertLog("收到推送更新~");
            ServiceMessengerManager.getInstance().send(ServiceMessengerManager.CODE_UPDATE_PLUGIN);
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private BroadcastReceiver mBLauncherReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACTION_UPDATE_PLUGIN.equalsIgnoreCase(intent.getAction())) {
                Intent intent1 = new Intent(context, AlarmReceiveService.class);
                intent1.setAction(AlarmReceiveService.ACTION_UPDATE_APK);
                intent1.putExtra(AlarmReceiveService.KEY_IS_HOST, true);
                startService(intent1);
            } else {
                JLog.d("BLauncherService收到退出广播");
                stopSelf();
            }
        }
    };
    private BroadcastReceiver mBHostRestartReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            startPluginLauncher();
        }
    };

    private BroadcastReceiver mBHostInstallPluginComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            startPluginLauncher(false);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        ServiceMessengerManager.getInstance().init(this);
        ServiceMessengerManager.getInstance().bind("com.techjumper.polyhome.polyhomebhost.MESSENGER.ACTION"
                , "com.techjumper.polyhome.polyhomebhost", this);

        IntentFilter filter = new IntentFilter(PLUGIN_QUIT);
        filter.addAction(ACTION_UPDATE_PLUGIN);
        registerReceiver(mBLauncherReceiver, filter);

        IntentFilter filter2 = new IntentFilter(ACTION_BHOST_RESTART);
        registerReceiver(mBHostRestartReceiver, filter2);

        IntentFilter filter3 = new IntentFilter(ACTION_INSTALL_COMPLETE);
        registerReceiver(mBHostInstallPluginComplete, filter3);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (ServiceMessengerManager.getInstance().isConnected()) {
            startPluginLauncher(false);
        }

        Intent innerIntent = new Intent(this, FakeService.class);
        startService(innerIntent);
        startForeground(GRAY_SERVICE_ID, new Notification());

        mHeartbeatClient = new BroadcastHeartbeatClient(this);
        mHeartbeatClient.startHeartbeat(BroadcastHeartbeatClient.BLAUNCHER_HEARTBEAT);
        return START_STICKY;
    }


    private void startPluginLauncher() {
        startPluginLauncher(true);
    }

    private void startPluginLauncher(boolean startLauncher) {

        ServiceMessengerManager.getInstance().send(ServiceMessengerManager.CODE_CORE_RECEIVE_REPLY_MESSENGER);

        String data;
        if (startLauncher) {
            data = HostDataBuilder.startPluginBuilder()
                    .activityName(StartPluginActivityEntity.ACTIVITY_LAUNCHER)
                    .packageName(Config.sLauncherPackageName)
                    .build();
            ServiceMessengerManager.getInstance().send(ServiceMessengerManager.CODE_START_PLUGIN, data);
        }

        //打开smarthome服务
        data = HostDataBuilder.startPluginBuilder()
                .activityName(StartPluginActivityEntity.ACTIVITY_LAUNCHER)
                .packageName("com.polyhome.smarthomeservice")
                .build();
        ServiceMessengerManager.getInstance().send(ServiceMessengerManager.CODE_START_PLUGIN, data);

        //打开医疗核心服务
        data = HostDataBuilder.startPluginBuilder()
                .activityName(StartPluginActivityEntity.ACTIVITY_LAUNCHER)
                .packageName("com.pltk.medicalservice")
                .build();
        ServiceMessengerManager.getInstance().send(ServiceMessengerManager.CODE_START_PLUGIN, data);

    }

    @Override
    public void onDestroy() {
        if (mHeartbeatClient != null)
            mHeartbeatClient.stop();
        unregisterReceiver(mBLauncherReceiver);
        unregisterReceiver(mBHostRestartReceiver);
        stopForeground(true);
        try {
            unregisterReceiver(mJPushReceiver);
        } catch (Exception ignored) {
        }
        ServiceMessengerManager.getInstance().unbind();
        JLog.d("BLauncher退出了");
        super.onDestroy();
    }

    @Override
    public void onServiceMessengerConnected() {
//        LogUtils.insertLog("5分钟后开启自动更新");

        //开启BLauncher更新插件
        AlarmReceiveService.startUpdateAlarm();

        try {
            //接收极光推送
            registerReceiver(mJPushReceiver, new IntentFilter(ACTION_CUSTOM_MESSAGE_RECEIVE));
        } catch (Exception ignored) {
        }
        startPluginLauncher();
//        ServiceMessengerManager.getInstance().send(ServiceMessengerManager.CODE_CORE_RECEIVE_REPLY_MESSENGER);
    }

    @Override
    public void onServiceDisconnected() {

    }

    @Override
    public void onServiceMessengerError(Throwable e) {

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

}
