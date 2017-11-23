package com.techjumper.polyhome.blauncher.manager;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcelable;
import android.os.RemoteException;

import com.techjumper.corelib.utils.common.JLog;
import com.techjumper.polyhome.blauncher.interfaces.IServiceMessenger;
import com.techjumper.polyhome.blauncher.interfaces.IServiceReplyListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 16/5/17
 * * * * * * * * * * * * * * * * * * * * * * *
 **/
public class ServiceMessengerManager {

    public static final int CODE_CUSTOM = 999;  //自定义消息
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

    private Messenger mServiceMessenger;

    private List<IServiceMessenger> mServiceListenerList = new ArrayList<>();
    private List<IServiceReplyListener> mServiceReplyListenerList = new ArrayList<>();

    private static ServiceMessengerManager INSTANCE;

    private Messenger mHostReplyMessenger = new Messenger(new HostReplyHandler());

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mServiceMessenger = new Messenger(service);
            notifyConnect();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServiceMessenger = null;
            notifyDisconnect();
        }
    };

    private Context mContext;

    private ServiceMessengerManager() {
    }


    public void init(Context ctx) {
        mContext = ctx.getApplicationContext();
    }

    public boolean isConnected() {
        return mServiceMessenger != null;
    }

    public static ServiceMessengerManager getInstance() {
        if (INSTANCE == null) {
            synchronized (ServiceMessengerManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ServiceMessengerManager();
                }
            }
        }
        return INSTANCE;
    }

    public void registerMessenger(IServiceMessenger iServiceMessenger) {
        for (IServiceMessenger listener : mServiceListenerList) {
            if (listener == iServiceMessenger) return;
        }
        mServiceListenerList.add(iServiceMessenger);
    }

    public void unregisterMessenger(IServiceMessenger iServiceMessenger) {
        Iterator<IServiceMessenger> iterator = mServiceListenerList.iterator();
        while (iterator.hasNext()) {
            IServiceMessenger next = iterator.next();
            if (next == null || next != iServiceMessenger)
                continue;
            iterator.remove();
            return;
        }
    }

    public void registerReplyListener(IServiceReplyListener iServiceReplyListener) {
        for (IServiceReplyListener listener : mServiceReplyListenerList) {
            if (listener == iServiceReplyListener) return;
        }
        mServiceReplyListenerList.add(iServiceReplyListener);
    }

    public void unregisterReplyListener(IServiceReplyListener iServiceReplyListener) {
        Iterator<IServiceReplyListener> iterator = mServiceReplyListenerList.iterator();
        while (iterator.hasNext()) {
            IServiceReplyListener next = iterator.next();
            if (next == null || next != iServiceReplyListener)
                continue;
            iterator.remove();
            return;
        }
    }

    public void notifyReceiveReply(int code, String message, Bundle extras) {
        for (IServiceReplyListener listener : mServiceReplyListenerList) {
            if (listener != null)
                listener.onServiceReply(code, message, extras);
        }
    }

    public void notifyConnect() {
        for (IServiceMessenger listener : mServiceListenerList) {
            if (listener != null)
                listener.onServiceMessengerConnected();
        }
    }

    public void notifyDisconnect() {
        for (IServiceMessenger listener : mServiceListenerList) {
            if (listener != null)
                listener.onServiceDisconnected();
        }
    }

    public void notifyError(Throwable e) {
        for (IServiceMessenger listener : mServiceListenerList) {
            if (listener != null)
                listener.onServiceMessengerError(e);
        }
    }


    public void bind(String action, String packageName, IServiceMessenger iServiceMessenger) {
        Intent intent = new Intent(action);
        intent.setPackage(packageName);
        mContext.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        registerMessenger(iServiceMessenger);
    }

    public void unbind() {
        mServiceListenerList.clear();
        try {
            mContext.unbindService(mConnection);
        } catch (Exception ignored) {
        }
    }


    public void send(int code) {
        send(code, "");
    }

    public void send(int code, String message) {
        send(code, message, null);
    }

    public void send(int code, String message, Bundle extra) {
        if (!isConnected()) {
            return;
        }
        try {
            mServiceMessenger.send(getMessage(code, message, extra));
        } catch (RemoteException e) {
            JLog.e(e);
            notifyError(e);
        }
    }

    private Message getMessage(int code, String msg, Bundle extra) {
        Bundle data = new Bundle();
        data.putString(KEY_MESSAGE, msg);
        data.putBundle(KEY_EXTRA, extra);
        Message message = Message.obtain(null, code * 10000);
        message.setData(data);
        message.replyTo = mHostReplyMessenger;
        return message;
    }

    @SuppressLint("HandlerLeak")
    private class HostReplyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CODE_GET_PLUGIN_INFO:
                    Bundle extras = msg.getData();
                    if (extras == null) break;
                    ArrayList<Parcelable> pluginInfoList = extras.getParcelableArrayList(KEY_EXTRA);
                    if (pluginInfoList == null) {
                        JLog.d("收到Host的反馈, 但是没有内容");
                        break;
                    }
                    JLog.d("插件个数:" + pluginInfoList.size());
//                    for (Parcelable pluginParce : pluginInfoList) {
//                        if (pluginParce instanceof PackageInfo) {
//                            PackageInfo pluginInfo = (PackageInfo) pluginParce;
//                            JLog.d("packageName:" + pluginInfo.packageName + "; version:" + pluginInfo.versionName);
//                        }
//                    }
                    break;
            }
            String message = msg.getData() == null ? "" : msg.getData().getString(KEY_MESSAGE);
            JLog.d("ServiceMessengerManager 收到Host的反馈了: code=" + msg.what + "; message=" + message + "; extras=" + msg.getData());
            notifyReceiveReply(msg.what, message, msg.getData());
        }
    }
}
