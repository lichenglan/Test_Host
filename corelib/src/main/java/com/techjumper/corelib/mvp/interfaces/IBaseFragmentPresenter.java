package com.techjumper.corelib.mvp.interfaces;

import android.os.Bundle;
import android.view.View;

import com.techjumper.corelib.ui.fragment.BaseFragment;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 16/2/12
 * * * * * * * * * * * * * * * * * * * * * * *
 **/
public interface IBaseFragmentPresenter<V> extends IFragmentPresenter<V> {

    void setFragment(BaseFragment fragment);

    void initData(Bundle savedInstanceState);

    void onViewInited(Bundle savedInstanceState);

    void bind(View view);

    void unBind();

    void onSaveState(Bundle saveBundle);

    void onSaveStateNoView(Bundle saveBundle);
}
