package com.techjumper.polyhome_b.bhostdaemon.tools;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.techjumper.corelib.utils.Utils;
import com.techjumper.corelib.utils.basic.NumberUtil;
import com.techjumper.corelib.utils.common.JLog;
import com.techjumper.corelib.utils.window.ToastUtils;
import com.techjumper.polyhome_b.bhostdaemon.R;
import com.techjumper.polyhome_b.bhostdaemon.entity.UpdateAPKEntity;
import com.techjumper.polyhome_b.bhostdaemon.net.NetHelper;
import com.techjumper.polyhome_b.bhostdaemon.net.RetrofitTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import rx.Subscriber;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 16/6/13
 * * * * * * * * * * * * * * * * * * * * * * *
 **/
public class UpdateChecker {

    private static UpdateChecker INSTANCE;

    private HashMap<String, PackageInfo> mPackageInfoMap = new HashMap<>();//包名=PackageInfo
    private IUpdateChecker iUpdateChecker;

    private UpdateChecker() {
    }

    public static UpdateChecker getInstance() {
        if (INSTANCE == null) {
            synchronized (UpdateChecker.class) {
                if (INSTANCE == null) {
                    INSTANCE = new UpdateChecker();
                }
            }
        }
        return INSTANCE;
    }

    public interface IUpdateChecker {
        void onDonwloadUrlsReceive(ArrayList<String> downloadUrls);

        void onAppAlreadyLatest();
    }


    public void execute(IUpdateChecker iUpdateChecker) {

        this.iUpdateChecker = iUpdateChecker;
//        ToastUtils.show("正在获取更新");
        JLog.d("正在获取 系统软件 更新");

        mPackageInfoMap.clear();
        packageInfoInMap();


        String[] packageNames = new String[mPackageInfoMap.size()];
        Iterator<Map.Entry<String, PackageInfo>> it = mPackageInfoMap.entrySet().iterator();
        int count = 0;
        while (it.hasNext()) {
            Map.Entry<String, PackageInfo> next = it.next();
            String key = next.getKey();
            packageNames[count] = key;
            count++;
        }
        for (String name : packageNames) {
            PackageInfo packageInfo = mPackageInfoMap.get(name);
            int versionCode = packageInfo == null ? -1 : packageInfo.versionCode;
            JLog.d(name + ":" + versionCode);
        }

        RetrofitTemplate.getInstance().fetchApkInfo(packageNames)
                .subscribe(new Subscriber<UpdateAPKEntity>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        JLog.e("获取插件更新失败", e);
                        ToastUtils.show(Utils.appContext.getString(R.string.error_to_connect_server));
                    }

                    @Override
                    public void onNext(UpdateAPKEntity updateAPKEntity) {
                        if (!NetHelper.processNetworkResult(updateAPKEntity)) {
                            return;
                        }
                        if (updateAPKEntity.getData() == null
                                || updateAPKEntity.getData().getResult() == null
                                || updateAPKEntity.getData().getResult().size() == 0) {
//                            ToastUtils.show(Utils.appContext.getString(R.string.alread_latest_version));
                            JLog.d(Utils.appContext.getString(R.string.alread_latest_version));
                        }

                        ArrayList<String> downloadUrls = new ArrayList<>();
                        List<UpdateAPKEntity.DataEntity.ResultEntity> results = updateAPKEntity.getData().getResult();
                        for (UpdateAPKEntity.DataEntity.ResultEntity serverApk : results) {
                            if (serverApk == null) continue;
                            PackageInfo localApk = mPackageInfoMap.get(serverApk.getPackage_name());
                            if (localApk == null) continue;
                            int serverVersion = NumberUtil.convertToint(serverApk.getVersion(), -1);
                            if (serverVersion == -1) continue;
                            if (localApk.versionCode < serverVersion) {
                                if (TextUtils.isEmpty(serverApk.getUrl()))
                                    continue;
                                downloadUrls.add(serverApk.getUrl());
                                JLog.d("发现更新: " + localApk.packageName + ", 本地版本:" + localApk.versionCode + ", 服务器版本:" + serverApk.getVersion());
                            }
                        }

                        if (downloadUrls.size() == 0) {
//                            if (isHost) {
//                                Utils.appContext.sendBroadcast(new Intent("action_host_plugin_update_complete"));
//                            }
                            if (iUpdateChecker != null) {
                                iUpdateChecker.onAppAlreadyLatest();
                            }
//                            ToastUtils.show(Utils.appContext.getString(R.string.alread_latest_version));
                            JLog.d(Utils.appContext.getString(R.string.alread_latest_version));
                        } else {
                            if (iUpdateChecker != null) {
                                iUpdateChecker.onDonwloadUrlsReceive(downloadUrls);
                            }
//                            Bundle bundle = new Bundle();
//                            bundle.putStringArrayList(ServiceMessengerManager.KEY_EXTRA, downloadUrls);
//                            ServiceMessengerManager.getInstance().send(ServiceMessengerManager.CODE_CORE_DOWNLOAD_PLUGIN
//                                    , null, bundle);
                        }


                    }
                });
    }


    private void packageInfoInMap() {
        String[] packageNames = new String[]{
                "com.dnake.talk"
                , "com.techjumper.polyhome.polyhomebhost"
//                , "com.techjumper.polyhome_b.bhostdaemon"
        };
        for (String packageName : packageNames) {
            PackageInfo packageInfo;
            try {
                packageInfo = Utils.appContext.getPackageManager().getPackageInfo(packageName, 0);
            } catch (PackageManager.NameNotFoundException e) {
                JLog.d(e);
                continue;
            }
            packageInfo = packageInfo == null ? createPackageInfo(packageName, -1) : packageInfo;
            mPackageInfoMap.put(packageName, packageInfo);
        }

    }

    private PackageInfo createPackageInfo(String packageName, int versionCode) {
        PackageInfo info = new PackageInfo();
        info.packageName = packageName;
        info.versionCode = versionCode;
        info.versionName = String.valueOf(versionCode);
        return info;
    }


}