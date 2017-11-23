package com.techjumper.lib2.utils;

import com.google.gson.Gson;
import com.techjumper.corelib.utils.common.JLog;


/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 16/2/15
 * * * * * * * * * * * * * * * * * * * * * * *
 **/
public class GsonUtils {

    private static final Gson mGson = new Gson();

    public static <T> T fromJson(String json, Class<T> cls) {
        T result = null;
        try {
            result = getGson().fromJson(json, cls);
        } catch (Exception e) {
            JLog.d("无法解析json:" + json + "到" + cls.getName() + "; exception message:" + e.toString());
        }

        return result;
    }

    public static String toJson(Object obj) {
        String result = null;
        try {
            result = getGson().toJson(obj);
        } catch (Exception e) {
            JLog.e("无法转换object到json:" + e.toString());
        }
        return result;
    }

    public static Gson getGson() {
//        return new GsonBuilder()
//                .setExclusionStrategies(new ExclusionStrategy() {
//                    @Override
//                    public boolean shouldSkipField(FieldAttributes f) {
//                        return f.getDeclaringClass().equals(RealmObject.class)
//                                || f.getDeclaringClass().equals(Drawable.class);
//                    }
//
//                    @Override
//                    public boolean shouldSkipClass(Class<?> clazz) {
//                        return false;
//                    }
//                })
//                .create();
        return mGson;
    }
}
