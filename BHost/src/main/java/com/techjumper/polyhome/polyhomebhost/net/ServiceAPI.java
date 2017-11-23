package com.techjumper.polyhome.polyhomebhost.net;

import com.techjumper.polyhome.polyhomebhost.entity.APKListEntity;
import com.techjumper.polyhome.polyhomebhost.entity.BaseArgumentsEntity;
import com.techjumper.polyhome.polyhomebhost.entity.TrueEntity;
import com.techjumper.polyhome.polyhomebhost.entity.UpdateAPKEntity;
import com.techjumper.polyhome.polyhomebhost.entity.UserEntity;

import java.util.Map;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
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

    /**
     * 软件版本列表
     * <p>
     * parms:
     * platform: 1, #1-安卓 2-iOS
     * village_id: 1,   #小区ID
     * packages: ["com.andriod","com.poly","com.poly3"] #包名（数组）
     * return:
     * {
     * "error_code": 0,
     * "error_msg": null,
     * "data": {
     * "packages": [
     * "com.andriod",
     * "com.poly",
     * "com.donfs"
     * ]
     * }
     * }
     */
    @GET("update/list")
    Observable<APKListEntity> fetchAPKList(@QueryMap Map<String, String> args);

    /**
     * 日志上传
     * <p>
     * parms:
     * family_id： 1，
     * original_filename: "20160103140232.log",
     * file: file, #file为Base64 encode后的数据
     * return:
     * <p>
     * {
     * "error_code": 0,
     * "error_msg": null,
     * "data": {
     * "result": "true"
     * }
     * }
     */
    @POST("upload/logs")
    Observable<TrueEntity> uploadLogs(@Body BaseArgumentsEntity entity);


    @GET("family/info")
    Observable<UserEntity> getUserInfo(@QueryMap Map<String, String> map);
}
