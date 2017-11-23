package com.techjumper.polyhome.polyhomebhost.mvp.p.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;

import com.morgoo.droidplugin.pm.PluginManager;
import com.techjumper.corelib.utils.Utils;
import com.techjumper.corelib.utils.common.AcHelper;
import com.techjumper.corelib.utils.common.JLog;
import com.techjumper.corelib.utils.file.PreferenceUtils;
import com.techjumper.corelib.utils.window.ToastUtils;
import com.techjumper.polyhome.droidpluginmanager.PluginAssetsManager;
import com.techjumper.polyhome.droidpluginmanager.PluginController;
import com.techjumper.polyhome.droidpluginmanager.PluginInstaller;
import com.techjumper.polyhome.droidpluginmanager.PluginUtils;
import com.techjumper.polyhome.droidpluginmanager.entity.ApkEntity;
import com.techjumper.polyhome.polyhomebhost.Config;
import com.techjumper.polyhome.polyhomebhost.by_function.heartbeat.BroadcastHeartbeatChecker;
import com.techjumper.polyhome.polyhomebhost.by_function.heartbeat.BroadcastHeartbeatTask;
import com.techjumper.polyhome.polyhomebhost.by_function.heartbeat.HeartbeatConstants;
import com.techjumper.polyhome.polyhomebhost.by_function.log.service.LogAlarmService;
import com.techjumper.polyhome.polyhomebhost.mvp.v.activity.MainActivity;
import com.techjumper.polyhome.polyhomebhost.service.AlarmReceiveService;
import com.techjumper.polyhome.polyhomebhost.service.BHostDaemonService;
import com.techjumper.polyhome.polyhomebhost.service.HomeDaemonService;
import com.techjumper.polyhome.polyhomebhost.service.HostService;
import com.techjumper.polyhome.polyhomebhost.utils.PolicyUtil;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 16/4/19
 * * * * * * * * * * * * * * * * * * * * * * *
 **/
public class MainActivityPresenter extends AppBaseActivityPresenter<MainActivity>
        implements PluginController.IPluginController, BroadcastHeartbeatTask.IHeartbeatTaskListener {


    public static final String PLUGIN_QUIT = "plugin_quit";
    public static final String ACTION_BHOST_RESTART = "bhost_restart";

    public static final String ACTION_HOST_HEARTBEAT = "action_host_heartbeat";
    public static final String ACTION_HOST_HEARTBEAT_RECEIVE = "action_host_heartbeat_receive";

    private HeartbeatReceiver mHeartbeatReceiver = new HeartbeatReceiver();
    private BroadcastHeartbeatChecker mHeartbeatChecker;

    @Override
    public void initData(Bundle savedInstanceState) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = Utils.appContext.getPackageManager().getPackageInfo(Utils.appContext.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        if (packageInfo != null) {
            JLog.d("BHost版本号: " + packageInfo.versionName);
        }
        getView().startService(new Intent(getView(), HostService.class));
//        getView().startService(new Intent(getView(), BHostDaemonService.class));
        IntentFilter intentFilter = new IntentFilter(ACTION_HOST_HEARTBEAT);
        intentFilter.addAction(HomeDaemonService.ACTION_STOP);
        getView().registerReceiver(mHeartbeatReceiver, intentFilter);

        //上传日志的定时任务
        LogAlarmService.startUpdateAlarm();

        mHeartbeatChecker = BroadcastHeartbeatChecker.registe(getView());
        Log.d(HeartbeatConstants.TAG_HEARTBEAT, "添加了一个心跳包：" + HeartbeatConstants.BLAUNCHER_HEARTBEAT);
        mHeartbeatChecker.addHeartbeatTask(new BroadcastHeartbeatTask(getView()
                , HeartbeatConstants.BLAUNCHER_HEARTBEAT, this));

    }

    @Override
    public void onViewInited(Bundle savedInstanceState) {
        getView().showLoading();
        PluginController.getInstance().start(this);

        JLog.d("PUSH ID: " + HostService.getJpushId());
    }

    @Override
    public void onPluginConnected() {

        Observable.timer(5, TimeUnit.MINUTES)
                .subscribe(aLong -> {
                    //开启自动更新
                    AlarmReceiveService.startUpdateAlarm();
                });

        if (PolicyUtil.hasLockPermission()) {
            startCopyPlugin();
        } else {
            PolicyUtil.requestLockPermision(getView(), 99);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != 99) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }

        if (resultCode == Activity.RESULT_OK) {
            startCopyPlugin();
        } else {
            getView().finish();
        }

    }

    private void startCopyPlugin() {
        boolean isFirstRun = PreferenceUtils.getPreference()
                .getBoolean(PluginInstaller.KEY_FIRST_RUN, true);

        addSubscription(
                Observable
                        .create(subscriber -> {
                            try {
                                PluginInstaller.getInstance().deletePreDir();
                            } catch (IOException ignored) {
                            }
                            subscriber.onNext(null);
                            subscriber.onCompleted();
                        })
                        .flatMap(obj -> PluginAssetsManager.getInstance().copyAssetsPluginToInstallDir())
                        .subscribeOn(Schedulers.io())
                        .subscribe(new Subscriber<Boolean>() {
                            @Override
                            public void onCompleted() {
                                installAll();
                            }

                            @Override
                            public void onError(Throwable e) {
                                JLog.e("出错了: " + e);
                                getView().dismissLoading();
                            }

                            @Override
                            public void onNext(Boolean aBoolean) {

                            }
                        })

        );
    }

    private void installAll() {
        addSubscription(
                PluginInstaller.getInstance().installAllWaitApk()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<ApkEntity>() {
                            @Override
                            public void onCompleted() {
                                try {
                                    List<ApplicationInfo> installedApplications = PluginManager.getInstance().getInstalledApplications(0);
                                    JLog.d("应用个数: " + (installedApplications == null ? 0 : installedApplications.size()));
                                    if (installedApplications != null) {
                                        for (ApplicationInfo info : installedApplications) {
                                            JLog.d("包名: " + info.packageName);
                                        }
                                    }

                                } catch (RemoteException e) {
                                    JLog.d("出错: " + e);
                                    e.printStackTrace();
                                }
                                try {
                                    PluginUtils.startPluginLauncherActivity(getView()
                                            , Config.sLauncherPackageName);
                                    PreferenceUtils.getPreference().edit()
                                            .putBoolean(PluginInstaller.KEY_FIRST_RUN, false).apply();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    JLog.e(e);
                                    ToastUtils.show("无法启动Launcher");
                                    AcHelper.finish(getView());
                                }
                                getView().dismissLoading();
                                PluginController.getInstance().removeListener(MainActivityPresenter.this);
                            }

                            @Override
                            public void onError(Throwable e) {
                                PluginInstaller.getInstance().installCompelete();
                                JLog.e_stack(e);
                                getView().dismissLoading();
                            }

                            @Override
                            public void onNext(ApkEntity apkEntity) {
                            }
                        })
        );
    }

    @Override
    public void onDestroy() {
        getView().unregisterReceiver(mHeartbeatReceiver);
        getView().sendBroadcast(new Intent(MainActivityPresenter.PLUGIN_QUIT));
        getView().stopService(new Intent(getView(), HomeDaemonService.class));
        getView().stopService(new Intent(getView(), BHostDaemonService.class));
        getView().stopService(new Intent(getView(), HostService.class));
        quitPlugin();
        quitAll();
        mHeartbeatChecker.unregiste();
        super.onDestroy();
    }

    private void quitPlugin() {
        List<PackageInfo> installedPackages = null;
        try {
            installedPackages = PluginManager.getInstance().getInstalledPackages(0);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        if (installedPackages == null) {
            quitAll();
            return;
        }

        for (PackageInfo info : installedPackages) {
            if (info == null || !PluginManager.getInstance().isConnected())
                continue;
            try {
                PluginManager.getInstance().forceStopPackage(info.packageName);
            } catch (RemoteException ignored) {
            }
        }
    }

    private void quitAll() {
        PluginController.getInstance().quit();
        new Thread(() -> {
            SystemClock.sleep(1000);
//            System.exit(0);
            try {
                killBackgroundProcesses();
            } catch (RemoteException e) {
                JLog.d(e);
            }
        }).start();
    }

    private void killBackgroundProcesses() throws RemoteException {
        killBackgroundProcesses(true);
    }

    private void killBackgroundProcesses(boolean includeSelf) throws RemoteException {
        ActivityManager am = (ActivityManager) Utils.appContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> infos = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info : infos) {
            if (info.pkgList != null) {
                if (info.pid != android.os.Process.myPid() && !containPkg(info.processName)) {
                    android.os.Process.killProcess(info.pid);
//                    JLog.d("杀掉了进程: " + info.processName + ", pid=" + info.pid);
                }
            }
        }
        if (includeSelf)
            android.os.Process.killProcess(android.os.Process.myPid());
    }

    private boolean containPkg(String pkgName) {
        String[] pkgs = new String[]{
                "com.dnake.talk"
        };
        for (String pkg : pkgs) {
            if (pkg.equalsIgnoreCase(pkgName))
                return true;
        }
        return false;
    }

    @Override
    public void onPluginDisconnected() {

    }

    @Override
    public void onHeartbeatReceive(String heartbeat) {
        Log.d(HeartbeatConstants.TAG_HEARTBEAT, "收到心跳包：" + heartbeat);
    }

    @Override
    public void onHeartbeatTimeout(String heartbeat) {
        Log.d(HeartbeatConstants.TAG_HEARTBEAT, "心跳包超时了：" + heartbeat);
        try {
            killBackgroundProcesses(false);
        } catch (RemoteException e) {
            JLog.e(e);
        }
        JLog.d("因为心跳包超时，所以重新启动相关插件");
        onViewInited(null);
    }

    public class HeartbeatReceiver extends BroadcastReceiver {
        private Intent mIntent = new Intent(ACTION_HOST_HEARTBEAT_RECEIVE);

        @Override
        public void onReceive(Context context, Intent intent) {
            if (HomeDaemonService.ACTION_STOP.equals(intent.getAction())) {
                if (mHeartbeatChecker != null) {
                    mHeartbeatChecker.unregiste();
                }
                return;
            }
            context.sendBroadcast(mIntent);
        }
    }

}
