package com.techjumper.polyhome_b.bhostdaemon.net;


import com.techjumper.polyhome_b.bhostdaemon.entity.UpdateAPKEntity;

import java.util.Map;

import retrofit2.http.GET;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 16/4/19
 * * * * * * * * * * * * * * * * * * * * * * *
 **/
public interface ServiceAPI {
    /**
     * 更新APK
     * <p>
     * {
     * "error_code": 0,
     * "error_msg": null,
     * "data": {
     * "result": [
     * {
     * "version": "1",    #版本号
     * "url": "/upload/files/main-host.apk",  #下载地址
     * "package_name": "com.andriod" #包名
     * },
     * {
     * "version": "3",
     * "url": "/upload/files/main-host.apk",
     * "package_name": "com.poly"
     * }
     * ]
     * }
     * }
     */
    @GET("update/apk")
    Observable<UpdateAPKEntity> fetchAPKInfo(@QueryMap Map<String, String> args);
}
