package com.techjumper.polyhome.polyhomebhost.tools;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.techjumper.corelib.utils.Utils;
import com.techjumper.corelib.utils.common.JLog;
import com.techjumper.corelib.utils.file.FileUtils;
import com.techjumper.hdownloadmanager.DownloadManagerHelper;
import com.techjumper.hdownloadmanager.HDownloader;
import com.techjumper.polyhome.polyhomebhost.Config;
import com.techjumper.polyhome.polyhomebhost.by_function.log.LogUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 16/6/13
 * * * * * * * * * * * * * * * * * * * * * * *
 **/
public class UpdateExecutor implements HDownloader.IHDownload {

    private static UpdateExecutor INSTANCE;

    private HashMap<Long, String> mDownloadIds = new HashMap<>();//下载id=下载地址

    private String mWaitInstallDir = "plugin_wait";
    private IUpdate iUpdate;

    private UpdateExecutor() {
        HDownloader.init(Utils.appContext);
        HDownloader.getInstance().registerChange(this);
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

    public void execute(ArrayList<String> urls, IUpdate iUpdate) {
        this.iUpdate = iUpdate;

        //清空之前所有的下载
        new DownloadManagerHelper((DownloadManager) Utils.appContext.getSystemService(Context.DOWNLOAD_SERVICE))
                .removeAll(urls);
        mDownloadIds.clear();

        for (String url : urls) {
            String downloadUrl = Config.sStatic + url;
            if (alreadyHasUpdate(downloadUrl))
                continue;
            long downloadId = HDownloader.getInstance().downloadApk(downloadUrl);
            JLog.d("正在下载: " + downloadUrl);
            LogUtils.insertLog("正在下载: " + downloadUrl);
            mDownloadIds.put(downloadId, downloadUrl);

        }
    }

    private boolean alreadyHasUpdate(String url) {
        if (TextUtils.isEmpty(url))
            return false;
        for (Map.Entry<Long, String> next : mDownloadIds.entrySet()) {
            if (next == null) continue;
            if (url.equalsIgnoreCase(next.getValue())) {
                JLog.d("此文件已经在下载, 不用重复执行: " + url);
                return true;
            }
        }
        return false;
    }

    @Override
    public void onDownloadChange(boolean selfChange, Uri uri, String url, String path, long downloadId, int current, int total, int status) {
        if (!mDownloadIds.containsKey(downloadId))
            return;
//        String name = mDownloadIds.get(downloadId);
        String name = HDownloader.getInstance().extraNameFromUrl(path);
        if (status == DownloadManager.STATUS_SUCCESSFUL
                && !TextUtils.isEmpty(name)
                && mDownloadIds.get(downloadId) != null) {
            Observable
                    .create(subscriber -> {
                        mDownloadIds.remove(downloadId);
                        JLog.d("下载 " + name + "成功, 剩余插件:" + mDownloadIds.size() + "个");
                        LogUtils.insertLog("下载 " + name + "成功, 剩余插件:" + mDownloadIds.size() + "个");
                        try {
                            FileUtils.copyFileToOtherPath(HDownloader.getInstance().getDownloadPath()
                                    , HDownloader.getInstance().extraNameFromUrl(path)
                                    , getPluginWaitInstallDir());
                            FileUtils.deleteFileFromSDCard(HDownloader.getInstance().getDownloadPath() + HDownloader.getInstance().extraNameFromUrl(path));
//                            JLog.d("拷贝 " + name + "到安装目录完成, 剩余插件数量: " + mDownloadIds.size());
                            if (iUpdate != null) {
                                iUpdate.onPluginDownloaded(getPluginWaitInstallDir() + File.separator + HDownloader.getInstance().extraNameFromUrl(path));
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            subscriber.onError(e);
                            return;
                        }
                        subscriber.onNext(null);
                        subscriber.onCompleted();

                    })
                    .subscribeOn(Schedulers.io())
                    .subscribe();
        } else if (status == DownloadManager.STATUS_FAILED) {
            JLog.d("下载 " + name + " 失败");
            LogUtils.insertLog("下载 " + name + " 失败");
            mDownloadIds.remove(downloadId);
        }
    }

    public interface IUpdate {
        void onPluginDownloaded(String donwloadedPath);
    }

    public String getPluginWaitInstallDir() throws IOException {
        return getPluginBaseDir() + File.separator + mWaitInstallDir;
    }

    public String getPluginBaseDir() throws IOException {
        String rootDir = FileUtils.getSDCardBaseDir();
        if (TextUtils.isEmpty(rootDir)) {
            LogUtils.insertLog("下载插件失败：机器没有存储设备或者没有权限");
            throw new IOException("机器没有存储设备或者没有权限");
        }
        return rootDir + File.separator + "com.techjumper.polyhome.polyhomebhost";
    }

}