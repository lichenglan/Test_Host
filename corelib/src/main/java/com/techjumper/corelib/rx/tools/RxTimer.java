package com.techjumper.corelib.rx.tools;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 16/3/17
 * * * * * * * * * * * * * * * * * * * * * * *
 **/
public class RxTimer {

    /**
     * @param interval 间隔时间,单位:毫秒
     */
    public static Observable<Long> timer(int interval) {
        return timer(interval, interval);
    }

    public static Observable<Long> timer(int init, int interval) {
        return Observable.interval(init, interval, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread());
    }

}
