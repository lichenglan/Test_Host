package com.techjumper.corelib.mvp.presenter;



import com.techjumper.corelib.mvp.interfaces.IPresenter;

import butterknife.ButterKnife;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 16/2/11
 * * * * * * * * * * * * * * * * * * * * * * *
 **/
public class BasePresenterImp<V> implements IPresenter<V> {

    protected V mView;

    @Override
    public void attachView(V v) {
        mView = v;
    }

    @Override
    public boolean hasView() {
        return mView != null;
    }

    @Override
    public V getView() {
        return mView;
    }

    @Override
    public void dropView() {
        mView = null;
    }
}
