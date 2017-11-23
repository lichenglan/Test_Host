package com.techjumper.polyhome.droidpluginmanager;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.RemoteException;

import com.morgoo.droidplugin.pm.PluginManager;
import com.morgoo.helper.compat.PackageManagerCompat;
import com.techjumper.corelib.utils.Utils;
import com.techjumper.corelib.utils.common.JLog;
import com.techjumper.polyhome.droidpluginmanager.entity.ApkEntity;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 16/4/19
 * * * * * * * * * * * * * * * * * * * * * * *
 **/
public class PluginUtils {

    public static boolean isInstalled(String path) throws RemoteException {
        return getInstalledPackageInfo(path) != null;
    }

    public static PackageInfo getInstalledPackageInfo(String path) throws RemoteException {
        return PluginManager.getInstance().getPackageInfo(getPackageName(path), 0);
    }

    public static String getPackageName(String path) {
        ApkEntity entity = new ApkEntity(Utils.appContext, path);
        PackageInfo packageInfo = entity.getPackageInfo();
        return packageInfo == null ? "" : packageInfo.packageName;
    }

    public static boolean hasUpdate(String sourcePath, String targetPath) {
        ApkEntity sourceEntity = new ApkEntity(Utils.appContext, sourcePath);
        ApkEntity targetEntity = new ApkEntity(Utils.appContext, targetPath);
        return sourceEntity.getVersionCode() > targetEntity.getVersionCode();
    }

    public static boolean hasUpdate(String sourcePath) throws RemoteException {
        PackageInfo info = getInstalledPackageInfo(sourcePath);
        if (info == null) return true;

        ApkEntity sourceEntity = new ApkEntity(Utils.appContext, sourcePath);
        return sourceEntity.getVersionCode() > info.versionCode;
    }

    public static synchronized boolean installOrUpgrade(String path) throws RemoteException {
        int flag = isInstalled(path) ? PackageManagerCompat.INSTALL_REPLACE_EXISTING : 0;
        JLog.d(path + " " + (flag == 0 ? "正在全新安装" : "正在替换安装"));
        int code = PluginManager.INSTALL_FAILED_NO_REQUESTEDPERMISSION;
        try {
            code = PluginManager.getInstance().installPackage(path, flag);
        } catch (Exception ignored) {
        }
        return code != PluginManager.INSTALL_FAILED_NO_REQUESTEDPERMISSION;
    }

    public static void startPluginLauncherActivity(Context ctx, String packageName)
            throws Exception {
        startPluginLauncherActivity(ctx, packageName, null);
    }


    public static void startPluginLauncherActivity(Context ctx, String packageName, Bundle extra)
            throws Exception {
        if (PluginManager.getInstance().getPackageInfo(packageName, 0) == null) {
            throw new ClassNotFoundException("没有找到包名为 " + packageName + " 的插件");
        }

        try {
            PackageManager pm = ctx.getPackageManager();
            Intent intent = pm.getLaunchIntentForPackage(packageName);
            if (extra != null) {
                intent.putExtras(extra);
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(intent);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public static void startPluginActivity(Context ctx, String packageName, String fullActivityName, Bundle extra)
            throws Exception {
        if (PluginManager.getInstance().getPackageInfo(packageName, 0) == null) {
            throw new ClassNotFoundException("没有找到包名为 " + packageName + " 的插件");
        }

        try {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName(packageName, fullActivityName));
            if (extra != null) {
                intent.putExtras(extra);
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(intent);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

//    public static void startPluginService(Activity ac, String packageName, String serviceFullName)
//            throws ClassNotFoundException, RemoteException {
//        if (PluginManager.getInstance().getPackageInfo(packageName, 0) == null) {
//            throw new ClassNotFoundException("没有找到包名为 " + packageName + " 的插件");
//        }
//
//        Intent targetIntent = new Intent();
//        ComponentName cn = new ComponentName(packageName, serviceFullName);
//        targetIntent.setComponent(cn);
//        ac.startService(targetIntent);
//    }
}
