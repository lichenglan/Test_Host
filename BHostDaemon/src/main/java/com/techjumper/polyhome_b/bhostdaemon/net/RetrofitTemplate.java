package com.techjumper.polyhome_b.bhostdaemon.net;

import com.techjumper.corelib.rx.tools.CommonWrap;
import com.techjumper.lib2.utils.RetrofitHelper;
import com.techjumper.polyhome_b.bhostdaemon.entity.UpdateAPKEntity;

import rx.Observable;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 16/6/24
 * * * * * * * * * * * * * * * * * * * * * * *
 **/
public class RetrofitTemplate {

    private static RetrofitTemplate INSTANCE;

    private RetrofitTemplate() {

//        Config.sDefaultBaseUrl = com.techjumper.polyhome_b.adlib.Config.sBaseUrl;
//        if (RetrofitHelper.sDefaultInterface == null) {
//            RetrofitHelper.sDefaultInterface = ServiceAPI.class;
//        }
    }

    public static RetrofitTemplate getInstance() {
        if (INSTANCE == null) {
            synchronized (RetrofitTemplate.class) {
                if (INSTANCE == null) {
                    INSTANCE = new RetrofitTemplate();
                }
            }
        }
        return INSTANCE;
    }

    public Observable<UpdateAPKEntity> fetchApkInfo(String[] packageNames) {
        return RetrofitHelper.<ServiceAPI>createDefault()
                .fetchAPKInfo(NetHelper.createBaseArgumentsMap(KeyValueCreator.fetchAPKInfo(packageNames)))
                .compose(CommonWrap.wrap());
    }
}
