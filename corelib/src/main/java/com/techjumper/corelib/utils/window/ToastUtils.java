package com.techjumper.corelib.utils.window;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.techjumper.corelib.R;
import com.techjumper.corelib.utils.Utils;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 15/9/8
 * * * * * * * * * * * * * * * * * * * * * * *
 **/
public class ToastUtils {

    private static Toast mToast;
    private static TextView mTvContent;

    public static void show(String text) {
        Utils.mainHandler.post(() -> {
            if (mToast == null) {
                mToast = new Toast(Utils.appContext);
                mToast.setDuration(Toast.LENGTH_SHORT);
                View view = LayoutInflater.from(Utils.appContext).inflate(R.layout.toast, null);
                mTvContent = (TextView) view.findViewById(R.id.tv_content);
                mToast.setView(view);
            }

            mTvContent.setText(text);
            mToast.show();
        });
    }

    public static void showLong(String text) {
        Utils.mainHandler.post(() -> {
            if (mToast == null) {
                mToast = new Toast(Utils.appContext);
                mToast.setDuration(Toast.LENGTH_LONG);
                View view = LayoutInflater.from(Utils.appContext).inflate(R.layout.toast, null);
                mTvContent = (TextView) view.findViewById(R.id.tv_content);
                mToast.setView(view);
            } else {
                mToast.setDuration(Toast.LENGTH_LONG);
            }


            mTvContent.setText(text);
            mToast.show();
        });
    }

}
