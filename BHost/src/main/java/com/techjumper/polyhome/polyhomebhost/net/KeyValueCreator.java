package com.techjumper.polyhome.polyhomebhost.net;

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

    public static KeyValuePair fetchAPKInfo(String[] packages, String family_id) {
        return newPair()
                .put("platform", "1")
                .put("packages", packages)
                .put("family_id", family_id);
    }

    public static KeyValuePair fetchAPKList(String family_id) {
        return newPair()
                .put("platform", "1")
                .put("family_id", family_id);
    }

    public static KeyValuePair uploadLogs(String family_id, String original_filename, String file) {
        return newPair()
                .put("family_id", family_id)
                .put("original_filename", original_filename)
                .put("file", file);
    }

    public static KeyValuePair getUserInfo(String mac) {
        return newPair()
                .put("mac", mac);
    }

}
