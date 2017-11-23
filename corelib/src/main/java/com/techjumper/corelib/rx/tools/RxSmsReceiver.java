package com.techjumper.corelib.rx.tools;

import android.content.ContentValues;
import android.content.Context;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

import com.f2prateek.rx.receivers.RxBroadcastReceiver;
import com.techjumper.corelib.BuildConfig;
import com.techjumper.corelib.utils.common.JLog;

import java.util.List;

import rx.Observable;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 16/3/6
 * * * * * * * * * * * * * * * * * * * * * * *
 **/
public class RxSmsReceiver {

    private static final String TAG = "RxSmsReceiver";

    public static final String KEY_MESSAGE = "key_message";
    public static final String KEY_FROM_ADDRESS = "key_from_address";
    public static final String KEY_SERVICE_CENTER_ADDRESS = "key_service_center_address";

    public static Observable<Bundle> asObservable(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        filter.setPriority(Integer.MAX_VALUE);
        return RxBroadcastReceiver.create(context, filter)
                .map(intent -> {
                    Bundle result = new Bundle();
                    try {
                        if (BuildConfig.DEBUG) {
                            JLog.d("收到广播：" + intent.getAction());
                            Bundle data = intent.getExtras();
                            for (String key : data.keySet()) {
                                JLog.d(key + " : " + data.get(key));
                            }
                        }
                        Object[] pdus = (Object[]) intent.getExtras().get("pdus");
                        String fromAddress = null;
                        String serviceCenterAddress = null;
                        if (pdus != null) {
                            String msgBody = "";
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.DONUT) {
                                for (Object obj : pdus) {
                                    SmsMessage sms = SmsMessage.createFromPdu((byte[]) obj);
                                    msgBody += sms.getMessageBody();
                                    fromAddress = sms.getOriginatingAddress();
                                    serviceCenterAddress = sms.getServiceCenterAddress();

                                    //JLog.d("getDisplayMessageBody：" + sms.getDisplayMessageBody());
                                    //JLog.d("getDisplayOriginatingAddress：" + sms.getDisplayOriginatingAddress());
                                    //JLog.d("getEmailBody：" + sms.getEmailBody());
                                    //JLog.d("getEmailFrom：" + sms.getEmailFrom());
                                    //JLog.d("getMessageBody：" + sms.getMessageBody());
                                    //JLog.d("getOriginatingAddress：" + sms.getOriginatingAddress());
                                    //JLog.d("getPseudoSubject：" + sms.getPseudoSubject());
                                    //JLog.d("getServiceCenterAddress：" + sms.getServiceCenterAddress());
                                    //JLog.d("getIndexOnIcc：" + sms.getIndexOnIcc());
                                    //JLog.d("getMessageClass：" + sms.getMessageClass());
                                    //JLog.d("getUserData：" + new String(sms.getUserData()));
                                }
                            }
                            result.putString(KEY_MESSAGE, msgBody);
                            result.putString(KEY_FROM_ADDRESS, fromAddress);
                            result.putString(KEY_SERVICE_CENTER_ADDRESS, serviceCenterAddress);

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return result;
                });
    }

    /**
     * Call requires API level 4
     * <uses-permission android:name="android.permission.SEND_SMS"/>
     */
    public static void sendMsgToPhone(String phone, String msg) {
        Log.i(TAG, "发送手机：" + phone + " ,内容： " + msg);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.DONUT) {
            SmsManager manager = SmsManager.getDefault();
            List<String> texts = manager.divideMessage(msg);
            for (String txt : texts) {
                manager.sendTextMessage(phone, null, txt, null, null);
            }
        } else {
            Log.e(TAG, "发送失败，系统版本低于DONUT，" + phone + " ,内容： " + msg);
        }

    }

    /**
     * Call requires API level 4
     * <uses-permission android:name="android.permission.WRITE_SMS"/>
     */
    public static void saveMsgToSystem(Context context, String phone, String msg) {
        ContentValues values = new ContentValues();
        values.put("date", System.currentTimeMillis());
        //阅读状态 
        values.put("read", 0);
        //1为收 2为发  
        values.put("type", 2);
        values.put("address", phone);
        values.put("body", msg);
        context.getContentResolver().insert(Uri.parse("content://sms/inbox"), values);
    }
}
