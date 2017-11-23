package com.techjumper.lib2.others;

import java.util.HashMap;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 16/3/3
 * * * * * * * * * * * * * * * * * * * * * * *
 **/
public class KeyValuePair<T, K> {

    private HashMap<T, K> mMap = new HashMap<>();

    public KeyValuePair<T, K> put(T key, K value) {
        mMap.put(key, value);
        return this;
    }

    public K get(T key) {
        return mMap.get(key);
    }

    public HashMap<T, K> toMap() {
        return mMap;
    }

}
