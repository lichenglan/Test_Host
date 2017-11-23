package com.techjumper.polyhome.droidpluginmanager.entity;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 16/4/19
 * * * * * * * * * * * * * * * * * * * * * * *
 **/
public class ApkEntity {
    CharSequence title;
    String versionName;
    int versionCode;
    String apkfile;
    PackageInfo packageInfo;

    public ApkEntity(Context context, String path) {
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(path, 0);
        if (info == null) return;
        Resources resources = null;
        try {
            resources = getResources(context, path);
        } catch (Exception ignored) {
        }
        try {
            if (resources != null) {
                title = resources.getString(info.applicationInfo.labelRes);
            }
        } catch (Exception e) {
            title = info.packageName;
        }

        versionName = info.versionName;
        versionCode = info.versionCode;
        apkfile = path;
        packageInfo = info;
    }

    public CharSequence getTitle() {
        return title;
    }

    public void setTitle(CharSequence title) {
        this.title = title;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getApkfile() {
        return apkfile;
    }

    public void setApkfile(String apkfile) {
        this.apkfile = apkfile;
    }

    public PackageInfo getPackageInfo() {
        return packageInfo;
    }

    public void setPackageInfo(PackageInfo packageInfo) {
        this.packageInfo = packageInfo;
    }

    //
//    ApkItem(PackageManager pm, PackageInfo info, String path) {
//        try {
//            icon = pm.getApplicationIcon(info.applicationInfo);
//        } catch (Exception e) {
//            icon = pm.getDefaultActivityIcon();
//        }
//        title = pm.getApplicationLabel(info.applicationInfo);
//        versionName = info.versionName;
//        versionCode = info.versionCode;
//        apkfile = path;
//        packageInfo = info;
//    }

    public static Resources getResources(Context context, String apkPath) throws Exception {
        String PATH_AssetManager = "android.content.res.AssetManager";
        Class assetMagCls = Class.forName(PATH_AssetManager);
        Constructor assetMagCt = assetMagCls.getConstructor((Class[]) null);
        Object assetMag = assetMagCt.newInstance((Object[]) null);
        Class[] typeArgs = new Class[1];
        typeArgs[0] = String.class;
        Method assetMag_addAssetPathMtd = assetMagCls.getDeclaredMethod("addAssetPath",
                typeArgs);
        Object[] valueArgs = new Object[1];
        valueArgs[0] = apkPath;
        assetMag_addAssetPathMtd.invoke(assetMag, valueArgs);
        Resources res = context.getResources();
        typeArgs = new Class[3];
        typeArgs[0] = assetMag.getClass();
        typeArgs[1] = res.getDisplayMetrics().getClass();
        typeArgs[2] = res.getConfiguration().getClass();
        Constructor resCt = Resources.class.getConstructor(typeArgs);
        valueArgs = new Object[3];
        valueArgs[0] = assetMag;
        valueArgs[1] = res.getDisplayMetrics();
        valueArgs[2] = res.getConfiguration();
        res = (Resources) resCt.newInstance(valueArgs);
        return res;
    }
}
