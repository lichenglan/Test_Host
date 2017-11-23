package com.techjumper.corelib.utils.window;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * StatusbarHelper.from(this).setTransparentStatusbar(true).setLightStatusBar(true).process();
 */
public final class StatusbarHelper {

    public static final String TAG = "StatusbarHelper";
    private boolean lightStatusBar;
    //透明且背景不占用控件的statusbar，这里估且叫做沉浸
    private boolean transparentStatusbar;
    private Activity activity;
    private View actionBarView;
    private View layoutRoot;
    private boolean noActionBar;
    private boolean noActionBarOffset;

    private StatusbarHelper(Activity activity, boolean lightStatusBar, boolean transparentStatusbar,
                            View actionBarView, View layoutRoot, boolean noActionBar, boolean noActionBarOffset) {
        this.lightStatusBar = lightStatusBar;
        this.transparentStatusbar = transparentStatusbar;
        this.activity = activity;
        this.actionBarView = actionBarView;
        this.layoutRoot = layoutRoot;
        this.noActionBar = noActionBar;
        this.noActionBarOffset = noActionBarOffset;
    }

    public static boolean isKitkat() {
        return Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT;
    }

    public static boolean isLessKitkat() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT;
    }

    public static boolean isMoreLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static Builder from(Activity activity) {
        return new StatusbarHelper.Builder().setActivity(activity);
    }

    public static void setStatusBarOffset(View view) {
        if (isLessKitkat()) return;

        if (view == null) return;
        view.post(new Runnable() {
            @Override
            public void run() {
                view.setPadding(view.getLeft()
                        , getStatusBarHeightPx(view.getContext()) + view.getPaddingTop()
                        , view.getPaddingRight()
                        , view.getPaddingBottom());
            }
        });
    }

    /**
     * Default status dp = 24 or 25
     * mhdpi = dp * 1
     * hdpi = dp * 1.5
     * xhdpi = dp * 2
     * xxhdpi = dp * 3
     * eg : 1920x1080, xxhdpi, => status/all = 25/640(dp) = 75/1080(px)
     * <p>
     * don't forget toolbar's dp = 48
     *
     * @return px
     */
    public static int getStatusBarHeightPx(Context context) {
        Context appContext = context.getApplicationContext();
        int result = 0;
        int resourceId =
                appContext.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = appContext.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static int getActionBarHeight(Context context) {

        int actionBarHeight = 0;
        try {
            if (context instanceof AppCompatActivity) {
                AppCompatActivity appCompatActivity = (AppCompatActivity) context;
                ActionBar ab = appCompatActivity.getSupportActionBar();
                if (ab == null || !ab.isShowing()) return 0;
                actionBarHeight = ab.getHeight();
                if (actionBarHeight != 0) {
                    return actionBarHeight;
                }
            }

            final TypedValue tv = new TypedValue();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                if (context.getTheme()
                        .resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
                    actionBarHeight = TypedValue.complexToDimensionPixelSize(
                            tv.data, context.getResources().getDisplayMetrics());
                }
            }
        } catch (Exception e) {
            actionBarHeight = 0;
        }

        // else if
        // (getTheme().resolveAttribute(com.actionbarsherlock.R.attr.actionBarSize,
        // tv, true))
        // {
        // //使用actionbarsherlock的actionbar做兼容的情况
        // actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,
        // getResources().getDisplayMetrics());
        // }

        return actionBarHeight;
    }

    public void processActionBar(final View v) {
        if (v == null) {
            return;
        }
        v.post(new Runnable() {
            @Override
            public void run() {
                if (!transparentStatusbar) return;
                int statusBarHeightPx = getStatusBarHeightPx(v.getContext());
                v.setPadding(v.getPaddingLeft(), statusBarHeightPx + v.getPaddingTop()
                        , v.getPaddingRight(),
                        v.getPaddingBottom());
//                v.getLayoutParams().height += statusBarHeightPx;
            }
        });
    }

    private void processLayoutRoot(final View layoutRoot) {
        if (layoutRoot == null) return;
        layoutRoot.post(new Runnable() {
            @Override
            public void run() {

//                int actionBarHeight;
//                if (actionBarView != null) {
//                    actionBarHeight = actionBarView.getHeight();
//                } else {
//                    actionBarHeight = noActionBar ? 0 :
//                            StatusbarHelper.getActionBarHeight(layoutRoot.getContext());
//                }
//
//
//                int offset = 0;
//                if (actionBarHeight == 0) {
//                    offset = noActionBarOffset ?
//                            StatusbarHelper.getStatusBarHeightPx(layoutRoot.getContext()) : 0;
//                }

                layoutRoot.setPadding(layoutRoot.getPaddingLeft()
//                        , offset + actionBarHeight + layoutRoot.getPaddingTop()
                        , layoutRoot.getPaddingTop()
                        , layoutRoot.getPaddingRight(),
                        layoutRoot.getPaddingBottom());
//                        layoutRoot.getPaddingBottom() + getNavigationBarHeight(layoutRoot.getContext()));

            }
        });
    }

    /**
     * 调用私有API处理颜色
     */
    public void processPrivateAPI() {
        processFlyme(lightStatusBar);
        processMIUI(lightStatusBar);
    }

    public void process() {
        int current = Build.VERSION.SDK_INT;

        if (current < Build.VERSION_CODES.KITKAT) return;

        //处理4.4沉浸
        if (current == Build.VERSION_CODES.KITKAT) {
            processKitkat();
        }
        //6.0处理沉浸与颜色，5.0只可以处理沉浸(不建议用白色背景)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            processLollipopAbove();
        }
        //调用私有API处理颜色
        processPrivateAPI();
        processActionBar(actionBarView);
        processLayoutRoot(layoutRoot);
    }

    /**
     * 处理4.4沉浸
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    void processKitkat() {
        //int current = activity.getWindow().gef
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (transparentStatusbar) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    /**
     * 改变小米的状态栏字体颜色为黑色, 要求MIUI6以上
     * Tested on: MIUIV7 5.0 Redmi-Note3
     */
    void processMIUI(boolean lightStatusBar) {
        Class<? extends Window> clazz = activity.getWindow().getClass();
        try {
            int darkModeFlag;
            Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
            extraFlagField.invoke(activity.getWindow(), lightStatusBar ? darkModeFlag : 0, darkModeFlag);
        } catch (Exception ignored) {

        }
    }

    /**
     * 改变魅族的状态栏字体为黑色，要求FlyMe4以上
     */
    private void processFlyme(boolean isLightStatusBar) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        try {
            Class<?> instance = Class.forName("android.view.WindowManager$LayoutParams");
            int value = instance.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON").getInt(lp);
            Field field = instance.getDeclaredField("meizuFlags");
            field.setAccessible(true);
            int origin = field.getInt(lp);
            if (isLightStatusBar) {
                field.set(lp, origin | value);
            } else {
                field.set(lp, (~value) & origin);
            }
        } catch (Exception ignored) {
            //
        }
    }

    /**
     * 处理Lollipop以上
     * Lollipop可以设置为沉浸，不能设置字体颜色
     * M(API23)可以设定
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    void processLollipopAbove() {
        Window window = activity.getWindow();
        int flag = window.getDecorView().getSystemUiVisibility();
        if (lightStatusBar) {
            /**
             * see {@link <a href="https://developer.android.com/reference/android/R.attr.html#windowLightStatusBar"></a>}
             */
            flag |= (WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS
                    | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        if (transparentStatusbar) {
            //改变字体颜色
            flag |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        }
        window.getDecorView().setSystemUiVisibility(flag);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        window.setStatusBarColor(Color.TRANSPARENT);
//        } else {
//            int currColor = window.getStatusBarColor();
//            int result = Color.argb((int) (255 * 0.33F)
//                    , Color.red(currColor), Color.green(currColor), Color.blue(currColor));
//            window.setStatusBarColor(result);
////            window.setStatusBarColor(0x55000000);
//        }
////            window.setStatusBarColor(Color.TRANSPARENT);
    }

    public static int getNavigationBarHeight(Context context) {
        int result = 0;
        if (hasNavBar(context)) {
            Resources res = context.getResources();
            int resourceId = res.getIdentifier("navigation_bar_height", "dimen", "android");
            if (resourceId > 0) {
                result = res.getDimensionPixelSize(resourceId);
            }
        }
        return result;
    }

    /**
     * 检查是否存在虚拟按键栏
     */
    private static boolean hasNavBar(Context context) {
        Resources res = context.getResources();
        int resourceId = res.getIdentifier("config_showNavigationBar", "bool", "android");
        if (resourceId != 0) {
            boolean hasNav = res.getBoolean(resourceId);
            // check override flag
            String sNavBarOverride = getNavBarOverride();
            if ("1".equals(sNavBarOverride)) {
                hasNav = false;
            } else if ("0".equals(sNavBarOverride)) {
                hasNav = true;
            }
            return hasNav;
        } else { // fallback
            return !ViewConfiguration.get(context).hasPermanentMenuKey();
        }
    }

    /**
     * 判断虚拟按键栏是否重写
     */
    private static String getNavBarOverride() {
        String sNavBarOverride = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                Class c = Class.forName("android.os.SystemProperties");
                Method m = c.getDeclaredMethod("get", String.class);
                m.setAccessible(true);
                sNavBarOverride = (String) m.invoke(null, "qemu.hw.mainkeys");
            } catch (Throwable ignored) {
            }
        }
        return sNavBarOverride;
    }

    final public static class Builder {
        private Activity activity;
        private boolean lightStatusBar = false;
        private boolean transparentStatusbar = false;
        private View actionBarView;
        private View layoutRoot;
        private boolean noActionBar;
        private boolean noActionBarOffset;


        public Builder() {
        }


        /**
         * 如果没有标题栏,是否偏移状态栏的高度
         */
        public Builder noActionBarOffset(boolean bool) {
            this.noActionBarOffset = bool;
            return this;
        }

        public Builder noActionBar(boolean noActionBar) {
            this.noActionBar = noActionBar;
            return this;
        }

        public Builder setActionbarView(@Nullable View actionbarView) {
            this.actionBarView = actionbarView;
            return this;
        }

        public Builder setLayoutRoot(@Nullable View layoutRoot) {
            this.layoutRoot = layoutRoot;
            return this;
        }

        Builder setActivity(@NonNull Activity activity) {
            this.activity = activity;
            return this;
        }

        public Builder setLightStatusBar(boolean lightStatusBar) {
            this.lightStatusBar = lightStatusBar;
            return this;
        }

        public Builder setTransparentStatusbar(boolean transparentStatusbar) {
            this.transparentStatusbar = transparentStatusbar;
            return this;
        }

        public void process() {
            new StatusbarHelper(activity, lightStatusBar, transparentStatusbar
                    , actionBarView, layoutRoot, noActionBar, noActionBarOffset)
                    .process();
        }
    }
}