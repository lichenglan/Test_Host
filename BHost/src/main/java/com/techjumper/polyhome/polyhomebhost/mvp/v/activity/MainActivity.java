package com.techjumper.polyhome.polyhomebhost.mvp.v.activity;

import android.os.Bundle;
import android.view.View;

import com.techjumper.corelib.mvp.factory.Presenter;
import com.techjumper.corelib.utils.common.AcHelper;
import com.techjumper.polyhome.polyhomebhost.R;
import com.techjumper.polyhome.polyhomebhost.mvp.p.activity.MainActivityPresenter;

@Presenter(MainActivityPresenter.class)
public class MainActivity extends AppBaseActivity<MainActivityPresenter> {

    @Override
    protected View inflateView(Bundle savedInstanceState) {
        return inflate(R.layout.activity_main);
    }


    @Override
    protected void initView(Bundle savedInstanceState) {
        AcHelper.fullScreen(this, true);
    }


}
