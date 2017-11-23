package com.techjumper.polyhome.blauncher.utils;

import com.techjumper.polyhome.blauncher.manager.ServiceMessengerManager;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 2016/10/29
 * * * * * * * * * * * * * * * * * * * * * * *
 **/
public class LogUtils {
    public static void insertLog(String content) {
        ServiceMessengerManager.getInstance().send(ServiceMessengerManager.CODE_INSERT_LOG, content);
    }
}
