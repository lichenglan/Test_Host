package com.techjumper.corelib.mvp.interfaces;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 16/2/12
 * * * * * * * * * * * * * * * * * * * * * * *
 **/
public interface IFragmentPresenter<View> extends IActivityPresenter<View> {

    void onAttach(Context context);

    void onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    void onActivityCreated(@Nullable Bundle savedInstanceState);

    void onDestroyView();

    void onDetach();

    void setUserVisibleHint(boolean isVisibleToUser);

    void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults);
}
