package com.techjumper.corelib.utils;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 16/2/2
 * * * * * * * * * * * * * * * * * * * * * * *
 **/

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

/**
 * 所有Utils的初始化工具类
 */
public class Utils {

    public static final Handler mainHandler = new Handler(Looper.getMainLooper());
    public static Context appContext;

    public static void init(Context ctx) {
        appContext = ctx.getApplicationContext();
    }
}
