package com.techjumper.polyhome.polyhomebhost.jpush;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.techjumper.corelib.utils.common.JLog;
import com.techjumper.corelib.utils.file.PreferenceUtils;
import com.techjumper.corelib.utils.window.ToastUtils;

import cn.jpush.android.api.JPushInterface;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 16/5/21
 * * * * * * * * * * * * * * * * * * * * * * *
 **/
public class JPushReceiver extends BroadcastReceiver {

    public static final String SP_NAME = "jpush_receive";
    public static final String KEY_ID = "id";

    public static String ACTION_CUSTOM_MESSAGE_RECEIVE = "action_push_receive";
    public static String KEY_EXTRA = "key_extra";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
//            Log.d(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
            if (bundle == null) return;
            String data = bundle.getString(JPushInterface.EXTRA_MESSAGE);
            if (TextUtils.isEmpty(data)) return;
            if (BuildConfig.DEBUG) {
                ToastUtils.showLong("云端推送了一条消息: \n" + data);
            }
            Intent targetIntent = new Intent(ACTION_CUSTOM_MESSAGE_RECEIVE);
            targetIntent.putExtra(KEY_EXTRA, data);
            context.sendBroadcast(targetIntent);
            JLog.d("推送消息来啦: " + data);

        } else if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            JLog.d("获取到推送ID: " + regId);
            PreferenceUtils.getPreference(SP_NAME).edit().putString(KEY_ID, regId).apply();
        }
    }
}
