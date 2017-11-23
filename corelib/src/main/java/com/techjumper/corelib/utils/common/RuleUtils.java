package com.techjumper.corelib.utils.common;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.techjumper.corelib.utils.Utils;

/**
 * 尺寸大小实用工具类
 */
public class RuleUtils {


    /**
     * 获取屏幕的宽度
     */
    public static int getScreenWidth() {
        DisplayMetrics displayMetrics = Utils.appContext.getResources().getDisplayMetrics();
        return displayMetrics.widthPixels;
    }

    /**
     * 获取屏幕的高度
     */
    public static int getScreenHeight() {
        DisplayMetrics displayMetrics = Utils.appContext.getResources().getDisplayMetrics();
        return displayMetrics.heightPixels;
    }

    /**
     * 将dp转换成对应的px值
     */
    public static int dp2Px(float dp) {
        DisplayMetrics metrics = Utils.appContext.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics);
    }

    /**
     * 将px转换成对应的dp值
     */
    public static int px2Dp(float px) {
        final float scale = Utils.appContext.getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }

    /**
     * 将sp转换成对应的px值
     */
    public static int sp2Px(float sp) {
        DisplayMetrics metrics = Utils.appContext.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, metrics);
    }

}
