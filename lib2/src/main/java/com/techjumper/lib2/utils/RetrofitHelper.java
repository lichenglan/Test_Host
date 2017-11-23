package com.techjumper.lib2.utils;

import com.techjumper.lib2.others.Config;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 16/2/17
 * * * * * * * * * * * * * * * * * * * * * * *
 **/
public class RetrofitHelper {

    private static Retrofit.Builder mRetrofit = new Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .client(OkHttpHelper.getDefault());

    public static Class sDefaultInterface;

    public static <T> T create(String api, Class<T> cls) {
        Retrofit retrofit = mRetrofit.baseUrl(api).build();
        return retrofit.create(cls);
    }

    @SuppressWarnings("unchecked")
    public static <T> T createDefault() {
        Retrofit retrofit = mRetrofit.baseUrl(Config.sDefaultBaseUrl).build();
        return (T) retrofit.create(sDefaultInterface);
    }


}
