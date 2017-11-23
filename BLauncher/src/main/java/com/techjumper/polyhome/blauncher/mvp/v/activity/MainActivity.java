package com.techjumper.polyhome.blauncher.mvp.v.activity;

import android.os.Bundle;
import android.view.View;

import com.techjumper.corelib.mvp.factory.Presenter;
import com.techjumper.polyhome.blauncher.R;
import com.techjumper.polyhome.blauncher.mvp.p.activity.MainActivityPresenter;

@Presenter(MainActivityPresenter.class)
public class MainActivity extends AppBaseActivity<MainActivityPresenter> {

    @Override
    protected View inflateView(Bundle savedInstanceState) {
        return inflate(R.layout.activity_main);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
    }

}
