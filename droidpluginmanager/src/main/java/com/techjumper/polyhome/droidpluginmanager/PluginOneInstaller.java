package com.techjumper.polyhome.droidpluginmanager;

import android.os.SystemClock;
import android.text.TextUtils;

import com.techjumper.corelib.utils.Utils;
import com.techjumper.corelib.utils.common.JLog;
import com.techjumper.corelib.utils.file.FileUtils;
import com.techjumper.corelib.utils.system.AppUtils;
import com.techjumper.polyhome.droidpluginmanager.entity.ApkEntity;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import rx.Observable;
import rx.schedulers.Schedulers;

public class PluginOneInstaller {

    private String mWaitInstallDir = "plugin_wait";
    private String mInstalledInstallDir = "plugin_installed";
    private String mTempInstallDir = "plugin_tmp";

    private IApkInstall iApkInstall;
    private AtomicBoolean mIsInstalling = new AtomicBoolean(false);
    private BlockingQueue<String> mQueue = new LinkedBlockingQueue<>();

    public PluginOneInstaller() {
    }


    public void install(String name, IApkInstall iApkInstall) throws InterruptedException, IOException {
        this.iApkInstall = iApkInstall;
        mQueue.add(name);
        if (!mIsInstalling.get()) {
            mIsInstalling.set(true);
            installPlugin();
        }
    }

    private void installPlugin() throws InterruptedException, IOException {
        if (!mQueue.isEmpty()) {
            String name = mQueue.take();
            installPlugin(name, false);
        }
    }


    private void installPlugin(String name, boolean extraCopy) throws IOException {
        if (!Constants.POSTFIX_PACKAGE.equals(FileUtils.fetchFilePostfix(name))) {
            name += "." + Constants.POSTFIX_PACKAGE;
        }

        String pluginRootDir = getPluginBaseDir();
        initPluginDir(pluginRootDir);

        File file = new File(getPluginWaitInstallDir() + File.separator + name);
        if (!file.exists()) {
            continueInstall();
            return;
        }


//        String path = file.getAbsolutePath();
        final String finalName = name;
        Observable
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
                        String fullName = tmpPath + File.separator + finalName;
                        long tmpTime = System.currentTimeMillis();
                        boolean b = PluginUtils.installOrUpgrade(fullName);
                        tmpTime = System.currentTimeMillis() - tmpTime;
                        if (!b || tmpTime < 500) {
                            SystemClock.sleep(1000);

                            tmpTime = System.currentTimeMillis();
                            b = PluginUtils.installOrUpgrade(fullName);
                            tmpTime = System.currentTimeMillis() - tmpTime;
                            if (!b || tmpTime < 500) {
                                SystemClock.sleep(1000);
                                b = PluginUtils.installOrUpgrade(fullName);
                            }
                        }
                        if (!b) {
                            JLog.d(finalName + "更新失败了!");
                        }
                        ApkEntity pluginEntity = new ApkEntity(Utils.appContext, fullName);
//                        if (pluginEntity == null) {
//                        JLog.d(fullName + " 安装失败");
                        File apkFile = new File(fullName);
//                        boolean isDelete = false;
                        if (apkFile.exists()) {
//                            isDelete = apkFile.delete();
                            apkFile.delete();
                        }
//                        }
                        subscriber.onNext(pluginEntity);
                    } catch (Exception e) {
                        subscriber.onError(e);
                        return;
                    }
                    subscriber.onCompleted();
                })
                .subscribeOn(Schedulers.io())
                .subscribe(apkEntity -> {
                    continueInstall();
                    if (iApkInstall != null) {
                        iApkInstall.onApkInstalled(apkEntity);
                    }
                });
    }

    private void continueInstall() {
        if (mQueue.isEmpty()) {
            mIsInstalling.set(false);
            return;
        }
        Utils.mainHandler.postDelayed(() -> {
            try {
                installPlugin();
            } catch (InterruptedException | IOException ignored) {
            }
        }, 100);
    }

    public interface IApkInstall {
        void onApkInstalled(ApkEntity apkEntity);
    }

    private void initPluginDir() throws IOException {
        initPluginDir(getPluginBaseDir());
    }

    private void initPluginDir(String pluginRootDir) throws IOException {
        initDir(pluginRootDir, mWaitInstallDir);
        initDir(pluginRootDir, mInstalledInstallDir);
        initDir(pluginRootDir, mTempInstallDir);
    }

    private void initDir(String pluginRootDir, String dirName) throws IOException {
        File file = new File(pluginRootDir + File.separator + dirName);
        if (!file.exists()) {
            if (!file.mkdirs()) throw new IOException("无法创建目录 " + file.getAbsolutePath());
        } else if (!file.isDirectory()) {
            if (file.delete()) {
                initDir(pluginRootDir, dirName);
            }
        }
    }

    private String getPluginWaitInstallDir() throws IOException {
        return getPluginBaseDir() + File.separator + mWaitInstallDir;
    }

    private String getPluginInstalledDir() throws IOException {
        return getPluginBaseDir() + File.separator + mInstalledInstallDir;
    }

    private String getTempInstallDir() throws IOException {
        return getPluginBaseDir() + File.separator + mTempInstallDir;
    }


    private String getPluginBaseDir() throws IOException {
        String rootDir = FileUtils.getSDCardBaseDir();
        if (TextUtils.isEmpty(rootDir))
            throw new IOException("机器没有存储设备或者没有权限");
        return rootDir + File.separator + AppUtils.getPackageName();
    }
}