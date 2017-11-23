package com.techjumper.corelib.mvp.interfaces;


/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 16/2/11
 * * * * * * * * * * * * * * * * * * * * * * *
 **/
public interface IView<P extends IPresenter> {

    P getPresenter();
}
