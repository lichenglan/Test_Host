package com.techjumper.corelib.entity.event;


import com.techjumper.corelib.ui.fragment.BaseFragment;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 16/2/17
 * * * * * * * * * * * * * * * * * * * * * * *
 **/
public class SwitchFragmentEvent {
    private Object from;
    private BaseFragment target;

    public SwitchFragmentEvent(Object from, BaseFragment target) {
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
