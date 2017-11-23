package com.techjumper.polyhome.blauncher.interfaces;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 16/5/17
 * * * * * * * * * * * * * * * * * * * * * * *
 **/
public interface IServiceMessenger {
    void onServiceMessengerConnected();

    void onServiceDisconnected();

    void onServiceMessengerError(Throwable e);
}
