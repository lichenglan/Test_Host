package com.techjumper.corelib.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.techjumper.corelib.R;
import com.techjumper.corelib.interfaces.IAbsClick;
import com.techjumper.corelib.utils.UI;

import java.util.List;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 16/2/20
 * * * * * * * * * * * * * * * * * * * * * * *
 **/
public abstract class JumperBaseAdapter<T> extends BaseAdapter {

    protected Context mContext;
    protected List<T> mDataList;

    private IAbsClick<T> iAbsClick;

    private View.OnClickListener onClickListener = v -> {
        int position = (int) v.getTag(R.id.tag_second);
        if (iAbsClick != null) {
            iAbsClick.onItemClick(v, position, getItem(position));
        }
    };

    public JumperBaseAdapter(Context context, List<T> mDataList) {
        this.mContext = context;
        this.mDataList = mDataList;
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public T getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflate(LayoutInflater.from(mContext), position);
        }
        UI ui = UI.create(convertView);
        onBindView(position, convertView, parent, ui);
        return convertView;
    }

    protected abstract View inflate(LayoutInflater inflater, int position);

    protected abstract void onBindView(int position, View convertView, ViewGroup parent, UI ui);

    public void setOnItemClickListener(IAbsClick<T> iAbsClick) {
        this.iAbsClick = iAbsClick;
    }

    protected void setOnItemClick(View view, int position) {
        view.setTag(R.id.tag_second, position);
        view.setOnClickListener(onClickListener);
    }

}
