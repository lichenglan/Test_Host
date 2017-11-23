package com.techjumper.polyhome.blauncher.net;

import com.techjumper.lib2.others.KeyValuePair;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 16/3/3
 * * * * * * * * * * * * * * * * * * * * * * *
 **/
public class KeyValueCreator {

    private static KeyValuePair<String, Object> newPair() {
        return new KeyValuePair<>();
    }

    public static KeyValuePair fetchAPKInfo(String[] packages) {
        return newPair()
                .put("platform", "1")
                .put("packages", packages);
    }

}
