package com.techjumper.corelib.entity.event;

import com.techjumper.corelib.ui.fragment.BaseFragment;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 16/3/31
 * * * * * * * * * * * * * * * * * * * * * * *
 **/
public class RemoveFragmentEvent {
    private Object from;
    private BaseFragment target;

    public RemoveFragmentEvent(Object from, BaseFragment target) {
        this.from = from;
        this.target = target;
    }

    public Object getFrom() {
        return from;
    }

    public BaseFragment getTarget() {
        return target;
    }

    public void setTarget(BaseFragment target) {
        this.target = target;
    }
}
