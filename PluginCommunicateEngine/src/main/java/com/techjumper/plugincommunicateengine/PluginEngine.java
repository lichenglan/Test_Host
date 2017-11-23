package com.techjumper.plugincommunicateengine;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.techjumper.polyhome.blauncher.aidl.IMessageListener;
import com.techjumper.polyhome.blauncher.aidl.IPluginCommunicate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 16/6/1
 * * * * * * * * * * * * * * * * * * * * * * *
 **/
public class PluginEngine {

    /**
     * 与插件通信的code
     */
    public static final int CODE_CUSTOM = 999;  //自定义消息
    public static final int CODE_START_PLUGIN = 1;  //打开指定插件
    public static final int CODE_START_PLUGIN_ACTIVITY = 2; //打开指定插件的指定页面
    public static final int CODE_GET_PLUGIN_INFO = 3; //获取插件信息
    public static final int CODE_SAVE_INFO = 4; //保存信息到本地
    public static final int CODE_GET_SAVE_INFO = 5; //获取本地信息
    public static final int CODE_UPDATE_PLUGIN = 6; //联网更新插件
    public static final int CODE_GET_PUSH_ID = 7; //获取推送的push id


    /**
     * 取插件数据的key
     */
    public static final String KEY_MESSAGE = "key_msg";
    public static final String KEY_EXTRA = "key_extra";

    private static PluginEngine INSTANCE;

    private Context mContext;
    private List<IPluginConnection> mListenerList = new CopyOnWriteArrayList<>();
    private List<IPluginMessageReceiver> mMessageReceiveListenerList = new CopyOnWriteArrayList<>();
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private IPluginCommunicate iPluginCommunicate;
    private PluginExecutor mPluginExecutor = new PluginExecutor();
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            try {
                iPluginCommunicate = IPluginCommunicate.Stub.asInterface(service);
            } catch (Exception ignored) {
            }
            if (iPluginCommunicate == null)
                return;
            try {
                iPluginCommunicate.registerListener(iMessageListener);
            } catch (RemoteException e) {
                e.printStackTrace();
                Log.e("HIDETAG", "onServiceConnected(): ", e);
            }
            notifyEngineConnected();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            clear();
        }
    };

    private IMessageListener.Stub iMessageListener = new IMessageListener.Stub() {
        @Override
        public void onNewMessageFromPluginEngine(final int code, final String message, final Bundle extras) throws RemoteException {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    notifyPluginMessageRecieve(code, message, extras);
                }
            });
        }

    };

    private PluginEngine() {
    }

    public void init(Context context) {
        mContext = context.getApplicationContext();
    }

    public static PluginEngine getInstance() {
        if (INSTANCE == null) {
            synchronized (PluginEngine.class) {
                if (INSTANCE == null) {
                    INSTANCE = new PluginEngine();
                }
            }
        }
        return INSTANCE;
    }

    public void start(IPluginConnection iPluginConnection) {
        if (iPluginCommunicate == null) {
            registerListener(iPluginConnection);
            Intent bLauncherIntent = new Intent("com.techjumper.polyhome.blauncher.PLUGIN.SERVICE");
            bLauncherIntent.setPackage("com.techjumper.polyhome.blauncher");
            mContext.bindService(bLauncherIntent, mConnection, Context.BIND_AUTO_CREATE);
            return;
        }
        registerListener(iPluginConnection);
        notifyEngineConnected();
    }

    private void registerListener(IPluginConnection iPluginConnection) {
        for (IPluginConnection iPlugin : mListenerList) {
            if (iPlugin == iPluginConnection) return;
        }
        mListenerList.add(iPluginConnection);
    }

    public void quit() {
        mListenerList.clear();
        mMessageReceiveListenerList.clear();
        try {
            iPluginCommunicate.unregisterListener(iMessageListener);
            iPluginCommunicate = null;
            mContext.unbindService(mConnection);
        } catch (Exception ignored) {
        }
    }

    private void unregisterListener(IPluginConnection iPluginConnection) {
        mListenerList.clear();
    }


    private void notifyEngineConnected() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                ArrayList<IPluginConnection> tmpList = new ArrayList<>();
                for (IPluginConnection next : mListenerList) {
                    if (next == null) {
                        continue;
                    }
                    tmpList.add(next);
                    next.onEngineConnected(mPluginExecutor);
                }
                mListenerList.removeAll(tmpList);
            }
        });
    }

    private void notifyEngineDisconnected() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                for (IPluginConnection iPlugin : mListenerList) {
                    if (iPlugin == null) continue;
                    iPlugin.onEngineDisconnected();
                }
            }
        });
    }

    public void registerReceiver(IPluginMessageReceiver iPluginMessageReceiver) {
        for (IPluginMessageReceiver receiver : mMessageReceiveListenerList) {
            if (receiver == iPluginMessageReceiver)
                return;
        }
        mMessageReceiveListenerList.add(iPluginMessageReceiver);
    }

    public void unregisterReceiver(IPluginMessageReceiver iPluginMessageReceiver) {
        int index = -1;
        for (int i = 0; i < mMessageReceiveListenerList.size(); i++) {
            IPluginMessageReceiver receiver = mMessageReceiveListenerList.get(i);
            if (receiver == iPluginMessageReceiver) {
                index = i;
                break;
            }
        }
        if (index != -1)
            mMessageReceiveListenerList.remove(index);
    }

    private void notifyPluginMessageRecieve(int code, String message, Bundle extras) {
        for (IPluginMessageReceiver next : mMessageReceiveListenerList) {
            if (next == null) {
                continue;
            }
            next.onPluginMessageReceive(code, message, extras);
        }
    }

    private IPluginCommunicate getPluginCommunicate() {
        return iPluginCommunicate;
    }

    public interface IPluginConnection {
        void onEngineConnected(PluginExecutor pluginExecutor);

        void onEngineDisconnected();
    }

    private void clear() {
        iPluginCommunicate = null;
        notifyEngineDisconnected();
    }

    public class PluginExecutor {

        private PluginExecutor() {
        }

        private void AssertPlugin() throws Exception {
            if (getPluginCommunicate() == null)
                throw new Exception("未连接到核心组件");
        }


        public void send(final int code) throws Exception {
            AssertPlugin();
            try {
                getPluginCommunicate().sendMessage(code, "");
            } catch (Exception e) {
                clear();
                start(new IPluginConnection() {
                    @Override
                    public void onEngineConnected(PluginExecutor pluginExecutor) {
                        try {
                            getPluginCommunicate().sendMessage(code, "");
                        } catch (Exception ignored) {
                            Toast.makeText(mContext, "远程服务断开连接", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onEngineDisconnected() {
                        Toast.makeText(mContext, "远程服务断开连接", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

        public void send(final int code, final String message) throws Exception {
            AssertPlugin();
            try {
                getPluginCommunicate().sendMessage(code, message);
            } catch (Exception e) {
                clear();
                start(new IPluginConnection() {
                    @Override
                    public void onEngineConnected(PluginExecutor pluginExecutor) {
                        try {
                            getPluginCommunicate().sendMessage(code, message);
                        } catch (Exception ignored) {
                            Toast.makeText(mContext, "远程服务断开连接", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onEngineDisconnected() {
                        Toast.makeText(mContext, "远程服务断开连接", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

        public void send(final int code, final String message, final Bundle extras) throws Exception {
            AssertPlugin();
            try {
                getPluginCommunicate().sendMessageWithExtras(code, message, extras);
            } catch (Exception e) {
                clear();
                start(new IPluginConnection() {
                    @Override
                    public void onEngineConnected(PluginExecutor pluginExecutor) {
                        try {
                            getPluginCommunicate().sendMessageWithExtras(code, message, extras);
                        } catch (Exception ignored) {
                            Toast.makeText(mContext, "远程服务断开连接", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onEngineDisconnected() {
                        Toast.makeText(mContext, "远程服务断开连接", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

    }

}
