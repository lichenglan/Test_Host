package com.techjumper.corelib.rx.tools;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;


/**
 * _subscriptions = new CompositeSubscription();
 * <p>
 * <p>
 * ConnectableObservable<Object> tapEventEmitter = _rxBus.asObservable().publish();
 * <p>
 * _subscriptions.add(tapEventEmitter.subscribe(new Action1<Object>() {
 *
 * @Override public void call(Object event) {
 * if (event instanceof RxBusDemoFragment.TapEvent) {
 * _showTapText();
 * }
 * }
 * }));
 * <p>
 * _subscriptions.add(tapEventEmitter.publish(new Func1<Observable<Object>, Observable<List<Object>>>() {
 * @Override public Observable<List<Object>> call(Observable<Object> stream) {
 * return stream.buffer(stream.debounce(1, TimeUnit.SECONDS));
 * }
 * }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<List<Object>>() {
 * @Override public void call(List<Object> taps) {
 * _showTapCount(taps.size());
 * }
 * }));
 * <p>
 * _subscriptions.add(tapEventEmitter.connect());
 */

public enum RxBus {

    INSTANCE;

    //private final PublishSubject<Object> _bus = PublishSubject.create();

    // If multiple threads are going to emit events to this
    // then it must be made thread-safe like this instead
    private static final Subject<Object, Object> _bus = new SerializedSubject<>(PublishSubject.create());

    public void send(Object o) {
        if (hasObservers()) {
            try {
                _bus.onNext(o);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Observable<Object> asObservable() {
        return _bus.onBackpressureBuffer().observeOn(AndroidSchedulers.mainThread());
    }

    public boolean hasObservers() {
        return _bus.hasObservers();
    }
}