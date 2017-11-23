package com.techjumper.corelib.utils.window.keyboardevent;


import android.app.Activity;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.techjumper.corelib.utils.window.keyboardevent.util.UIUtil;

/**
 * Created by yshrsmz on 15/03/17.
 */
public class KeyboardVisibilityEvent {

    private final static int KEYBOARD_VISIBLE_THRESHOLD_DP = 100;
    private static KeyboardVisibilityEventListener mListener;

    /**
     * Set keyboard visiblity change event listener.
     *
     * @param activity Activity
     * @param listener KeyboardVisibilityEventListener
     */
    public static void setEventListener(
            @NonNull final Activity activity,
            @NonNull final KeyboardVisibilityEventListener listener) {

        final View activityRoot = getActivityRoot(activity);
        mListener = listener;

        activityRoot.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {

                    private final Rect r = new Rect();

                    private final int visibleThreshold = Math.round(
                            UIUtil.convertDpToPx(activity, KEYBOARD_VISIBLE_THRESHOLD_DP));

                    private boolean wasOpened = false;

                    @Override
                    public void onGlobalLayout() {
                        activityRoot.getWindowVisibleDisplayFrame(r);

                        int heightDiff = activityRoot.getRootView().getHeight() - r.height();

                        boolean isOpen = heightDiff > visibleThreshold;

                        if (isOpen == wasOpened) {
                            // keyboard state has not changed
                            return;
                        }

                        wasOpened = isOpen;

                        if (mListener != null) {
                            mListener.onVisibilityChanged(isOpen);
                        }
                    }
                });
    }

    public static void unregister() {
        mListener = null;
    }

    /**
     * Determine if keyboard is visible
     *
     * @param activity Activity
     * @return Whether keyboard is visible or not
     */
    public static boolean isKeyboardVisible(Activity activity) {
        Rect r = new Rect();

        View activityRoot = getActivityRoot(activity);
        int visibleThreshold = Math
                .round(UIUtil.convertDpToPx(activity, KEYBOARD_VISIBLE_THRESHOLD_DP));

        activityRoot.getWindowVisibleDisplayFrame(r);

        int heightDiff = activityRoot.getRootView().getHeight() - r.height();

        return heightDiff > visibleThreshold;
    }

    private static View getActivityRoot(Activity activity) {
        return ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
    }
}
