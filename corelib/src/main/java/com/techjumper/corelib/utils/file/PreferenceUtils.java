package com.techjumper.corelib.utils.file;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.techjumper.corelib.utils.Utils;

import java.util.Set;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 16/2/13
 * * * * * * * * * * * * * * * * * * * * * * *
 **/

/**
 * SharedPreference封装类
 * 这个工具类会有较多的装箱拆箱，但SP一般不会做大量操作所以问题不大。
 * <p>
 * 注意：如果不满足if()条件则会报异常
 */
public class PreferenceUtils {

    @SuppressWarnings("unchecked")
    public static <T> T get(String key, T defValue) {

        if (defValue instanceof String) {
            return (T) getPreference().getString(key, (String) defValue);
        } else if (defValue instanceof Integer) {
            Integer value = getPreference().getInt(key, (Integer) defValue);
            return (T) value;
        } else if (defValue instanceof Boolean) {
            Boolean value = getPreference().getBoolean(key, (Boolean) defValue);
            return (T) value;
        } else if (defValue instanceof Float) {
            Float value = getPreference().getFloat(key, (Float) defValue);
            return (T) value;
        } else if (defValue instanceof Double) {
            Float value = getPreference().getFloat(key, ((Double) defValue).floatValue());
            Double dValue = value.doubleValue();
            return (T) dValue;
        } else if (defValue instanceof Long) {
            Long value = getPreference().getLong(key, (Long) defValue);
            return (T) value;
        } else if (defValue instanceof Set) {
            return (T) getPreference().getStringSet(key, (Set<String>) defValue);
        }
        return (T) new Object();
    }

    @SuppressWarnings("unchecked")
    public static <T> boolean save(String key, T value) {
        SharedPreferences.Editor editor = getPreference().edit();
        if (value instanceof String) {
            editor.putString(key, (String) value);
        } else if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        } else if (value instanceof Float) {
            editor.putFloat(key, (Float) value);
        } else if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        } else if (value instanceof Set) {
            editor.putStringSet(key, (Set<String>) value);
        } else if (value instanceof Double) {
            editor.putFloat(key, ((Double) value).floatValue());
        }
        return editor.commit();
    }

    public static SharedPreferences getPreference() {
        return PreferenceManager.getDefaultSharedPreferences(Utils.appContext);
    }

    public static SharedPreferences getPreference(String name) {
        return Utils.appContext.getSharedPreferences(name, 0);
    }
}
