package com.techjumper.polyhome.polyhomebhost.utils;

import android.content.Intent;

import com.techjumper.corelib.utils.Utils;
import com.techjumper.polyhome.polyhomebhost.service.HostService;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 2016/11/6
 * * * * * * * * * * * * * * * * * * * * * * *
 **/
public class HostRTPIUtils {
    public static void updatePlugin() {
        Intent intent = new Intent(HostService.ACTION_RECEIVER_TO_PLUGIN_INSTRUCTION);
        intent.putExtra(HostService.KEY_MESSAGE, HostService.CODE_UPDATE_PLUGIN);
        Utils.appContext.sendBroadcast(intent);
    }
}
