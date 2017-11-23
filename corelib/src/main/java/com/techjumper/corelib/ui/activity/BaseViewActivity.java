package com.techjumper.corelib.ui.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.techjumper.corelib.mvp.factory.ReflectionPresenterFactory;
import com.techjumper.corelib.mvp.interfaces.IBaseActivityPresenter;
import com.techjumper.corelib.mvp.interfaces.IView;


/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 16/2/11
 * * * * * * * * * * * * * * * * * * * * * * *
 **/

/**
 * 名字里的View代表MVP中的V
 */
public abstract class BaseViewActivity<P extends IBaseActivityPresenter> extends BaseActivity
        implements IView<P> {

    private P mPresenter;
    private static final Object mLock = new Object();


    @SuppressWarnings("unchecked")
    @Override
    public P getPresenter() {
        if (mPresenter == null) {
            mPresenter = ReflectionPresenterFactory.<P>from(getClass()).createPresenter();
        }
        if (!mPresenter.hasView()) {
            synchronized (mLock) {
                if (!mPresenter.hasView()) {
                    mPresenter.attachView(this);
                }
            }
        }
        return mPresenter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPresenter().setActivity(this);
        getPresenter().bind();
        getPresenter().onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getPresenter().onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPresenter().onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        getPresenter().onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        getPresenter().onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getPresenter().onRestart();
    }

    @Override
    protected void onDestroy() {
        getPresenter().onDestroy();
        getPresenter().unBind();
        mPresenter.dropView();
        mPresenter = null;
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getPresenter().onSavedInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        getPresenter().onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        getPresenter().initData(savedInstanceState);
    }

    @Override
    protected void onViewInited(Bundle savedInstanceState) {
        getPresenter().onViewInited(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getPresenter().onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        getPresenter().onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
