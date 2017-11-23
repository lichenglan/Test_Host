package com.techjumper.polyhome.polyhomebhost.utils;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.techjumper.corelib.utils.Utils;
import com.techjumper.corelib.utils.common.JLog;
import com.techjumper.polyhome.polyhomebhost.receiver.AdminReceiver;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 16/7/21
 * * * * * * * * * * * * * * * * * * * * * * *
 **/
public class PolicyUtil {
    private PolicyUtil() {
    }

    public static void lockOrRequest(Activity ac, int requestCode) {
        DevicePolicyManager policyManager = getDPM();
        if (hasLockPermission()) {
            policyManager.lockNow();// 锁屏
            JLog.d("有权限, 并关闭屏幕");
        } else {
            JLog.d("没有权限, 去请求权限");
            requestLockPermision(ac, requestCode);
        }
    }

    public static boolean hasLockPermission() {
//        DevicePolicyManager dpm = getDPM();
//        ComponentName cn = new ComponentName(Utils.appContext, AdminReceiver.class);
//        return dpm.isAdminActive(cn);
        return true;
    }

    public static DevicePolicyManager getDPM() {
        return (DevicePolicyManager) Utils.appContext.getSystemService(Context.DEVICE_POLICY_SERVICE);
    }

    public static void lockDirectly() {
        try {
            getDPM().lockNow();
        } catch (Exception ignored) {
        }
    }

    public static void requestLockPermision(Activity ac, int requestCode) {
        // 启动设备管理(隐式Intent) - 在AndroidManifest.xml中设定相应过滤器
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);

        // 权限列表
        ComponentName cn = new ComponentName(Utils.appContext, AdminReceiver.class);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, cn);

        // 描述(additional explanation) 在申请权限时出现的提示语句
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                "为了节省系统资源并让程序正常运行, 请给予锁屏权限");
        ac.startActivityForResult(intent, requestCode);
    }

    public static boolean onLockActivityResult(int lastRequestCode, int returnRequestCode, int resultCode) {
        if (returnRequestCode != lastRequestCode) {
            return false;
        }
        if (resultCode == Activity.RESULT_OK) {
            getDPM().lockNow();
            JLog.d("请求到了权限, 关闭屏幕");
            return true;
        } else {
            JLog.d("没有获取到权限, 无法关闭屏幕");
            return false;
        }

    }
}
