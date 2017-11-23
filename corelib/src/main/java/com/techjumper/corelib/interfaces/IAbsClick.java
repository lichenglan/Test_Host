package com.techjumper.corelib.interfaces;

import android.view.View;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 16/3/3
 * * * * * * * * * * * * * * * * * * * * * * *
 **/
public interface IAbsClick<T> {
    void onItemClick(View view, int position, T data);
}
