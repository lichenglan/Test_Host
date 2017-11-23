package com.techjumper.corelib.rx.tools;

import rx.Observable;

/**
 * create by zhaoyiding
 */
public class CommonWrap {

    private static final Observable.Transformer mWraper = new Observable.Transformer() {
        @Override
        public Object call(Object o) {
            return ((Observable) o)
                    .retryWhen(new RetryWhenProcess(3))
                    .compose(SchedulersCompat.applyExecutorSchedulers());
        }
    };

    @SuppressWarnings("unchecked")
    public static <T> Observable.Transformer<T, T> wrap() {
        return (Observable.Transformer<T, T>) mWraper;
    }

}