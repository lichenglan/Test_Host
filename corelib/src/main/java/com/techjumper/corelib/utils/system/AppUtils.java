package com.techjumper.corelib.utils.system;


import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.techjumper.corelib.utils.Utils;

/**
 * 系统和应用实用工具类
 */
public class AppUtils {

    /**
     * 获取设备的制造商
     *
     * @return 设备制造商
     */
    public static String getDeviceManufacture() {
        return android.os.Build.MANUFACTURER;
    }

    /**
     * 获取设备名称
     *
     * @return 设备名称
     */
    public static String getDeviceName() {
        return android.os.Build.MODEL;
    }

    /**
     * 获取系统版本号
     *
     * @return 系统版本号
     */
    public static String getSystemVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    /**
     * 获取设备号
     *
     * @return
     */
    public static String getDeviceIMEI() {
        TelephonyManager telephonyManager = (TelephonyManager) Utils.appContext.getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager == null || TextUtils.isEmpty(telephonyManager.getDeviceId())) {
            return Settings.Secure.getString(Utils.appContext.getContentResolver(), Settings.Secure.ANDROID_ID);
        } else {
            return telephonyManager.getDeviceId();
        }
    }

    /**
     * 获取应用的版本名
     */
    public static String getAppVersion() {
        PackageManager packageManager = Utils.appContext.getPackageManager();
        PackageInfo packageInfo;
        try {
            packageInfo = packageManager.getPackageInfo(Utils.appContext.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getPackageName() {
        return Utils.appContext.getPackageName();
    }

    /**
     * 获取应用的版本号
     */
    public static int getVersionCode() {
        int versionCode;
        try {
            versionCode = Utils.appContext.getPackageManager()
                    .getPackageInfo(Utils.appContext.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            versionCode = -1;
        }
        return versionCode;
    }

    /**
     * Get application name from Manifest file.
     *
     * @return application name.
     */
    public static String getApplicationName() {
        int stringId = Utils.appContext.getApplicationInfo().labelRes;
        return Utils.appContext.getString(stringId);
    }


    /**
     * 判断当前有没有网络连接
     */
    public static boolean isNetworkAvailable() {
//        ConnectivityManager manager = (ConnectivityManager) Utils.appContext
//                .getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo networkinfo = manager.getActiveNetworkInfo();
//        return !(networkinfo == null || !networkinfo.isAvailable());
        ConnectivityManager connectivityManager = (ConnectivityManager) Utils.appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * Checks to see if the user has rotation enabled/disabled in their phone settings.
     *
     * @return true if rotation is enabled, otherwise false.
     */
    public static boolean isRotationEnabled() {
        return android.provider.Settings.System.getInt(Utils.appContext.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0) == 1;
    }

    /**
     * Check if there is any connectivity to a Wifi network.
     * <p>
     * Can be used in combination with {@link #isConnectedMobile}
     * to provide different features if the device is on a wifi network or a cell network.
     *
     * @return true if a wifi connection is available, otherwise false.
     */
    public static boolean isConnectedWifi() {
        ConnectivityManager cm = (ConnectivityManager) Utils.appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_WIFI);
    }

    /**
     * Check if there is any connectivity to a mobile network
     * <p>
     * Can be used in combination with {@link #isConnectedWifi}
     * to provide different features if the device is on a wifi network or a cell network.
     *
     * @return true if a mobile connection is available, otherwise false.
     */
    public static boolean isConnectedMobile() {
        ConnectivityManager cm = (ConnectivityManager) Utils.appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_MOBILE);
    }


    /**
     * 得到手机号码(卡1)
     */
    public static String getLine1Number() {
        String number = "";
        try {
            TelephonyManager tm = (TelephonyManager) Utils.appContext.getSystemService(Context.TELEPHONY_SERVICE);
            number = tm.getLine1Number();
            if (!TextUtils.isEmpty(number)
                    && number.length() > 11) {
                number = number.substring(number.length() - 11, number.length());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return number;
    }

    /**
     * 是否为魅族手机
     */
    public static boolean isMeizu() {
        return Build.BOARD.contains("meizu")
                || Build.BRAND.contains("Meizu");
    }


}
