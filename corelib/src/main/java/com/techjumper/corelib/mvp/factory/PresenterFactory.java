package com.techjumper.corelib.mvp.factory;


import com.techjumper.corelib.mvp.interfaces.IPresenter;

public abstract class PresenterFactory<P extends IPresenter> {
    public abstract P createPresenter();
}
