package com.techjumper.corelib.mvp.interfaces;



/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 16/2/11
 * * * * * * * * * * * * * * * * * * * * * * *
 **/
public interface IPresenter<View> {

    void attachView(View view);

    boolean hasView();

    View getView();

    void dropView();


}
