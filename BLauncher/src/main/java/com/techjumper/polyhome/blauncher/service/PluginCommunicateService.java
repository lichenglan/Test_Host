package com.techjumper.polyhome.blauncher.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.techjumper.corelib.rx.tools.RxBus;
import com.techjumper.corelib.utils.common.JLog;
import com.techjumper.polyhome.blauncher.aidl.IMessageListener;
import com.techjumper.polyhome.blauncher.aidl.IPluginCommunicate;
import com.techjumper.polyhome.blauncher.entity.event.PluginInfoReceiveEvent;
import com.techjumper.polyhome.blauncher.interfaces.IServiceReplyListener;
import com.techjumper.polyhome.blauncher.manager.ServiceMessengerManager;
import com.techjumper.polyhome.blauncher.manager.UpdateExecutor;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 16/6/1
 * * * * * * * * * * * * * * * * * * * * * * *
 **/
public class PluginCommunicateService extends Service implements IServiceReplyListener {

    private boolean mIsWaitUpdate;
    private AtomicBoolean mIsBroadcasting = new AtomicBoolean(false);

    private RemoteCallbackList<IMessageListener> mMsgListenerList = new RemoteCallbackList<>();
    private Binder mBinder = new IPluginCommunicate.Stub() {
        @Override
        public void registerListener(IMessageListener listener) throws RemoteException {
            mMsgListenerList.register(listener);
            try {
                mIsBroadcasting.set(true);
                int count = mMsgListenerList.beginBroadcast();
                mMsgListenerList.finishBroadcast();
                JLog.d("有客户端注册了, count=" + count);
            } catch (Exception ignored) {
            }finally {
                mIsBroadcasting.set(false);
            }
        }

        @Override
        public void unregisterListener(IMessageListener listener) throws RemoteException {
            mMsgListenerList.unregister(listener);

            try {
                mIsBroadcasting.set(true);
                int count = mMsgListenerList.beginBroadcast();
                mMsgListenerList.finishBroadcast();
                JLog.d("有客户端取消注册了, count=" + count);
            } catch (Exception ignored) {
            }finally {
                mIsBroadcasting.set(false);
            }

        }

        @Override
        public void sendMessage(int code, String message) throws RemoteException {
            JLog.d("有客户端发送了消息: code=" + code + "; message=" + message);
            switch (code) {
//                case ServiceMessengerManager.CODE_UPDATE_PLUGIN:
//                    mIsWaitUpdate = true;
//                    ServiceMessengerManager.getInstance().send(ServiceMessengerManager.CODE_GET_PLUGIN_INFO);
//                    break;
                case ServiceMessengerManager.CODE_CUSTOM:
                    notifyMessageToClient(code, message, null);
                    break;
                default:
                    ServiceMessengerManager.getInstance().send(code, message);
                    break;
            }
        }

        @Override
        public void sendMessageWithExtras(int code, String message, Bundle extras) throws RemoteException {
            JLog.d("有客户端发送了消息: code=" + code + "; message=" + message + "; extras=" + extras);
            switch (code) {
                case ServiceMessengerManager.CODE_CUSTOM:
                    notifyMessageToClient(code, message, extras);
                    break;
                default:
                    ServiceMessengerManager.getInstance().send(code, message, extras);
                    break;
            }
        }

    };

    private BroadcastReceiver mQuitReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            JLog.d("PluginCommunicateService收到退出广播");
            stopSelf();
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mIsWaitUpdate = false;
        ServiceMessengerManager.getInstance().registerReplyListener(this);
        IntentFilter filter = new IntentFilter(BLauncherService.PLUGIN_QUIT);
        registerReceiver(mQuitReceiver, filter);
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mQuitReceiver);
        ServiceMessengerManager.getInstance().unregisterReplyListener(this);
        JLog.d("PluginCommunicateService退出了");
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public synchronized void notifyMessageToClient(int code, String message, Bundle extras) {
        if (mIsBroadcasting.get()) {
            try {
                wait();
            } catch (InterruptedException e) {
                JLog.d(e);
            }
        }
        mIsBroadcasting.set(true);
        int count = mMsgListenerList.beginBroadcast();
        for (int i = 0; i < count; i++) {
            IMessageListener listener = mMsgListenerList.getBroadcastItem(i);
            try {
                listener.onNewMessageFromPluginEngine(code, message, extras);
            } catch (RemoteException e) {
                e.printStackTrace();
                JLog.e("通信失败: " + e);
            }
        }
        mMsgListenerList.finishBroadcast();
        mIsBroadcasting.set(false);
        notify();
    }

    @Override
    public void onServiceReply(int code, String message, Bundle extras) {

        if (mIsWaitUpdate && extras != null && code == ServiceMessengerManager.CODE_GET_PLUGIN_INFO) {
            ArrayList<Parcelable> parcelableArrayList = extras.getParcelableArrayList(ServiceMessengerManager.KEY_EXTRA);
            if (parcelableArrayList == null)
                return;
            if (parcelableArrayList.size() == 0) {
                mIsWaitUpdate = false;
                return;
            }
            ArrayList<PackageInfo> packageInfos = new ArrayList<>();
            for (Parcelable parcelable : parcelableArrayList) {
                if (parcelable == null)
                    continue;
                if (!(parcelable instanceof PackageInfo))
                    continue;
                PackageInfo packageInfo = (PackageInfo) parcelable;
                packageInfos.add(packageInfo);
            }
            mIsWaitUpdate = false;
            UpdateExecutor.getInstance().execute(packageInfos);
        } else if (ServiceMessengerManager.CODE_GET_PLUGIN_INFO == code && extras != null) {
            ArrayList<Parcelable> parcelableArrayList = extras.getParcelableArrayList(ServiceMessengerManager.KEY_EXTRA);
            if (parcelableArrayList == null || parcelableArrayList.size() == 0)
                return;
            ArrayList<PackageInfo> packageInfos = new ArrayList<>();
            for (Parcelable parcelable : parcelableArrayList) {
                if (parcelable == null)
                    continue;
                if (!(parcelable instanceof PackageInfo))
                    continue;
                PackageInfo packageInfo = (PackageInfo) parcelable;
                packageInfos.add(packageInfo);
            }
            RxBus.INSTANCE.send(new PluginInfoReceiveEvent(packageInfos));
            notifyMessageToClient(code, message, extras);
        } else {
            notifyMessageToClient(code, message, extras);
        }
    }
}
