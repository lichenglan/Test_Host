package com.techjumper.corelib.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.techjumper.corelib.mvp.factory.ReflectionPresenterFactory;
import com.techjumper.corelib.mvp.interfaces.IBaseFragmentPresenter;
import com.techjumper.corelib.mvp.interfaces.IView;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 16/2/12
 * * * * * * * * * * * * * * * * * * * * * * *
 **/

/**
 * 名字里的View代表MVP中的V
 */
public abstract class BaseViewFragment<P extends IBaseFragmentPresenter> extends BaseFragment
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
    public void onAttach(Context context) {
        super.onAttach(context);
        getPresenter().setFragment(this);
        getPresenter().onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPresenter().onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        getPresenter().onCreateView(inflater, container, savedInstanceState);
        getPresenter().bind(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getPresenter().onActivityCreated(savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();
        getPresenter().onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        getPresenter().onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        getPresenter().onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        getPresenter().onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getPresenter().onDestroyView();
        getPresenter().unBind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getPresenter().onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        getPresenter().onDetach();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getPresenter().onSavedInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        getPresenter().onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        getPresenter().setUserVisibleHint(isVisibleToUser);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getPresenter().onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onSaveState(Bundle saveBundle) {
        super.onSaveState(saveBundle);
        getPresenter().onSaveState(saveBundle);
    }

    @Override
    protected void onSaveStateNoView(Bundle saveBundle) {
        super.onSaveStateNoView(saveBundle);
        getPresenter().onSaveStateNoView(saveBundle);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        getPresenter().onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
