package com.techjumper.polyhome.polyhomebhost.tools;

import android.content.pm.PackageInfo;
import android.text.TextUtils;

import com.techjumper.corelib.rx.tools.CommonWrap;
import com.techjumper.corelib.utils.Utils;
import com.techjumper.corelib.utils.basic.NumberUtil;
import com.techjumper.corelib.utils.common.JLog;
import com.techjumper.corelib.utils.window.ToastUtils;
import com.techjumper.lib2.utils.RetrofitHelper;
import com.techjumper.polyhome.polyhomebhost.R;
import com.techjumper.polyhome.polyhomebhost.by_function.log.LogUtils;
import com.techjumper.polyhome.polyhomebhost.entity.APKListEntity;
import com.techjumper.polyhome.polyhomebhost.entity.UpdateAPKEntity;
import com.techjumper.polyhome.polyhomebhost.net.KeyValueCreator;
import com.techjumper.polyhome.polyhomebhost.net.NetHelper;
import com.techjumper.polyhome.polyhomebhost.net.ServiceAPI;

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
public class UpdateChecker {

    private static UpdateChecker INSTANCE;

    /**
     * 插件列表的配置名字
     */
    public static final String PLUGIN_LIST_ASSET_NAME = "plugin_list";

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
        void onPluginListReceive(ArrayList<String> downloadUrls);

        void onPluginAlreadyLatest(boolean isHost);

        void onError(Throwable e);
    }


    public void execute(ArrayList<PackageInfo> packageInfos, String id, boolean isHost, IUpdateChecker iUpdateChecker) {

        this.iUpdateChecker = iUpdateChecker;
        ToastUtils.show("正在获取更新");

        fetchApkList(id + "")
                .map(apkListEntity -> {
                    mPackageInfoMap.clear();
                    initPrePlugins(apkListEntity);
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
                        PackageInfo packageInfo = mPackageInfoMap.get(name);
                        int versionCode = packageInfo == null ? -1 : packageInfo.versionCode;
                        JLog.d(name + ":" + versionCode);
                        LogUtils.insertLog(name + ":" + versionCode);
                    }
                    return packageNames;
                })
                .flatMap(packages -> fetchApkInfo(packages, id))
                .subscribe(new Subscriber<UpdateAPKEntity>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        JLog.e("获取插件更新失败", e);
                        ToastUtils.show(Utils.appContext.getString(R.string.error_to_connect_server));
                        LogUtils.insertLog("检查更新发生错误：" + e);
                        if (iUpdateChecker != null) {
                            iUpdateChecker.onError(e);
                        }
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
                            LogUtils.insertLog(Utils.appContext.getString(R.string.alread_latest_version));
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
                                LogUtils.insertLog("发现更新: " + localApk.packageName + ", 本地版本:" + localApk.versionCode + ", 服务器版本:" + serverApk.getVersion());
                            }
                        }

                        if (downloadUrls.size() == 0) {
//                            if (isHost) {
//                                Utils.appContext.sendBroadcast(new Intent("action_host_plugin_update_complete"));
//                            }
                            if (iUpdateChecker != null) {
                                iUpdateChecker.onPluginAlreadyLatest(isHost);
                            }
                            ToastUtils.show(Utils.appContext.getString(R.string.alread_latest_version));
                            LogUtils.insertLog(Utils.appContext.getString(R.string.alread_latest_version));
                        } else {
                            if (iUpdateChecker != null) {
                                iUpdateChecker.onPluginListReceive(downloadUrls);
                            }
//                            Bundle bundle = new Bundle();
//                            bundle.putStringArrayList(ServiceMessengerManager.KEY_EXTRA, downloadUrls);
//                            ServiceMessengerManager.getInstance().send(ServiceMessengerManager.CODE_CORE_DOWNLOAD_PLUGIN
//                                    , null, bundle);
                        }


                    }
                });
    }


    private void initPrePlugins(APKListEntity apkListEntity) {
//        SaveInfoEntity saveInfoEntity = GsonUtils.fromJson(config, SaveInfoEntity.class);
//        SaveInfoEntity.DataEntity data = saveInfoEntity.getData();
//        if (data == null)
//            return;
//        HashMap<String, String> values = data.getValues();
//        if (values == null)
//            return;
//        if (values.get(PLUGIN_LIST_ASSET_NAME) == null)
//            return;
//
//        String packageJson = values.get(PLUGIN_LIST_ASSET_NAME);
//        String[] packageNames = GsonUtils.fromJson(packageJson, String[].class);
//
//        if (packageNames == null || packageNames.length == 0)
//            return;
//        JLog.d("长度: " + packageNames.length + ", 插件包名配置:" + Arrays.toString(packageNames));
//        String[] packageNames = new String[]{
//                "com.techjumper.polyhome.b.property"
//                , "pltk.com.medical"
//                , "com.techjumper.polyhome.b.home"
//                , "com.pltk.medicalservice"
//                , "com.polyhome.sceneanddevice"
//                , "com.techjumper.polyhome.blauncher"
//                , "com.techjumper.polyhome.b.info"
//                , "com.polyhome.smarthomeservice"};
        if (!NetHelper.isSuccess(apkListEntity) || apkListEntity.getData() == null) {
            JLog.e("获取网络插件列表失败");
            return;
        }
        List<String> packages = apkListEntity.getData().getPackages();
        if (packages == null || packages.size() == 0) {
            JLog.e("获取网络插件列表失败, packages 为 null");
            return;
        }
        for (String packageName : packages) {
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

    private Observable<UpdateAPKEntity> fetchApkInfo(String[] packageNames, String family_id) {
        return RetrofitHelper.<ServiceAPI>createDefault()
                .fetchAPKInfo(NetHelper.createBaseArgumentsMap(KeyValueCreator.fetchAPKInfo(packageNames, family_id)))
                .compose(CommonWrap.wrap());
    }

    private Observable<APKListEntity> fetchApkList(String family_id) {
        return RetrofitHelper.<ServiceAPI>createDefault()
                .fetchAPKList(NetHelper.createBaseArgumentsMap(KeyValueCreator.fetchAPKList(family_id)))
                .compose(CommonWrap.wrap());
    }


}