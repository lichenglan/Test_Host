package com.techjumper.corelib.mvp.presenter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.techjumper.corelib.mvp.interfaces.IBaseFragmentPresenter;
import com.techjumper.corelib.ui.fragment.BaseFragment;

import butterknife.ButterKnife;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 16/2/12
 * * * * * * * * * * * * * * * * * * * * * * *
 **/
public abstract class BaseFragmentPresenterImp<V> extends BasePresenterImp<V>
        implements IBaseFragmentPresenter<V> {


    protected BaseFragment mFragment;

    @Override
    public void onAttach(Context context) {
    }

    @Override
    public void setFragment(BaseFragment fragment) {
        mFragment = fragment;
    }

    @Override
    public void onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    }

    @Override
    public void bind(View view) {
        ButterKnife.bind(this, view);
    }

    @Override
    public void unBind() {
        ButterKnife.unbind(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onDestroyView() {

    }

    @Override
    public void onDetach() {

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {

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

    @Override
    public void onSaveState(Bundle saveBundle) {

    }

    @Override
    public void onSaveStateNoView(Bundle saveBundle) {

    }

    /**
     * 获得从{@link Fragment#setArguments(Bundle)} 传入的 Bundle
     *
     * @return 返回传入的bundle，如果为null则新建
     */
    protected Bundle getOutBundle() {
        if (mFragment == null) return new Bundle();
        if (mFragment.getArguments() == null)
            return new Bundle();
        else
            return mFragment.getArguments();
    }
}
