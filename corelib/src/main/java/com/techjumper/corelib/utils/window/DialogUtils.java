package com.techjumper.corelib.utils.window;


import android.app.Activity;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.techjumper.corelib.R;
import com.techjumper.corelib.utils.Utils;

/**
 * <h2>Utility methods used to show a quick pop-up dialog.</h2>
 * <p>
 * <h3>Common uses:</h3>
 * <code>DialogUtils.{@link #quickDialog quickDialog}(this, "Test Message");</code> //Shows an alert dialog
 */
public class DialogUtils {

    /**
     * Show a model dialog box.  The <code>android.app.AlertDialog</code> object is returned so that
     * you can specify an OnDismissListener (or other listeners) if required.
     * <b>Note:</b> show() is already called on the AlertDialog being returned.
     *
     * @param context The current Context or Activity that this method is called from.
     * @param message Message to display in the dialog.
     * @return AlertDialog that is being displayed.
     */
    public static void quickDialog(final Activity context, final String message) {
        try {
            getBuilder(context)
                    .content(message)
                    .positiveText(android.R.string.ok)
                    .show();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public static MaterialDialog.Builder getBuilder(Activity context) {
        return new MaterialDialog.Builder(context)
                .backgroundColor(0xFF37474F)
                .positiveColor(0xFF1DE9B6)
                .negativeColor(0xFF1DE9B6)
                .titleColor(0xFF1DE9B6)
                .dividerColor(0xFF1DE9B6)
                .contentColorRes(android.R.color.white)
                .theme(Theme.DARK);

    }


}
