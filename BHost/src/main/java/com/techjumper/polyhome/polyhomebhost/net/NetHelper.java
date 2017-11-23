package com.techjumper.polyhome.polyhomebhost.net;

import com.techjumper.corelib.utils.Utils;
import com.techjumper.corelib.utils.basic.StringUtils;
import com.techjumper.corelib.utils.window.ToastUtils;
import com.techjumper.lib2.others.KeyValuePair;
import com.techjumper.lib2.utils.GsonUtils;
import com.techjumper.polyhome.polyhomebhost.R;
import com.techjumper.polyhome.polyhomebhost.entity.BaseArgumentsEntity;
import com.techjumper.polyhome.polyhomebhost.entity.BaseEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 16/3/3
 * * * * * * * * * * * * * * * * * * * * * * *
 **/
public class NetHelper {

    private static final String PRIVATE_KEY = "GjcfbhCIJ2owQP1Kxn64DqSk5X4YRZ7u";
    public static final int CODE_SUCCESS = 0;
    public static final int CODE_NOT_LOGIN = 109;
    public static final int CODE_NO_DATA = 404;
    public static final int CODE_NO_THIS_FAMILY = 301;

    /**
     * 全局超时时间
     */
    public static int GLOBAL_TIME_OUT = 5000;

    public static String encrypt(String json) {
        String pre = json + PRIVATE_KEY;
        return StringUtils.md5(pre);
    }

    public static BaseArgumentsEntity createBaseArguments(KeyValuePair argMap) {
        String json = GsonUtils.toJson(argMap.toMap());
        String encrypt = encrypt(json);
        return new BaseArgumentsEntity(encrypt, json);
    }

    public static Map<String, String> createBaseArgumentsMap(KeyValuePair argMap) {
        String json = GsonUtils.toJson(argMap.toMap());
        String encrypt = encrypt(json);
        HashMap<String, String> map = new HashMap<>();
        map.put(BaseArgumentsEntity.FILED_SIGN, encrypt);
        map.put(BaseArgumentsEntity.FILED_DATA, json);
        return map;
    }

    public static boolean isSuccess(BaseEntity entity) {
        return entity != null && entity.getError_code() == CODE_SUCCESS;
    }

    public static boolean processNetworkResult(BaseEntity entity) {
        return processNetworkResult(entity, true);
    }

    public static boolean processNetworkResult(BaseEntity entity, boolean notifyNoData) {
        if (NetHelper.isSuccess(entity))
            return true;
        if (entity != null) {
            if (entity.getError_code() == NetHelper.CODE_NO_DATA) {
                if (notifyNoData)
                    ToastUtils.show(Utils.appContext.getString(R.string.error_no_data));
            } else {
                ToastUtils.show(entity.getError_code() + ":" + entity.getError_msg());
            }
        } else
            ToastUtils.show("网络连接失败");
        return false;
    }
}

