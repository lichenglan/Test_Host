package com.techjumper.corelib.utils.system;

import android.app.KeyguardManager;
import android.content.Context;
import android.os.PowerManager;

import com.techjumper.corelib.utils.Utils;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 16/6/30
 * * * * * * * * * * * * * * * * * * * * * * *
 **/
public class PowerUtil {
    /**
     * 点亮屏幕
     */
    public static void wakeUpScreen() {
        KeyguardManager km = (KeyguardManager) Utils.appContext.getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock kl = km.newKeyguardLock("unLock");
        kl.disableKeyguard();
        PowerManager pm = (PowerManager) Utils.appContext.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "HIDETAG");
        wl.acquire();
        wl.release();
    }

//    public static void turnOffScreen() {
//        PowerManager pm = (PowerManager) Utils.appContext.getSystemService(Context.POWER_SERVICE);
//        int code = Build.VERSION.SDK_INT >= 21 ? PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK
//                : PowerManager.SCREEN_DIM_WAKE_LOCK;
//        PowerManager.WakeLock wakeLock = pm.newWakeLock(code, "hidetag");
//        wakeLock.acquire();
//        wakeLock.release();
//    }
}
