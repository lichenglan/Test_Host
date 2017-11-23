package com.techjumper.corelib.utils.system;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 16/2/14
 * * * * * * * * * * * * * * * * * * * * * * *
 **/

//Manifest.permission.READ_EXTERNAL_STORAGE

public class PermissionHelper {

    /**
     * 检察权限
     * @param ac 发起请求的Activity
     * @param requestCode 请求码,可任意填写
     * @param permission 请求的权限
     * @return 如果为true代表获取到权限, 如果为false则会回调 {@link android.support.v4.app.FragmentActivity#onRequestPermissionsResult}
     */
    public static boolean checkAndRequestPermission(Activity ac, int requestCode, String permission) {

        PackageManager pm = ac.getPackageManager();
        boolean hasPermission = pm.checkPermission(permission, ac.getPackageName())
                == PackageManager.PERMISSION_GRANTED;
        if (hasPermission) return true;

        ActivityCompat.requestPermissions(ac, new String[]{permission}, requestCode);
        return false;
    }

}
