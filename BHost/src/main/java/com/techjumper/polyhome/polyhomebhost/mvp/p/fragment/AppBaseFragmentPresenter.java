package com.techjumper.polyhome.polyhomebhost.mvp.p.fragment;

import com.techjumper.corelib.mvp.presenter.BaseFragmentPresenterImp;
import com.techjumper.corelib.rx.tools.RxUtils;
import com.techjumper.polyhome.polyhomebhost.R;
import com.techjumper.polyhome.polyhomebhost.entity.BaseEntity;
import com.techjumper.polyhome.polyhomebhost.mvp.v.fragment.AppBaseFragment;
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
public abstract class AppBaseFragmentPresenter<T extends AppBaseFragment> extends BaseFragmentPresenterImp<T> {
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
    public void onDestroyView() {
        unsubscribeAll();
        super.onDestroyView();
    }

    protected boolean processNetworkResult(BaseEntity entity) {
        return processNetworkResult(entity, true);
    }

    protected boolean processNetworkResult(BaseEntity entity, boolean notifyNoData) {
        if (NetHelper.isSuccess(entity))
            return true;
        if (entity != null) {
            if (entity.getError_code() == NetHelper.CODE_NOT_LOGIN) {
//                UserManager.INSTANCE.logout();
////                boolean shouldClose = (!(getView().getActivity() instanceof TabHomeActivity));
//                new AcHelper.Builder(getView().getActivity())
//                        .target(LoginActivity.class)
////                        .closeCurrent(shouldClose)
//                        .start();
//                getView().showHint(entity.getError_code() + ":" + entity.getError_msg());
            } else if (entity.getError_code() == NetHelper.CODE_NO_DATA) {
                if (notifyNoData)
                    getView().showHintShort(getView().getString(R.string.error_no_data));
            } else {
                getView().showHint(entity.getError_code() + ":" + entity.getError_msg());
            }
        } else
            getView().showError(null);
        return false;
    }

    public void onDialogDismiss() {

    }
}
