package com.techjumper.corelib.utils.common;

import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 16/2/12
 * * * * * * * * * * * * * * * * * * * * * * *
 **/
public class DateUtils {

    public static String formatCurrentTime(String pattern) {
        if (TextUtils.isEmpty(pattern)) return "";
        return formatTime(pattern, System.currentTimeMillis());
    }

    public static String formatTime(String pattern, long currentTimeMilli) {
        if (TextUtils.isEmpty(pattern) || currentTimeMilli < 0L) return "";
        Date date = currentTimeMilli == 0L ? new Date() : new Date(currentTimeMilli);
        return new SimpleDateFormat(pattern, Locale.CHINA).format(date);
    }
}
