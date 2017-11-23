package com.techjumper.corelib.mvp.presenter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.techjumper.corelib.mvp.interfaces.IActivityPresenter;
import com.techjumper.corelib.mvp.interfaces.IBaseActivityPresenter;
import com.techjumper.corelib.ui.activity.BaseActivity;

import butterknife.ButterKnife;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 16/2/12
 * * * * * * * * * * * * * * * * * * * * * * *
 **/
public abstract class BaseActivityPresenterImp<V> extends BasePresenterImp<V>
        implements IBaseActivityPresenter<V> {

    protected BaseActivity mActivity;

    @Override
    public void setActivity(BaseActivity activity) {
        mActivity = activity;
    }

    @Override
    public void bind() {
        ButterKnife.bind(this, mActivity);
    }

    @Override
    public void unBind() {
        ButterKnife.unbind(this);
    }

    @Override
    public void onCreate(Bundle saveInstanceState) {
    }

    @Override
    public void onStart() {
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onRestart() {

    }

    @Override
    public void onDestroy() {
    }

    @Override
    public void onSavedInstanceState(Bundle saveInstanceState) {
    }

    @Override
    public void onRestoreInstanceState(Bundle saveInstanceState) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    }

    protected Bundle getIntentBundle() {
        if (mActivity == null
                || mActivity.getIntent() == null
                || mActivity.getIntent().getExtras() == null)
            return new Bundle();
        else
            return mActivity.getIntent().getExtras();
    }


}
