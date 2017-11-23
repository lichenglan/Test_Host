package com.techjumper.polyhome.blauncher.interfaces;

import android.os.Bundle;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 16/6/6
 * * * * * * * * * * * * * * * * * * * * * * *
 **/
public interface IServiceReplyListener {
    void onServiceReply(int code, String message, Bundle extras);
}
