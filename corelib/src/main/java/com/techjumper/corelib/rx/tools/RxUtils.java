package com.techjumper.corelib.rx.tools;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public class RxUtils {

    public static boolean unsubscribeIfNotNull(Subscription subscription) {
        boolean success = false;
        if (subscription != null
                && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
            success = true;
        }
        return success;
    }

    public static CompositeSubscription getNewCompositeSubIfUnsubscribed(CompositeSubscription subscription) {
        if (subscription == null || subscription.isUnsubscribed()) {
            return new CompositeSubscription();
        }

        return subscription;
    }
}
