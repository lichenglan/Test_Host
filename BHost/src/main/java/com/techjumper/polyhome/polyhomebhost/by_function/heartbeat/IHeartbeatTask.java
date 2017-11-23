package com.techjumper.polyhome.polyhomebhost.by_function.heartbeat;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 2016/10/27
 * * * * * * * * * * * * * * * * * * * * * * *
 **/
public interface IHeartbeatTask {
    boolean checkHeartbeat(String heartbeat);
    void unsubscribe();
}
