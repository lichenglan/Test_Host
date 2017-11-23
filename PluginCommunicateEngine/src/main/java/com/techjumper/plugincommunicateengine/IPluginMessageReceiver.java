package com.techjumper.plugincommunicateengine;

import android.os.Bundle;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 16/6/3
 * * * * * * * * * * * * * * * * * * * * * * *
 **/
public interface IPluginMessageReceiver {
    void onPluginMessageReceive(int code, String message, Bundle extras);
}
