package com.techjumper.corelib.utils.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.techjumper.corelib.R;

import java.util.Map;
import java.util.Map.Entry;

public class AcHelper {

    public static int defaultStartAnimEnter = R.anim.slid_in_right;
    public static int defaultStartAnimExit = R.anim.slid_out_left;
    public static int defaultBackAnimEnter = R.anim.slid_in_left;
    public static int defaultBackAnimExit = R.anim.slid_out_right;


    private AcHelper() {
    }

    public static void finish(Activity ac) {
        if (ac == null || ac.isFinishing()) return;
        if (ac instanceof AppCompatActivity) {
            ((AppCompatActivity) ac).supportFinishAfterTransition();
        } else {
            ac.finish();
        }
        ac.overridePendingTransition(defaultBackAnimEnter, defaultBackAnimExit);
    }

    /**
     * Used to get the parameter values passed into Activity via a Bundle.<p>
     * !千万不要用这个方法获取基本数据类型（int等等），会直接报空指针而且无法被try..catch..!
     */
    @SuppressWarnings("unchecked")
    public static <T> T getExtra(Activity context, String key) {
        T param = null;
        try {
            Bundle bundle = context.getIntent().getExtras();
            param = (T) bundle.get(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return param;
    }


    /**
     * Force screen to turn on if the phone is asleep.
     *
     * @param context The current Context or Activity that this method is called from
     */
    public static void turnScreenOn(Activity context) {
        try {
            Window window = context.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
            window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
            window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        } catch (Exception ex) {
            Log.e("PercolateAndroidUtils", "Unable to turn on screen for activity " + context);
        }
    }

    public static void fullScreen(Activity ac, boolean b) {
        Window window = ac.getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;

        if (b) {
            attributes.flags |= flag;
        } else {
            attributes.flags &= ~flag;
        }
        window.setAttributes(attributes);
    }

    public static class Builder {
        Activity ac;
        Class<? extends Activity> target;
        Bundle extra;
        boolean closeCurrent;
        int enterAnim;
        int exitAnim;
        int requestCode;

        public Builder(Activity ac) {
            this.ac = ac;
        }

        public Builder target(Class<? extends Activity> target) {
            this.target = target;
            return this;
        }

        public Builder extra(Bundle extra) {
            this.extra = extra;
            return this;
        }

        public Builder closeCurrent(boolean closeCurrent) {
            this.closeCurrent = closeCurrent;
            return this;
        }

        public Builder enterAnim(int enterAnim) {
            this.enterAnim = enterAnim;
            return this;
        }

        public Builder exitAnim(int exitAnim) {
            this.exitAnim = exitAnim;
            return this;
        }

        public Builder requestCode(int requestCode) {
            this.requestCode = requestCode;
            return this;
        }

        public void start() {
            if (target == null) return;
            Intent intent = new Intent(ac, target);
            if (extra != null) intent.putExtras(extra);
            if (requestCode == 0)
                ac.startActivity(intent);
            else
                ac.startActivityForResult(intent, requestCode);
            if (closeCurrent) ac.finish();
            enterAnim = enterAnim == 0 ? defaultStartAnimEnter : enterAnim;
            exitAnim = exitAnim == 0 ? defaultStartAnimExit : exitAnim;
            ac.overridePendingTransition(enterAnim, exitAnim);
        }
    }

}
