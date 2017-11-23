package com.techjumper.plugincommunicateengine.entity.core;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 16/5/17
 * * * * * * * * * * * * * * * * * * * * * * *
 **/
public class BaseMessageEntity<T> {

    private T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
