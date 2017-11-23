package com.techjumper.corelib.utils.common;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.view.View;

import com.techjumper.corelib.utils.Utils;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 16/2/24
 * * * * * * * * * * * * * * * * * * * * * * *
 **/
public class ResourceUtils {

    public static void setBackgroundDrawable(View view, int res) {
        try {
            Drawable drawable = getDrawableRes(res);

            setBackgroundDrawable(view, drawable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Drawable getDrawableRes(int res) {
        Drawable drawable;
        if (Build.VERSION.SDK_INT < 21) {
            drawable = Utils.appContext.getResources().getDrawable(res);
        } else {
            drawable = Utils.appContext.getDrawable(res);
        }
        return drawable;
    }

    public static void setBackgroundDrawable(View view, Drawable drawable) {
        try {
            if (Build.VERSION.SDK_INT < 16) {
                view.setBackgroundDrawable(drawable);
            } else {
                view.setBackground(drawable);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @ColorInt
    public static int getColorResource(@ColorRes int res) {
        if (Build.VERSION.SDK_INT < 23) {
            return Utils.appContext.getResources().getColor(res);
        } else {
            return Utils.appContext.getColor(res);
        }
    }


    public static int getLayoutId(Context paramContext, String paramString) {
        try {
            return paramContext.getResources().getIdentifier(paramString, "layout",
                    paramContext.getPackageName());
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static int getStringId(Context paramContext, String paramString) {
        try {
            return paramContext.getResources().getIdentifier(paramString, "string",
                    paramContext.getPackageName());
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static int getDrawableId(Context paramContext, String paramString) {
        try {
            return paramContext.getResources().getIdentifier(paramString,
                    "drawable", paramContext.getPackageName());
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static int getStyleId(Context paramContext, String paramString) {
        try {
            return paramContext.getResources().getIdentifier(paramString,
                    "style", paramContext.getPackageName());
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static int getId(Context paramContext, String paramString) {
        try {
            return paramContext.getResources().getIdentifier(paramString,
                    "id", paramContext.getPackageName());
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static int getColorId(Context paramContext, String paramString) {
        try {
            return paramContext.getResources().getIdentifier(paramString,
                    "color", paramContext.getPackageName());
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

}
