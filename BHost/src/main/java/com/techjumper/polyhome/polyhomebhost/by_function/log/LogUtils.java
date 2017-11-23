package com.techjumper.polyhome.polyhomebhost.by_function.log;

import com.techjumper.polyhome.polyhomebhost.by_function.log.tasks.InsertTask;
import com.techjumper.polyhome.polyhomebhost.by_function.log.tasks.PolyLogDbTaskQueue;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 2016/10/29
 * * * * * * * * * * * * * * * * * * * * * * *
 **/
public class LogUtils {
    public static void insertLog(String content) {
        PolyLogDbTaskQueue.getInstance().addToQueue(new InsertTask(content));
    }
}
