package com.techjumper.corelib.mvp.interfaces;

import android.os.Bundle;

import com.techjumper.corelib.ui.activity.BaseActivity;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 16/2/12
 * * * * * * * * * * * * * * * * * * * * * * *
 **/
public interface IBaseActivityPresenter<View> extends IActivityPresenter<View> {

    void setActivity(BaseActivity activity);

    void initData(Bundle savedInstanceState);

    void onViewInited(Bundle savedInstanceState);

    void bind();

    void unBind();
}
