package com.techjumper.corelib.mvp.factory;

import com.techjumper.corelib.mvp.interfaces.IPresenter;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 16/2/11
 * * * * * * * * * * * * * * * * * * * * * * *
 **/
public class ReflectionPresenterFactory<P extends IPresenter> extends PresenterFactory<P> {

    private Class<P> presenterClass;

    private ReflectionPresenterFactory(Class<P> presenterClass) {
        this.presenterClass = presenterClass;
    }

    @SuppressWarnings("unchecked")
    public static <P extends IPresenter> ReflectionPresenterFactory<P> from(Class<?> viewClass) {
        Presenter annotation = viewClass.getAnnotation(Presenter.class);
        Class<P> presenterClass = annotation == null ? null : (Class<P>) annotation.value();
        return presenterClass == null ? null : new ReflectionPresenterFactory<>(presenterClass);
    }

    @Override
    public P createPresenter() {
        P presenter;
        try {
            presenter = presenterClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return presenter;
    }
}
