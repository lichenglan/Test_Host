package com.techjumper.polyhome.polyhomebhost.net;

import com.techjumper.corelib.rx.tools.CommonWrap;
import com.techjumper.corelib.utils.file.FileUtils;
import com.techjumper.lib2.utils.RetrofitHelper;
import com.techjumper.polyhome.polyhomebhost.entity.TrueEntity;
import com.techjumper.polyhome.polyhomebhost.entity.UserEntity;

import rx.Observable;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 2016/10/29
 * * * * * * * * * * * * * * * * * * * * * * *
 **/
public class NetExecutor {
    public static Observable<TrueEntity> uploadLogs(String family_id, String originalFileName, String base64Content) {
        return RetrofitHelper.<ServiceAPI>createDefault()
                .uploadLogs(NetHelper.createBaseArguments(KeyValueCreator.uploadLogs(family_id
                        , originalFileName, base64Content)))
                .compose(CommonWrap.wrap());
    }

    public static Observable<UserEntity> getUserInfo() {
        return RetrofitHelper.<ServiceAPI>createDefault()
                .getUserInfo(NetHelper.createBaseArgumentsMap(KeyValueCreator.getUserInfo(FileUtils.getMacAddress())))
                .compose(CommonWrap.wrap());
    }
}
