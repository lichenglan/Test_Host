package com.techjumper.corelib.utils.window;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.techjumper.corelib.utils.Utils;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 16/2/2
 * * * * * * * * * * * * * * * * * * * * * * *
 **/
public class KeyboardUtils {

    /**
     * Go away keyboard, nobody likes you.
     *
     * @param field field that holds the keyboard focus
     */
    public static void closeKeyboard(View field) {
        try {
            InputMethodManager imm = (InputMethodManager) Utils.appContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(field.getWindowToken(), 0);
        } catch (Exception ex) {
            Log.e("HIDETAG", "Error occurred trying to hide the keyboard.  Exception=" + ex);
        }
    }

    /**
     * Show the pop-up keyboard
     *
     * @param field field that requests focus
     */
    public static void showKeyboard(View field) {
        try {
            field.requestFocus();
            InputMethodManager imm = (InputMethodManager) Utils.appContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(field, InputMethodManager.SHOW_IMPLICIT);
        } catch (Exception ex) {
            Log.e("HIDETAG", "Error occurred trying to show the keyboard.  Exception=" + ex);
        }
    }
}
