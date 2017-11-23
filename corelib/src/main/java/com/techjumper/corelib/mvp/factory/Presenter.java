package com.techjumper.corelib.mvp.factory;

import com.techjumper.corelib.mvp.interfaces.IPresenter;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface Presenter {
    Class<? extends IPresenter> value();
}
