package com.techjumper.polyhome.polyhomebhost.mvp.v.activity;


import android.os.Bundle;

import com.techjumper.corelib.mvp.factory.Presenter;
import com.techjumper.corelib.rx.tools.RxUtils;
import com.techjumper.corelib.ui.activity.BaseFragmentActivity;
import com.techjumper.corelib.utils.Utils;
import com.techjumper.corelib.utils.window.ToastUtils;
import com.techjumper.polyhome.polyhomebhost.R;
import com.techjumper.polyhome.polyhomebhost.mvp.p.activity.AppBaseActivityPresenter;
import com.techjumper.progressdialog.KProgressHUD;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 16/2/23
 * * * * * * * * * * * * * * * * * * * * * * *
 **/
@Presenter(AppBaseActivityPresenter.class)
public abstract class AppBaseActivity<T extends AppBaseActivityPresenter> extends BaseFragmentActivity<T> {

    private List<Subscription> mSubList = new ArrayList<>();
    private KProgressHUD mProgress;
//    private TitleHelper.Builder mTitleBuilder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProgress = KProgressHUD.create(this)
                .setDimAmount(0.3F)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setOnDismissListener(dialog -> onDialogDismiss());

//        mTitleBuilder = TitleHelper.create(mViewRoot)
//                .title(getLayoutTitle())
//                .showLeft(showTitleLeft())
//                .showRight(showTitleRight())
//                .leftIconClick(v -> {
//                    if (!onTitleLeftClick()) {
//                        AcHelper.finish(this);
//                    }
//                })
//                .rightIconClick(v1 -> {
//                    if (!onTitleRightClick()) {
//                        KeyboardUtils.closeKeyboard(mViewRoot);
//
//                    }
//                });
//        mTitleBuilder.process();
    }

    protected boolean showTitleRight() {
        return false;
    }

    protected boolean showTitleLeft() {
        return true;
    }

    protected boolean onTitleRightClick() {
        return false;
    }

    protected boolean onTitleLeftClick() {
        return false;
    }

    public String getLayoutTitle() {
        return "";
    }

//    @Override
//    protected StatusbarHelper.Builder onStatusbarTransform(StatusbarHelper.Builder builder) {
//        View titleGroup = findViewById(R.id.title_group);
////        builder.setLayoutRoot(null);
//        return titleGroup == null ? super.onStatusbarTransform(builder) : builder.setActionbarView(titleGroup);
//    }

    public void onResume() {
//        MobclickAgent.onResume(this);
        super.onResume();
    }

    public void onPause() {
//        MobclickAgent.onPause(this);
        super.onPause();
    }

    public List<Subscription> addSubscription(Subscription subscription) {
        mSubList.add(subscription);
        return mSubList;
    }

    public void unsubscribeAll() {
        for (Subscription sub : mSubList) {
            RxUtils.unsubscribeIfNotNull(sub);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unsubscribeAll();
    }

    public void showHint(String hint) {
        ToastUtils.showLong(hint);
    }

    public void showHintShort(String hint) {
        ToastUtils.show(hint);
    }

    public void showError(Throwable e) {
        ToastUtils.show(Utils.appContext.getString(R.string.error_to_connect_server));
    }

    public void showLoading() {
        showLoading(true);
    }

    public void showLoading(boolean cancellable) {
        mProgress.setCancellable(cancellable)
                .show();
    }

    protected void onDialogDismiss() {
        getPresenter().onDialogDismiss();
    }

    public void dismissLoading() {
        mProgress.dismiss();
    }
}
