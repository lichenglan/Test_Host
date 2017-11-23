package com.techjumper.polyhome.polyhomebhost.service;

import android.accounts.NetworkErrorException;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.f2prateek.rx.receivers.RxBroadcastReceiver;
import com.morgoo.droidplugin.pm.PluginManager;
import com.techjumper.corelib.rx.tools.RxUtils;
import com.techjumper.corelib.utils.Utils;
import com.techjumper.corelib.utils.common.JLog;
import com.techjumper.corelib.utils.file.PreferenceUtils;
import com.techjumper.corelib.utils.window.ToastUtils;
import com.techjumper.lib2.utils.GsonUtils;
import com.techjumper.polyhome.droidpluginmanager.PluginController;
import com.techjumper.polyhome.droidpluginmanager.PluginInstaller;
import com.techjumper.polyhome.droidpluginmanager.PluginOneInstaller;
import com.techjumper.polyhome.droidpluginmanager.PluginUtils;
import com.techjumper.polyhome.droidpluginmanager.entity.ApkEntity;
import com.techjumper.polyhome.polyhomebhost.Config;
import com.techjumper.polyhome.polyhomebhost.R;
import com.techjumper.polyhome.polyhomebhost.by_function.log.LogUtils;
import com.techjumper.polyhome.polyhomebhost.entity.core.SaveInfoEntity;
import com.techjumper.polyhome.polyhomebhost.entity.core.StartPluginActivityEntity;
import com.techjumper.polyhome.polyhomebhost.jpush.JPushReceiver;
import com.techjumper.polyhome.polyhomebhost.net.NetExecutor;
import com.techjumper.polyhome.polyhomebhost.net.NetHelper;
import com.techjumper.polyhome.polyhomebhost.system.HostApplication;
import com.techjumper.polyhome.polyhomebhost.tools.UpdateChecker;
import com.techjumper.polyhome.polyhomebhost.tools.UpdateExecutor;
import com.techjumper.polyhome.polyhomebhost.utils.HostDataBuilder;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cn.jpush.android.api.JPushInterface;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Func1;


/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 16/5/17
 * * * * * * * * * * * * * * * * * * * * * * *
 **/
public class HostService extends Service implements PluginOneInstaller.IApkInstall {

//    private final Messenger mOtherMessenger = new Messenger(new OtherMessagenerHandler(this));

    public static final int CODE_START_PLUGIN = 1;  //打开指定插件
    public static final int CODE_START_PLUGIN_ACTIVITY = 2; //打开指定插件的指定页面
    public static final int CODE_GET_PLUGIN_INFO = 3; //获取插件信息
    public static final int CODE_SAVE_INFO = 4; //保存信息到本地
    public static final int CODE_GET_SAVE_INFO = 5; //获取本地信息
    public static final int CODE_UPDATE_PLUGIN = 6; //联网更新插件
    public static final int CODE_GET_PUSH_ID = 7; //获取推送的push id
    public static final int CODE_INSERT_LOG = 8; //往系统插入一条日志

    /**
     * 与核心层通信专用code
     */
    public static final int CODE_CORE_INSTALL_PLUGIN = 1000;//执行安装插件的流程
    public static final int CODE_CORE_RECEIVE_REPLY_MESSENGER = 1001;//传递 reply messenger 到 bhost
    public static final int CODE_CORE_DOWNLOAD_PLUGIN = 1002;//下载插件

    public static final String KEY_MESSAGE = "key_msg";
    public static final String KEY_EXTRA = "key_extra";

    public static final String ACTION_INSTALL_COMPLETE = "action_install_complete";
    public static final String ACTION_OTHER_APK_REPLY = "action_other_apk_reply";
    public static final String ACTION_HOST_PLUGIN_UPDATE_COMPLETE = "action_host_plugin_update_complete";
    public static final String ACTION_RECEIVER_TO_PLUGIN_INSTRUCTION = "action_receiver_to_plugin_instruction";

    private final Messenger mMessenger = new Messenger(new MessengerHandler(this));

    public int mErrorCount;

    private static Messenger mClient;
    private Subscription mUpdateSubs;
    private PluginOneInstaller mPluginInstaller;
    private NumberProcessController mNumberProcessC = new NumberProcessController();
    private Subscription mDelaySubs;
    private boolean mIsFirstUpdateSuccess = true;

    public static final String POLY_B_FAMILY_REGISTER = "poly_b_family_register";

//    private BroadcastReceiver mHostReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (ACTION_HOST_PLUGIN_UPDATE_COMPLETE.equalsIgnoreCase(intent.getAction())) {
//
//            }
//        }
//    };

    public Subscription mRTPI;

    @Override
    public void onCreate() {
        super.onCreate();
//        IntentFilter filter = new IntentFilter(ACTION_HOST_PLUGIN_UPDATE_COMPLETE);
//        registerReceiver(mHostReceiver, filter);
        JLog.d("屏幕分辨率: " + getResources().getDisplayMetrics().widthPixels
                + " x " + getResources().getDisplayMetrics().heightPixels);

        mRTPI = RxBroadcastReceiver.create(this, new IntentFilter(ACTION_RECEIVER_TO_PLUGIN_INSTRUCTION))
                .subscribe(intent -> {
                    if (intent.getExtras() == null
                            || intent.getExtras().getInt(KEY_MESSAGE, -1) == -1)
                        return;

                    int msgCode = intent.getExtras().getInt(KEY_MESSAGE, -1);
                    switch (msgCode) {
                        case CODE_UPDATE_PLUGIN:
                            processUpdatePlugin(false);
                            break;
                    }
                });

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        int check = checkCallingOrSelfPermission("com.techjumper.polyhome.polyhomebhost.permission.ACCESS");
        if (check == PackageManager.PERMISSION_DENIED) {
            return null;
        }
        return mMessenger.getBinder();
    }


    @Override
    public void onApkInstalled(ApkEntity apkEntity) {
        timer();
        mNumberProcessC.increase();
        JLog.d("完成一次更新 " + mNumberProcessC.getFormatString());
        LogUtils.insertLog("完成一次更新 " + mNumberProcessC.getFormatString());
        ToastUtils.showLong("完成一次更新 " + mNumberProcessC.getFormatString());
        //循环去请求, 直到没有apk可下为止
        if (mNumberProcessC.isReachMaxSize()) {
            mNumberProcessC.reset();
            if (mIsFirstUpdateSuccess) { //如果是第一次更新成功则不做延迟处理
                mIsFirstUpdateSuccess = false;
                processUpdatePlugin(true);
                return;
            }
            Utils.mainHandler.postDelayed(() -> {
                processUpdatePlugin(true);
            }, 60 * 1000L);
        }

//        if (apkEntity == null) {
//            return;
//        } else if (apkEntity.getPackageInfo() == null) {
//            return;
//        }
//        String packageName = apkEntity.getPackageInfo().packageName;
//        if (Config.sLauncherPackageName.equals(packageName)) {
//            Utils.mainHandler.postDelayed(() -> {
//                try {
//                    JLog.d("更新了核心功能");
//                    killBackgroundProcesses();
//                } catch (RemoteException ignored) {
//                }
//
//            }, 500);
//        } else {
//            sendBroadcast(new Intent(ACTION_INSTALL_COMPLETE));
//        }
    }

    private void timer() {
        RxUtils.unsubscribeIfNotNull(mDelaySubs);
        mDelaySubs = Observable.timer(15, TimeUnit.MINUTES)
                .subscribe(aLong -> {
                    JLog.d("长时间无反应, 先启动Launcher");
                    mNumberProcessC.reset();
                    mIsFirstUpdateSuccess = true;
                    startLauncher();
                });
        JLog.d("重启无操作计时");
    }


//    private static class OtherMessagenerHandler extends MessengerHandler {
//
//        public OtherMessagenerHandler(HostService service) {
//            super(service);
//        }
//
//        @Override
//        protected boolean isMainMessenger {
//            return false;
//        }
//    }

    private static class MessengerHandler extends Handler {
        private HostService mService;

        MessengerHandler(HostService service) {
            mService = service;
        }

        @Override
        public void handleMessage(Message msg) {
            String data = msg.getData().getString(KEY_MESSAGE);
            Bundle bundle = msg.getData().getBundle(KEY_EXTRA);
            JLog.d("收到BLauncher消息: code=" + msg.what + " content=" + data);
            int code = msg.what;
            int newCode = code / 10000;
            boolean isMainMessenger = false;
            if (newCode != 0) {
                code = newCode;
                isMainMessenger = true;
            }
            switch (code) {
                case CODE_START_PLUGIN:
                    mService.processStartPlugin(data, bundle, isMainMessenger);
                    break;
                case CODE_START_PLUGIN_ACTIVITY:
                    mService.processStartPluginActivity(data, bundle, isMainMessenger);
                    break;
                case CODE_GET_PLUGIN_INFO:
                    mService.processGetPluginInfo(msg, data, CODE_GET_PLUGIN_INFO, isMainMessenger);
                    break;
                case CODE_SAVE_INFO:
                    mService.processSaveInfo(msg, data, isMainMessenger);
                    break;
                case CODE_GET_SAVE_INFO:
                    mService.processGetSaveInfo(msg, data, isMainMessenger);
                    break;
                case CODE_UPDATE_PLUGIN:
                    mService.processUpdatePlugin(false);
                    break;
                case CODE_CORE_INSTALL_PLUGIN:
//                    mService.processInstallPlugin();
                    break;
                case CODE_GET_PUSH_ID:
                    mService.processGetPushId(msg, data, isMainMessenger);
                    break;
                case CODE_CORE_RECEIVE_REPLY_MESSENGER:
                    mService.processReceiveReplyMessenger(msg, isMainMessenger);
                    break;
                case CODE_CORE_DOWNLOAD_PLUGIN:
                    mService.processDownloadPlugin(msg, bundle, isMainMessenger);
                    break;
                case CODE_INSERT_LOG:
                    mService.processInsertLog(data);
                    break;
                default:
                    break;
            }
        }

    }

    private void processInsertLog(String message) {
        JLog.d("<log>远程进程调用了插入日志，内容为：" + message);
        LogUtils.insertLog(message);
    }

    private void processUpdatePlugin(boolean isHost) {
        LogUtils.insertLog("BHost接到更新插件的指令");

        NetExecutor.getUserInfo()
                .flatMap(userEntity -> {
                    if (!NetHelper.processNetworkResult(userEntity)) {
                        String log = userEntity == null ? "请求服务器失败，数据为空"
                                : "请求服务器失败，code=" + userEntity.getError_code() + " msg=" + userEntity.getError_msg();
                        return Observable.error(new NetworkErrorException(log));
                    }
                    if (userEntity.getData() == null)
                        return Observable.error(new NullPointerException("用户信息为空"));

                    return Observable.just(userEntity.getData().getId() + "");
                })
                .map(new Func1<String, Boolean>() {
                    @Override
                    public Boolean call(String id) {
                        if (TextUtils.isEmpty(id)) {
                            JLog.d("本地用户数据为空, 不进行更新");
                            LogUtils.insertLog("本地用户数据为空，不进行更新");
                            return false;
                        }
                        JLog.d("得到家庭ID：" + id);
                        LogUtils.insertLog("得到家庭ID：" + id);
                        sendBroadcast(new Intent("action_update_bhost"));
                        timer();
                        PluginController.getInstance().start(new PluginController.IPluginController() {
                            @Override
                            public void onPluginConnected() {
                                PluginController.getInstance().removeListener(this);

                                List<PackageInfo> installedPackages = null;
                                try {
                                    installedPackages = PluginManager.getInstance().getInstalledPackages(0);
                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                    JLog.e("获取插件列表失败:" + e);
                                }
                                ArrayList<PackageInfo> packageInfosAL = new ArrayList<>();
                                if (installedPackages != null) {
                                    packageInfosAL.addAll(installedPackages);
                                }
                                UpdateChecker.getInstance().execute(packageInfosAL, id, isHost, new UpdateChecker.IUpdateChecker() {
                                    @Override
                                    public void onPluginListReceive(ArrayList<String> downloadUrls) {
                                        processDownloadPlugin(downloadUrls);
                                    }

                                    @Override
                                    public void onPluginAlreadyLatest(boolean isHost) {
                                        mNumberProcessC.reset();
                                        RxUtils.unsubscribeIfNotNull(mDelaySubs);
                                        if (!isHost)
                                            return;
                                        mIsFirstUpdateSuccess = true;
                                        JLog.d("插件安装完成啦");
                                        LogUtils.insertLog("插件安装完成啦");
                                        startLauncher();
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        RxUtils.unsubscribeIfNotNull(mDelaySubs);
                                    }
                                });

                            }

                            @Override
                            public void onPluginDisconnected() {

                            }
                        });
                        return true;
                    }
                })
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        JLog.d(e);
                        LogUtils.insertLog("更新失败了：" + e);
                        if (isHost) {
                            mIsFirstUpdateSuccess = true;
                            startLauncher();
                        }

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {

                    }
                });


    }

//    public static String getFamilyId() {
//        String data = HostDataBuilder.saveInfoBuilder()
//                .name(POLY_B_FAMILY_REGISTER)
//                .build();
//        String listJson = loadLocalDataFromJson(data);
//        if (TextUtils.isEmpty(listJson)) {
//            return "";
//        }
//        JLog.d("本地用户数据: " + listJson);
//        SaveInfoEntity userInfo = GsonUtils.fromJson(listJson, SaveInfoEntity.class);
//        SaveInfoEntity.DataEntity userInfoData = userInfo.getData();
//        if (userInfoData == null || userInfoData.getValues() == null) {
//            return "";
//        }
//
//        HashMap<String, String> values = userInfoData.getValues();
//        if (!values.containsKey("id")) {
//            JLog.d("没有找到本地用户的ID");
//            return "";
//        }
//        return values.get("id");
//    }

    private void startLauncher() {
//        boolean success = RxUtils.unsubscribeIfNotNull(mDelaySubs);
//        JLog.d("取消无操作定时判断:" + success);
//        //停止心跳包
//        sendBroadcast(new Intent(HomeDaemonService.ACTION_STOP));
//        try {
//            killBackgroundProcesses();
//        } catch (RemoteException e) {
//            JLog.d(e);
//        }
//        Utils.mainHandler.postDelayed(() -> {
//            try {
//                JLog.d("插件安装完毕, 正在启动首页");
//                ToastUtils.showLong("更新完毕, 正在启动首页...");
//                PluginUtils.startPluginLauncherActivity(HostService.this
//                        , Config.sLauncherPackageName);
//            } catch (Exception e) {
//                JLog.d(e);
//                startLauncher();
//            }
//        }, 1000);
        LogUtils.insertLog("准备重启机器");
        Utils.mainHandler.postDelayed(this::reboot, 2000);
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

    private void processDownloadPlugin(Message msg, Bundle bundle, boolean isMainMessenger) {
        HostApplication.sMainHandler.post(() -> {
            if (bundle == null || bundle.get(KEY_EXTRA) == null || !(bundle.get(KEY_EXTRA) instanceof ArrayList)) {
                JLog.d("传递过来的下载数据有误");
                return;
            }
            ArrayList<String> urls = bundle.getStringArrayList(KEY_EXTRA);
            if (urls == null) {
                JLog.d("传递过来的下载数据有误");
                return;
            }
            ToastUtils.show("发现新版本, 正在更新...");
            JLog.d("有插件需要更新,数量:" + urls.size());
            mNumberProcessC.init(urls.size());
            //停止心跳包
            sendBroadcast(new Intent(HomeDaemonService.ACTION_STOP));
            try {
                killBackgroundProcesses();
            } catch (RemoteException e) {
                JLog.d(e);
            }
            UpdateExecutor.getInstance().execute(urls, donwloadedPath -> {
                try {
                    installOnePlugin(donwloadedPath);
                } catch (IOException e) {
                    JLog.d(e);
                }
            });
        });
    }

    private void processDownloadPlugin(ArrayList<String> urls) {
        HostApplication.sMainHandler.post(() -> {

            if (urls == null || urls.size() == 0) {
                return;
            }
            if (mNumberProcessC.isIncreasing()) {
                LogUtils.insertLog("之前的更新还未结束，不再重复更新");
                JLog.d("之前的更新还未结束, 不再重复更新");
                return;
            }
            ToastUtils.show("发现新版本, 正在更新...");
            JLog.d("有插件需要更新,数量:" + urls.size());
            LogUtils.insertLog("有插件需要更新：" + urls.size() + "个");
            mNumberProcessC.init(urls.size());
            //停止心跳包
            sendBroadcast(new Intent(HomeDaemonService.ACTION_STOP));
//            try {
//                killBackgroundProcesses();
//            } catch (RemoteException e) {
//                JLog.d(e);
//            }
            UpdateExecutor.getInstance().execute(urls, donwloadedPath -> {
                try {
                    try {
                        killBackgroundProcesses();
                    } catch (RemoteException e) {
                        JLog.d(e);
                    }
                    installOnePlugin(donwloadedPath);
                } catch (IOException e) {
                    JLog.d(e);
                }
            });
        });
    }

    private void installOnePlugin(String downloadPath) throws IOException {
        try {
            getPluginInstaller().install(PluginInstaller.getInstance().extraNameFromUrl(downloadPath)
                    , this);
        } catch (InterruptedException e) {
            JLog.e(e);
        }

    }

    private PluginOneInstaller getPluginInstaller() {
        if (mPluginInstaller == null) {
            synchronized (HostService.class) {
                if (mPluginInstaller == null) {
                    mPluginInstaller = new PluginOneInstaller();
                }
            }
        }
        return mPluginInstaller;
    }

    private void processReceiveReplyMessenger(Message msg, boolean bLauncherMessenger) {
        mClient = msg.replyTo;

        startService(new Intent(this, HomeDaemonService.class));
        JLog.d("核心层与宿主已建立关联");
    }

    public static String getJpushId() {
        String registrationID = JPushInterface.getRegistrationID(Utils.appContext);
        if (TextUtils.isEmpty(registrationID)) {
            registrationID = PreferenceUtils.getPreference(JPushReceiver.SP_NAME).getString(JPushReceiver.KEY_ID, "");
        }
        return registrationID;
    }

    private void processGetPushId(Message msg, String data, boolean bLauncherMessenger) {
        Messenger client = msg.replyTo;
        Bundle bundle = new Bundle();

        bundle.putString(KEY_MESSAGE, getJpushId());
        replay(client, CODE_GET_PUSH_ID, bundle, bLauncherMessenger);
    }

    private void processInstallPlugin() {
//        if (PluginInstaller.getInstance().isInstalling()) {
//            JLog.d("已经有更新任务在执行, 不再重复执行");
//            return;
//        }
        RxUtils.unsubscribeIfNotNull(mUpdateSubs);
        mUpdateSubs = PluginInstaller.getInstance().installAllWaitApk()
                .subscribe(new Subscriber<ApkEntity>() {
                    @Override
                    public void onCompleted() {
//                        if (PluginInstaller.getInstance().hasWiatInstallPlugin()) {
//                            PluginInstaller.getInstance().installAllWaitApk()
//                                    .subscribe(this);
//                            JLog.d("检查到还有apk未安装完, 所以继续安装");
//                        } else {
                        HostService.this.sendBroadcast(new Intent(ACTION_INSTALL_COMPLETE));
                        ToastUtils.show("完成了安装");
                        JLog.d("完成了安装");
//                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        PluginInstaller.getInstance().installCompelete();
                        if (mErrorCount != 0) {
                            mErrorCount = 0;
                            return;
                        }
                        mErrorCount++;
                        if (PluginInstaller.getInstance().hasWiatInstallPlugin()) {
                            PluginInstaller.getInstance().installAllWaitApk()
                                    .subscribe(this);
                        } else {
                            JLog.d("部分功能更新失败");
                            ToastUtils.show("部分功能更新失败");
                        }
                    }

                    @Override
                    public void onNext(ApkEntity apkEntity) {
                        if (apkEntity == null
                                || apkEntity.getPackageInfo() == null
                                || apkEntity.getPackageInfo().packageName == null)
                            return;

                        String packageName = apkEntity.getPackageInfo().packageName;
                        JLog.d("更新了 " + packageName);
                        if (Config.sLauncherPackageName.equals(packageName)) {
                            try {
                                //杀掉所有其他进程
                                killBackgroundProcesses();
//                                JLog.d("更新的是 BLauncher, 所以重启 BLauncher");
                                JLog.d("更新的是 BLauncher");
//                                PluginUtils.startPluginLauncherActivity(HostService.this
//                                        , Config.sLauncherPackageName);
                            } catch (Exception e) {
                                e.printStackTrace();
                                JLog.e("杀掉进程出错: " + e);
                            }
                        } else if (!Config.sLauncherServiceLauncher.equals(packageName))
                            return;

                        try {
//                            JLog.d("更新的是首页, 所以重启首页");
                            JLog.d("更新的是 首页");
//                            PluginUtils.startPluginLauncherActivity(HostService.this
//                                    , Config.sLauncherServiceLauncher);
                        } catch (Exception e) {
                            e.printStackTrace();
                            JLog.e("更新插件后, 启动 插件的首页 失败: " + e);
                        }
                    }
                });
    }

    public static void killBackgroundProcesses() throws RemoteException {
        ActivityManager am = (ActivityManager) Utils.appContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> infos = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info : infos) {
            if (info.pkgList != null) {
                if (info.pid != android.os.Process.myPid() && !containPkg(info.processName)
                        && !TextUtils.isEmpty(info.processName)
                        && !info.processName.contains("install")
                        && !info.processName.contains("dnake")
                        ) {
                    android.os.Process.killProcess(info.pid);
//                    JLog.d("杀掉了进程: " + info.processName + ", pid=" + info.pid);
                }
            }
        }
    }

    private static boolean containPkg(String pkgName) {
        String[] pkgs = new String[]{
                "com.dnake.talk"
                , "com.techjumper.polyhome.polyhomebhost:remote"
                , "com.techjumper.polyhome.polyhomebhost"
        };
        for (String pkg : pkgs) {
            if (pkg.equalsIgnoreCase(pkgName))
                return true;
        }
        return false;
    }

    private void processGetSaveInfo(Message msg, String data, boolean bLauncherMessenger) {
        Messenger client = msg.replyTo;
        String message = loadLocalDataFromJson(data);
        Bundle bundle = new Bundle();
        bundle.putString(KEY_MESSAGE, message);
        replay(client, CODE_GET_SAVE_INFO, bundle, bLauncherMessenger);
    }

    private static String loadLocalDataFromJson(String data) {
        SaveInfoEntity saveInfoEntity = GsonUtils.fromJson(data, SaveInfoEntity.class);
        if (saveInfoEntity == null || saveInfoEntity.getData() == null)
            return "";
        String name = saveInfoEntity.getData().getName();
        if (TextUtils.isEmpty(name))
            return "";
        Map<String, ?> allData = PreferenceUtils.getPreference(name).getAll();
        HostDataBuilder.SaveInfoBuilder dataBuilder = HostDataBuilder.saveInfoBuilder()
                .name(name);
        if (allData != null) {
            for (Map.Entry<String, ?> next : allData.entrySet()) {
                String key = next.getKey();
                String value = String.valueOf(next.getValue());
                dataBuilder.put(key, value);
            }
        }
        return dataBuilder.build();
    }

    private void replay(Messenger client, int code, Bundle bundle, boolean isMainMessenger) {

        Message replyMessage = Message.obtain(null, code);
        replyMessage.setData(bundle);
        try {
            String message = "";
            if (bundle != null && bundle.getString(KEY_MESSAGE) != null) {
                message = bundle.getString(KEY_MESSAGE);
            }
            Log.d("HIDETAG", "BHost回应消息: " + message + ",isMainMessenger:" + isMainMessenger);

            client.send(replyMessage);
            if (!isMainMessenger
                    && mClient != null
                    ) {
                Log.d("HIDETAG", "BHost额外回复一次");
                mClient.send(replyMessage);
//                Intent intent = new Intent(ACTION_OTHER_APK_REPLY);
//                intent.putExtra(KEY_CODE, code);
//                intent.putExtra(KEY_EXTRA, bundle);
//                sendBroadcast(intent);
            } else if (!isMainMessenger) {
                Log.d("HIDETAG", "需要给插件额外回复,但是插件的Messenger为null");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            Log.e("HIDETAG", "Host 回复消息失败:" + e);
        }
    }

    @SuppressLint("CommitPrefEdits")
    private void processSaveInfo(Message msg, String data, boolean bLauncherMessenger) {
        Messenger client = msg.replyTo;
        SaveInfoEntity saveInfoEntity = GsonUtils.fromJson(data, SaveInfoEntity.class);
        if (saveInfoEntity == null || saveInfoEntity.getData() == null) return;
        SaveInfoEntity.DataEntity dataEntity = saveInfoEntity.getData();
        if (TextUtils.isEmpty(dataEntity.getName())
                || dataEntity.getValues() == null)
            return;
        SharedPreferences.Editor editor = PreferenceUtils.getPreference(dataEntity.getName()).edit();
        HashMap<String, String> values = dataEntity.getValues();
        for (Map.Entry<String, String> next : values.entrySet()) {
            String key = next.getKey();
            String value = next.getValue();
            editor.putString(key, value);
        }
        editor.commit();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_MESSAGE, dataEntity.getName());
        replay(client, CODE_SAVE_INFO, bundle, bLauncherMessenger);
    }

    private void processGetPluginInfo(Message msg, String data, int what, boolean bLauncherMessenger) {
        PluginController.getInstance().start(new PluginController.IPluginController() {
            @Override
            public void onPluginConnected() {
                PluginController.getInstance().removeListener(this);
                Messenger client = msg.replyTo;
                if (client == null) return;

                List<PackageInfo> installedPackages = null;
                try {
                    installedPackages = PluginManager.getInstance().getInstalledPackages(0);
                } catch (RemoteException e) {
                    e.printStackTrace();
                    JLog.e("获取插件列表失败:" + e);
                }
                ArrayList<PackageInfo> packageInfosAL = new ArrayList<>();
                if (installedPackages != null) {
                    packageInfosAL.addAll(installedPackages);
                }

                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList(KEY_EXTRA, packageInfosAL);
                bundle.putString(KEY_MESSAGE, data);
                Message replyMessage = Message.obtain(null, what);
                replyMessage.setData(bundle);
                try {
                    client.send(replyMessage);
                } catch (RemoteException e) {
                    e.printStackTrace();
                    Log.e("HIDETAG", "Host 回复消息失败:" + e);
                }
            }

            @Override
            public void onPluginDisconnected() {

            }
        });
    }

    public void processStartPlugin(String data, Bundle extra, boolean bLauncherMessenger) {
        StartPluginActivityEntity entity = GsonUtils.fromJson(data, StartPluginActivityEntity.class);
        if (entity == null || entity.getData() == null) {
            ToastUtils.show(getString(R.string.error_data));
            return;
        }

        StartPluginActivityEntity.DataEntity dataEntity = entity.getData();
        try {
            PluginUtils.startPluginLauncherActivity(this
                    , dataEntity.getPackageName(), extra);
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtils.show(getString(R.string.error_activity_not_found));
        }
    }

    public void processStartPluginActivity(String data, Bundle extra, boolean bLauncherMessenger) {
        StartPluginActivityEntity entity = GsonUtils.fromJson(data, StartPluginActivityEntity.class);
        if (entity == null || entity.getData() == null) {
            ToastUtils.show(getString(R.string.error_data));
            return;
        }

        StartPluginActivityEntity.DataEntity dataEntity = entity.getData();

        try {
            PluginUtils.startPluginActivity(this
                    , dataEntity.getPackageName(), dataEntity.getActivityName(), extra);
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtils.show(getString(R.string.error_activity_not_found));
        }
    }

    private class NumberProcessController {
        private int mSize;
        private int mProcess;

        void init(int size) {
            mSize = size;
            mProcess = 0;
        }

        void increase() {

            if (++mProcess > mSize) {
                mProcess = mSize;
            }
        }

        boolean isIncreasing() {
            return mSize != 0;
        }

        void reset() {
            mSize = mProcess = 0;
        }

        String getFormatString() {
            return "(" + mProcess + "/" + mSize + ")";
        }

        boolean isReachMaxSize() {
            return mProcess >= mSize;
        }
    }

    @Override
    public void onDestroy() {
        PluginController.getInstance().quit();
//        try {
//            unregisterReceiver(mHostReceiver);
//        } catch (Exception ignored) {
//        }
        RxUtils.unsubscribeIfNotNull(mRTPI);
        super.onDestroy();
    }
}
