package com.techjumper.corelib.rx.tools;

import com.techjumper.corelib.rx.ExecutorManager;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * Observable.from(someSource)
 * .map(new Func1<Data, Data>() {
 *
 * @Override public Data call(Data data) {
 * return manipulate(data);
 * }
 * })
 * .compose(this.<YourType>applySchedulers())
 * .subscribe(new Action1<Data>() {
 * @Override public void call(Data data) {
 * doSomething(data);
 * }
 * });
 *
 *
 * 线程切换器
 * 在compose()之前为工作线程，在compose之后为主线程
 */
public class SchedulersCompat {
    private static final Observable.Transformer computationTransformer =
            new Observable.Transformer() {
                @Override
                public Object call(Object observable) {
                    return ((Observable) observable).subscribeOn(Schedulers.computation())
                            .observeOn(AndroidSchedulers.mainThread());
                }
            };
    private static final Observable.Transformer ioTransformer = new Observable.Transformer() {
        @Override
        public Object call(Object observable) {
            return ((Observable) observable).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        }
    };
    private static final Observable.Transformer newTransformer = new Observable.Transformer() {
        @Override
        public Object call(Object observable) {
            return ((Observable) observable).subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread());
        }
    };
    private static final Observable.Transformer trampolineTransformer = new Observable.Transformer() {
        @Override
        public Object call(Object observable) {
            return ((Observable) observable).subscribeOn(Schedulers.trampoline())
                    .observeOn(AndroidSchedulers.mainThread());
        }
    };
    private static final Observable.Transformer executorTransformer = new Observable.Transformer() {
        @Override
        public Object call(Object observable) {
            return ((Observable) observable).subscribeOn(Schedulers.from(ExecutorManager.eventExecutor))
                    .observeOn(AndroidSchedulers.mainThread());
        }
    };

    /**
     * Don't break the chain: use RxJava's compose() operator
     */
    @SuppressWarnings("unchecked")
    public static <T> Observable.Transformer<T, T> applyComputationSchedulers() {
        return (Observable.Transformer<T, T>) computationTransformer;
    }

    @SuppressWarnings("unchecked")
    public static <T> Observable.Transformer<T, T> applyIoSchedulers() {
        return (Observable.Transformer<T, T>) ioTransformer;
    }

    @SuppressWarnings("unchecked")
    public static <T> Observable.Transformer<T, T> applyNewSchedulers() {
        return (Observable.Transformer<T, T>) newTransformer;
    }

    @SuppressWarnings("unchecked")
    public static <T> Observable.Transformer<T, T> applyTrampolineSchedulers() {
        return (Observable.Transformer<T, T>) trampolineTransformer;
    }

    @SuppressWarnings("unchecked")
    public static <T> Observable.Transformer<T, T> applyExecutorSchedulers() {
        return (Observable.Transformer<T, T>) executorTransformer;
    }
}
