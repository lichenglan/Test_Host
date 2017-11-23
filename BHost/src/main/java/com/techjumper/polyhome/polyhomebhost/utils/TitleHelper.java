package com.techjumper.polyhome.polyhomebhost.utils;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 16/2/28
 * * * * * * * * * * * * * * * * * * * * * * *
 **/
public class TitleHelper {

//    public static void setTextAlpha(View viewRoot, float alpha) {
//        View view = viewRoot.findViewById(R.id.tv_title);
//        view.setAlpha(alpha);
//    }
//
//    public static void setTitleAlpha(View viewRoot, float alpha) {
//        View view = viewRoot.findViewById(R.id.title_group);
//        view.setAlpha(alpha);
//    }
//
//    public static void showOrHideRight(View viewRoot, boolean show) {
//        View view = viewRoot.findViewById(R.id.layout_title_right);
//        if (view != null) {
//            view.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
//        }
//        view = viewRoot.findViewById(R.id.right_group);
//        if (view != null) {
//            view.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
//        }
//    }
//
//    public static void setOnRightClickListener(View viewRoot, IconTextSwitchView.IIconText iIconText) {
//        UI.create(viewRoot).<IconTextSwitchView>findById(R.id.layout_title_right).setOnIconAndTextClickListener(iIconText);
//    }
//
//    public static void setRightIcon(View viewRoot, int resId) {
//        UI.create(viewRoot).<ImageView>findById(R.id.iv_right_icon).setImageResource(resId);
//    }
//
//    public static void setTitleDefaultBg(View viewRoot) {
//        ResourceUtils.setBackgroundDrawable(viewRoot.findViewById(R.id.title_group), R.mipmap.bg_title);
//    }
//
//    public static Builder create(Activity ac) {
//        return new Builder(ac);
//    }
//
//    public static Builder create(View view) {
//        return new Builder(view);
//    }
//
//    public static void showRightIconOrText(View viewRoot, boolean showText) {
//        IconTextSwitchView view = UI.create(viewRoot).<IconTextSwitchView>findById(R.id.layout_title_right);
//        if (showText) {
//            view.showText();
//        } else {
//            view.showIcon();
//        }
//    }
//
//    public static class Builder {
//        private Object obj;
//        private View.OnClickListener leftIconClickListener;
//        private View.OnClickListener rightIconClickListener;
//        private String titleText;
//        private boolean showRight;
//        private boolean showLeft = true;
//
//        private Builder(Object obj) {
//            this.obj = obj;
//        }
//
//        public Builder title(String text) {
//            titleText = text;
//            return this;
//        }
//
//        public Builder leftIconClick(View.OnClickListener listener) {
//            leftIconClickListener = listener;
//            return this;
//        }
//
//        public Builder rightIconClick(View.OnClickListener listener) {
//            rightIconClickListener = listener;
//            return this;
//        }
//
//        public Builder showRight(boolean right) {
//            showRight = right;
//            return this;
//        }
//
//        public Builder showLeft(boolean left) {
//            showLeft = left;
//            return this;
//        }
//
//        public void process() {
//            UI ui;
//            if (obj instanceof Activity) {
//                ui = UI.create((Activity) obj);
//            } else {
//                ui = UI.create((View) obj);
//            }
//            if (ui.findById(R.id.title_group) == null) return;
//            View back = ui.findById(R.id.left_group);
//            View right = ui.findById(R.id.right_group);
//            if (back == null) return;
//            back.setVisibility(showLeft ? View.VISIBLE : View.GONE);
//            back.setOnClickListener(leftIconClickListener);
//            if (right != null) {
//                right.setVisibility(showRight ? View.VISIBLE : View.GONE);
//                right.setOnClickListener(rightIconClickListener);
//            }
//            TextView tv = ui.findById(R.id.tv_title);
//            if (tv != null) {
//                tv.setText(titleText);
//            }
//        }
//    }
}
