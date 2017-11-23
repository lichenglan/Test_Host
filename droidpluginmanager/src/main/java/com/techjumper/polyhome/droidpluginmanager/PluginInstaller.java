package com.techjumper.polyhome.droidpluginmanager;

import android.os.SystemClock;
import android.text.TextUtils;

import com.techjumper.corelib.utils.Utils;
import com.techjumper.corelib.utils.common.JLog;
import com.techjumper.corelib.utils.file.FileUtils;
import com.techjumper.corelib.utils.file.PreferenceUtils;
import com.techjumper.corelib.utils.system.AppUtils;
import com.techjumper.polyhome.droidpluginmanager.entity.ApkEntity;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 16/4/19
 * * * * * * * * * * * * * * * * * * * * * * *
 **/
public class PluginInstaller {

    private static PluginInstaller INSTANCE;

    public static final String KEY_FIRST_RUN = "key_first_run";

    private String mWaitInstallDir = "plugin_wait";
    private String mInstalledInstallDir = "plugin_installed";
    private String mTempInstallDir = "plugin_tmp";
    private Observable<ApkEntity> mApkEntityObservable;
    private AtomicBoolean mIsInstalling = new AtomicBoolean(false);

    private PluginInstaller() {
    }

    public static PluginInstaller getInstance() {
        if (INSTANCE == null) {
            synchronized (PluginInstaller.class) {
                if (INSTANCE == null) {
                    INSTANCE = new PluginInstaller();
                }
            }
        }
        return INSTANCE;
    }

    public Observable<ApkEntity> installAllWaitApk() {
        if (mIsInstalling.get())
            return null;

        if (!mIsInstalling.get()) {
            synchronized (PluginInstaller.class) {
                if (!mIsInstalling.get()) {
                    mIsInstalling.set(true);
                } else {
                    return null;
                }
            }
        } else {
            return null;
        }
        mApkEntityObservable = Observable
                .create((Observable.OnSubscribe<File[]>) subscriber -> {


//            File file = new File(rootDir + File.separator + AppUtils.getPackageName());
//            boolean mkResult = file.mkdirs();
//            if (!mkResult) try {
//                throw new IOException("创建插件目录失败");
//            } catch (IOException e) {
//                subscriber.onError(e);
//            }

                    String pluginRootDir = "";
                    try {
                        pluginRootDir = getPluginBaseDir();
                        initPluginDir(pluginRootDir);
                        File file = new File(getPluginWaitInstallDir());
                        File[] files = file.listFiles();
                        int count = files == null || files.length == 0 ? 0 : files.length;
                        if (count == 0) {
                            installCompelete();
                            subscriber.onCompleted();
                            return;
                        }
                        subscriber.onNext(files);
                    } catch (IOException e) {
                        subscriber.onError(e);
                    }

                    installCompelete();
                    subscriber.onCompleted();

                })
                .flatMap(Observable::from)
                .flatMap(file -> {
                    if (file == null) return null;
                    if (!Constants.POSTFIX_PACKAGE.equals(FileUtils.fetchFilePostfix(file))) {
                        FileUtils.deleteFileFromSDCard(file.getAbsolutePath());
                        return null;
                    }
                    try {
                        return installPlugin(file.getName(), false);
                    } catch (IOException e) {
                        return Observable.error(e);
                    }
                })
                .subscribeOn(Schedulers.io());
        return mApkEntityObservable;
    }

    public void deletePreDir() throws IOException {
        boolean firstRun = PreferenceUtils.getPreference().getBoolean(KEY_FIRST_RUN, true);
        if (!firstRun)
            return;

        FileUtils.deleteDir(getPluginBaseDir());
    }

    public void installCompelete() {
        mApkEntityObservable = null;
        mIsInstalling.set(false);
    }

    /**
     * 是否存在待安装的插件
     */
    public boolean hasWiatInstallPlugin() {
        String pluginRootDir = "";
        try {
            pluginRootDir = getPluginBaseDir();
            initPluginDir(pluginRootDir);
            File file = new File(getPluginWaitInstallDir());
            File[] files = file.listFiles();
            int count = files == null || files.length == 0 ? 0 : files.length;
            return count != 0;
        } catch (Exception ignored) {

        }
        return false;
    }

    public boolean isInstalling() {
//        return mIsInstalling.get();
        return false;
    }

    private Observable<ApkEntity> installPlugin(String name, boolean extraCopy) throws IOException {
        if (!Constants.POSTFIX_PACKAGE.equals(FileUtils.fetchFilePostfix(name))) {
            name += "." + Constants.POSTFIX_PACKAGE;
        }

        String pluginRootDir = getPluginBaseDir();
        initPluginDir(pluginRootDir);

        File file = new File(getPluginWaitInstallDir() + File.separator + name);
        if (!file.exists()) {
            return Observable.create(new Observable.OnSubscribe<ApkEntity>() {
                @Override
                public void call(Subscriber<? super ApkEntity> subscriber) {
                    subscriber.onNext(null);
                    subscriber.onCompleted();
                }
            });
        }

//        String path = file.getAbsolutePath();
        final String finalName = name;
        return Observable
                .create((Observable.OnSubscribe<ApkEntity>) subscriber -> {
                    try {
                        String tmpPath;
                        if (extraCopy) {
                            tmpPath = getTempInstallDir();
                            FileUtils.copyFileToOtherPath(getPluginWaitInstallDir()
                                    , finalName
                                    , tmpPath);
                            FileUtils.deleteFileIfExist(getPluginWaitInstallDir(), finalName);
                        } else {
                            tmpPath = getPluginWaitInstallDir();
                        }
                        boolean b = PluginUtils.installOrUpgrade(tmpPath + File.separator + finalName);
                        if (!b) {
                            SystemClock.sleep(50);
                            b = PluginUtils.installOrUpgrade(tmpPath + File.separator + finalName);
                            if (!b) {
                                SystemClock.sleep(50);
                                b = PluginUtils.installOrUpgrade(tmpPath + File.separator + finalName);
                            }
                        }
                        if (!b) {
                            JLog.d(finalName + "更新失败了!");
                        }
                        ApkEntity pluginEntity = new ApkEntity(Utils.appContext, tmpPath + File.separator + finalName);
//                        if (pluginEntity == null) {
//                        JLog.d(tmpPath + File.separator + finalName + " 安装失败");
                        File apkFile = new File(tmpPath + File.separator + finalName);
                        boolean isDelete = false;
                        if (apkFile.exists()) {
                            isDelete = apkFile.delete();
                        }
//                        }
//                            JLog.d("执行完" + pluginEntity.getPackageInfo().packageName + "更新并删除文件: "
//                                    + (isDelete ? "成功" : "失败"));
                        subscriber.onNext(pluginEntity);
                    } catch (Exception e) {
                        subscriber.onError(e);
                        return;
                    }
                    subscriber.onCompleted();
                })
                .subscribeOn(Schedulers.io());
    }

    public String extraNameFromUrl(String url) {
        if (TextUtils.isEmpty(url))
            return "unnamed";

        int last1 = url.lastIndexOf("/");
        if (last1 == -1) {
            return "unnamed";
        }
        if (last1 + 1 >= url.length()) {
            return "unnamed";
        }

        return url.substring(last1 + 1, url.length());
    }

    public void initPluginDir() throws IOException {
        initPluginDir(getPluginBaseDir());
    }

    public void initPluginDir(String pluginRootDir) throws IOException {
        initDir(pluginRootDir, mWaitInstallDir);
        initDir(pluginRootDir, mInstalledInstallDir);
        initDir(pluginRootDir, mTempInstallDir);
    }

    public void initDir(String pluginRootDir, String dirName) throws IOException {
        File file = new File(pluginRootDir + File.separator + dirName);
        if (!file.exists()) {
            if (!file.mkdirs()) throw new IOException("无法创建目录 " + file.getAbsolutePath());
        } else if (!file.isDirectory()) {
            if (file.delete()) {
                initDir(pluginRootDir, dirName);
            }
        }
    }

    public String getPluginWaitInstallDir() throws IOException {
        return getPluginBaseDir() + File.separator + mWaitInstallDir;
    }

    public String getPluginInstalledDir() throws IOException {
        return getPluginBaseDir() + File.separator + mInstalledInstallDir;
    }

    public String getTempInstallDir() throws IOException {
        return getPluginBaseDir() + File.separator + mTempInstallDir;
    }


    public String getPluginBaseDir() throws IOException {
        String rootDir = FileUtils.getSDCardBaseDir();
        if (TextUtils.isEmpty(rootDir))
            throw new IOException("机器没有存储设备或者没有权限");
        return rootDir + File.separator + AppUtils.getPackageName();
    }

}
