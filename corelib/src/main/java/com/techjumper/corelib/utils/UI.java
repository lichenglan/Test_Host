package com.techjumper.corelib.utils;

import android.app.Activity;
import android.util.SparseArray;
import android.view.View;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 16/1/8
 * * * * * * * * * * * * * * * * * * * * * * *
 **/
public class UI {

    private Object item;

    private UI(Object obj) {
        item = obj;
    }

    public static UI create(View view) {
        return new UI(view);
    }

    public static UI create(Activity ac) {
        return new UI(ac);
    }

    @SuppressWarnings("unchecked")
    public <T> T findById(int id) {
        View view = null;
        if (item instanceof Activity) {
            view = ((Activity) item).findViewById(id);
        } else if (item instanceof View) {
            view = ((View) item).findViewById(id);
        }
        return (T) view;
    }

    @SuppressWarnings("unchecked")
    public <T extends View> T getHolderView(int id) {
        if (!(item instanceof View))
            throw new ClassCastException("only View can be used");

        View view = (View) item;

        SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
        if (viewHolder == null) {
            viewHolder = new SparseArray<>();
            view.setTag(viewHolder);
        }

        View childView = viewHolder.get(id);
        if (childView == null) {
            childView = view.findViewById(id);
            viewHolder.put(id, childView);
        }

        return (T) childView;

    }


}
