package com.techjumper.corelib.rx.tools;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 16/2/18
 * * * * * * * * * * * * * * * * * * * * * * *
 **/

import com.techjumper.corelib.utils.common.JLog;

import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.exceptions.OnErrorNotImplementedException;
import rx.functions.Func1;
import rx.functions.Func2;

/**
 * 封装了RxJava的RetryWhen()
 */
public class RetryWhenProcess implements Func1<Observable<? extends Throwable>, Observable<?>> {

    public static final String ERROR_404 = "retrofit2.adapter.rxjava.HttpException: HTTP 404 Not Found";

    private long mInterval;
    private int mCount;
    public Throwable mThrowable;

    public RetryWhenProcess(long interval) {
        this(interval, 3);
    }


    public RetryWhenProcess(long interval, int count) {
        this(interval, count, true);
    }


    public RetryWhenProcess(long interval, int count, boolean isSecond) {
        mInterval = isSecond ? interval * 1000 : interval;
        mCount = count + 1;
    }
//
//    @Override
//    public Observable<?> call(Observable<? extends Throwable> observable) {
//        return observable.flatMap(throwable -> {
//            if (throwable instanceof UnknownHostException
//                    || throwable instanceof OnErrorNotImplementedException
//                    || ERROR_404.equals(throwable.toString())
//                    ) {
//                return Observable.error(throwable);
//            }
//            JLog.d("RetryWhenProcess:" + throwable);
//            return Observable.timer(mInterval, TimeUnit.SECONDS);
//        });
//    }

    @Override
    public Observable<?> call(Observable<? extends Throwable> errors) {
        return errors.flatMap(throwable1 -> {
            mThrowable = throwable1;
            JLog.d("RetryWhenProcess:" + throwable1);
            return errors;
        }).zipWith(Observable.range(1, mCount), (throwable, i) -> i)
                .flatMap(retryCount -> {
                    if (retryCount == mCount && mThrowable != null) {
                        return Observable.error(mThrowable);
                    }
                    return Observable.timer(mInterval, TimeUnit.MILLISECONDS);
                });
    }
}
