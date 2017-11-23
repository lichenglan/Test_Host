package com.techjumper.hdownloadmanager;

import android.app.DownloadManager;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 16/6/12
 * * * * * * * * * * * * * * * * * * * * * * *
 **/
public class HDownloader {

    public static final String DOWNLOAD_SUB_PATH = "h_tmp";
    public static final String MIME_TYPE_APK = "application/vnd.android.package-archive";

    private static final String SP_NAME = "hdownload";

    private static HDownloader sSelf;
    private static Context mAppContext;
    private Handler mHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            return true;
        }
    });
    private DownloadObservable mDownloadObservable = new DownloadObservable(mHandler);

    private DownloadManager mDM;
    private DownloadManagerHelper mDMH;
    private ArrayList<IHDownload> mListeners = new ArrayList<>();

    private HDownloader() {
        mDM = (DownloadManager) mAppContext.getSystemService(Context.DOWNLOAD_SERVICE);
        mDMH = new DownloadManagerHelper(mDM);
        mAppContext.getContentResolver().registerContentObserver(DownloadManagerHelper.CONTENT_URI, true,
                mDownloadObservable);
    }

    public static HDownloader getInstance() {
        if (sSelf == null) {
            synchronized (HDownloader.class) {
                if (sSelf == null) {
                    sSelf = new HDownloader();
                }
            }
        }
        return sSelf;
    }

    public static void init(Context context) {
        mAppContext = context.getApplicationContext();
    }

    public long downloadApk(String url) {
        return downloadApk(url, extraNameFromUrl(url));
    }

    public long downloadApk(String url, String name) {
        return donwload(url, name, MIME_TYPE_APK);
    }

    public long donwload(String url, String name, String mimeType) {
        DownloadManager.Request request;
        try {
            request = new DownloadManager.Request(Uri.parse(url));
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("HIDETAG", "出错了: " + e);
            return -1;
        }
        getDownloadPath();
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, DOWNLOAD_SUB_PATH + File.separator + name);
        request.setTitle("正在下载 " + name);
        request.setDescription(url);
//        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
//        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        request.setMimeType(mimeType);
        //        putIdToLocal(url, downloadId);
        return mDM.enqueue(request);
    }

//    public void pause(String url)

//        pause(getIdByUrl(url));
//    }


    public void pause(long id) {
        mDMH.pauseDownload(id);
    }

//    public void resume(String url) {
//        resume(getIdByUrl(url));
//    }

    public void resume(long id) {
        mDMH.resumeDownload(id);
    }

    public void reboot() {
        quit();
        mAppContext.getContentResolver().registerContentObserver(DownloadManagerHelper.CONTENT_URI, true,
                mDownloadObservable);
    }

    public void quit() {
        mAppContext.getContentResolver().unregisterContentObserver(mDownloadObservable);
    }

    public void registerChange(IHDownload iHDownload) {
        reboot();
        for (IHDownload l : mListeners) {
            if (l == iHDownload)
                return;
        }
        mListeners.add(iHDownload);

    }

    public void unregisterChange(IHDownload iHDownload) {
        Iterator<IHDownload> it = mListeners.iterator();
        while (it.hasNext()) {
            IHDownload next = it.next();
            if (next == iHDownload) {
                it.remove();
                return;
            }
        }
    }

    public void notifyChange(boolean selfChange, Uri uri, String url, String name, long downloadId, int current, int total, int status) {
        for (IHDownload l : mListeners) {
            l.onDownloadChange(selfChange, uri, url, name, downloadId, current, total, status);
        }
    }

//    private SharedPreferences getSp() {
//        return mAppContext.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
//    }
//
//    private boolean putIdToLocal(String url, long downloadId) {
//        return getSp().edit().putLong(StringUtils.md5(url), downloadId).commit();
//    }
//
//    public long getIdByUrl(String url) {
//        return getSp().getLong(StringUtils.md5(url), -1);
//    }
//
//    private boolean removeLocalId(String url) {
//        return getSp().edit().remove(StringUtils.md5(url)).commit();
//    }

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

    public String getDownloadPath() {
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                + File.separator + DOWNLOAD_SUB_PATH + File.separator;
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return path;
    }

    private class DownloadObservable extends ContentObserver {

        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public DownloadObservable(Handler handler) {
            super(handler);
        }


        @Override
        public void onChange(final boolean selfChange, final Uri uri) {
//            long downloadId = getIdByUrl(uri.toString());
            ExecutorManager.eventExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    long downloadId = -1;
                    try {
                        downloadId = Long.parseLong(uri.getLastPathSegment());
                    } catch (Exception ignored) {
                    }

                    String url = mDMH.getUrl(downloadId);
                    String path = mDMH.getFileName(downloadId);

                    int[] bytesAndStatus = mDMH.getBytesAndStatus(downloadId);
                    notifyChange(selfChange, uri, url, path, downloadId, bytesAndStatus[0], bytesAndStatus[1], bytesAndStatus[2]);
                }
            });

        }
//            if (bytesAndStatus[2] == DownloadManager.STATUS_FAILED
//                    || bytesAndStatus[2] == DownloadManager.STATUS_SUCCESSFUL) {
//                removeLocalId(uri.toString());
//            }
    }

    public interface IHDownload {
        void onDownloadChange(boolean selfChange, Uri uri, String url, String path, long downloadId, int current, int total, int status);
    }
}

