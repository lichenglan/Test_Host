package com.techjumper.polyhome.polyhomebhost.mvp.p.activity;

import com.techjumper.corelib.mvp.presenter.BaseActivityPresenterImp;
import com.techjumper.corelib.rx.tools.RxUtils;
import com.techjumper.polyhome.polyhomebhost.R;
import com.techjumper.polyhome.polyhomebhost.entity.BaseEntity;
import com.techjumper.polyhome.polyhomebhost.mvp.v.activity.AppBaseActivity;
import com.techjumper.polyhome.polyhomebhost.net.NetHelper;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 16/2/23
 * * * * * * * * * * * * * * * * * * * * * * *
 **/
public abstract class AppBaseActivityPresenter<T extends AppBaseActivity> extends BaseActivityPresenterImp<T> {
    private List<Subscription> mSubList = new ArrayList<>();

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
    public void onDestroy() {
        unsubscribeAll();
        super.onDestroy();
    }

    protected boolean processNetworkResult(BaseEntity entity) {
        return processNetworkResult(entity, true);
    }

    protected boolean processNetworkResult(BaseEntity entity, boolean isShowMessage) {
        if (NetHelper.isSuccess(entity))
            return true;
        if (entity != null) {
            if (isShowMessage) {
                getView().showHint(entity.getError_code() + ":" + entity.getError_msg());
            }
            if (entity.getError_code() == NetHelper.CODE_NOT_LOGIN) {
//                UserManager.INSTANCE.logout();
//                boolean shouldClose = (!(getView() instanceof TabHomeActivity));
//                new AcHelper.Builder(getView())
//                        .target(LoginActivity.class)
//                        .closeCurrent(shouldClose)
//                        .start();
            } else if (entity.getError_code() == NetHelper.CODE_NO_DATA) {
                getView().showHintShort(getView().getString(R.string.error_no_data));
            }
        } else
            getView().showError(null);
        return false;
    }

    public void onDialogDismiss() {

    }
}
