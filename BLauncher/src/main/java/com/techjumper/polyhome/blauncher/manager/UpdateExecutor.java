package com.techjumper.polyhome.blauncher.manager;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.text.TextUtils;

import com.techjumper.corelib.rx.tools.CommonWrap;
import com.techjumper.corelib.utils.Utils;
import com.techjumper.corelib.utils.basic.NumberUtil;
import com.techjumper.corelib.utils.common.JLog;
import com.techjumper.corelib.utils.window.ToastUtils;
import com.techjumper.lib2.utils.RetrofitHelper;
import com.techjumper.polyhome.blauncher.R;
import com.techjumper.polyhome.blauncher.entity.UpdateAPKEntity;
import com.techjumper.polyhome.blauncher.net.KeyValueCreator;
import com.techjumper.polyhome.blauncher.net.NetHelper;
import com.techjumper.polyhome.blauncher.net.ServiceAPI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 16/6/13
 * * * * * * * * * * * * * * * * * * * * * * *
 **/
public class UpdateExecutor {

    private static UpdateExecutor INSTANCE;

    private HashMap<String, PackageInfo> mPackageInfoMap = new HashMap<>();//包名=PackageInfo

    private UpdateExecutor() {
    }

    public static UpdateExecutor getInstance() {
        if (INSTANCE == null) {
            synchronized (UpdateExecutor.class) {
                if (INSTANCE == null) {
                    INSTANCE = new UpdateExecutor();
                }
            }
        }
        return INSTANCE;
    }

    public void execute(ArrayList<PackageInfo> packageInfos) {
        execute(packageInfos, false);
    }


    public void execute(ArrayList<PackageInfo> packageInfos, boolean isHost) {

        ToastUtils.show("正在获取更新");

        mPackageInfoMap.clear();
        initPrePlugins();
        for (int i = 0; i < packageInfos.size(); i++) {
            PackageInfo packageInfo = packageInfos.get(i);
            mPackageInfoMap.put(packageInfo.packageName, packageInfo);
        }
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
            JLog.d(name);
        }

        fetchApkInfo(packageNames)
                .subscribe(new Subscriber<UpdateAPKEntity>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        JLog.e("获取插件更新失败", e);
                        ToastUtils.show(Utils.appContext.getString(R.string.error_network));
                    }

                    @Override
                    public void onNext(UpdateAPKEntity updateAPKEntity) {
                        if (!NetHelper.processNetworkResult(updateAPKEntity)) {
                            return;
                        }
                        if (updateAPKEntity.getData() == null
                                || updateAPKEntity.getData().getResult() == null
                                || updateAPKEntity.getData().getResult().size() == 0) {
                            ToastUtils.show(Utils.appContext.getString(R.string.alread_latest_version));
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
                            if (isHost) {
                                Utils.appContext.sendBroadcast(new Intent("action_host_plugin_update_complete"));
                            }
                            ToastUtils.show(Utils.appContext.getString(R.string.alread_latest_version));
                        } else {
                            Bundle bundle = new Bundle();
                            bundle.putStringArrayList(ServiceMessengerManager.KEY_EXTRA, downloadUrls);
                            ServiceMessengerManager.getInstance().send(ServiceMessengerManager.CODE_CORE_DOWNLOAD_PLUGIN
                                    , null, bundle);
                        }


                    }
                });
    }


    private void initPrePlugins() {
        String[] packageNames = new String[]{
                "com.techjumper.polyhome.b.property"
                , "pltk.com.medical"
                , "com.techjumper.polyhome.b.home"
                , "com.pltk.medicalservice"
                , "com.polyhome.sceneanddevice"
                , "com.techjumper.polyhome.blauncher"
                , "com.techjumper.polyhome.b.info"
                , "com.polyhome.smarthomeservice"};
        for (String packageName : packageNames) {
            mPackageInfoMap.put(packageName, createPackageInfo(packageName, -1));
        }

    }

    private PackageInfo createPackageInfo(String packageName, int versionCode) {
        PackageInfo info = new PackageInfo();
        info.packageName = packageName;
        info.versionCode = versionCode;
        info.versionName = String.valueOf(versionCode);
        return info;
    }

    private Observable<UpdateAPKEntity> fetchApkInfo(String[] packageNames) {
        return RetrofitHelper.<ServiceAPI>createDefault()
                .fetchAPKInfo(NetHelper.createBaseArgumentsMap(KeyValueCreator.fetchAPKInfo(packageNames)))
                .compose(CommonWrap.wrap());
    }


}