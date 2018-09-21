package com.example.simplelauncher;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 权限控制工具类：
 * 为了适配API23，即Android M 在清单文件中配置use permissions后，还要在程序运行的时候进行申请。
 *
 * ***整个权限的申请与处理的过程是这样的：
 * *****1.进入主Activity，首先申请所有的权限；
 * *****2.用户对权限进行授权，有2种情况：
 * ********1).用户Allow了权限，则表示该权限已经被授权，无须其它操作；
 * ********2).用户Deny了权限，则下次启动Activity会再次弹出系统的Permisssions申请授权对话框。
 * *****3.如果用户Deny了权限，那么下次再次进入Activity，会再次申请权限，这次的权限对话框上，会有一个选项“never ask again”：
 * ********1).如果用户勾选了“never ask again”的checkbox，下次启动时就必须自己写Dialog或者Snackbar引导用户到应用设置里面去手动授予权限；
 * ********2).如果用户未勾选上面的选项，若选择了Allow，则表示该权限已经被授权，无须其它操作；
 * ********3).如果用户未勾选上面的选项，若选择了Deny，则下次启动Activity会再次弹出系统的Permissions申请授权对话框。
 */
public class PermissionsUtil {
    private static final String TAG = "PermissionsUtil";

    // 状态码、标志位
    private static final int REQUEST_STATUS_CODE = 0x001;
    private static final int REQUEST_PERMISSION_SETTING = 0x002;

    //常量字符串数组，将需要忽略的权限写进去，同时必须要在AndroidManifest.xml中声明。
    private static String[] PERMISSIONS_IGNORE = {
            Manifest.permission.SYSTEM_ALERT_WINDOW,
            Manifest.permission.READ_LOGS,
            Manifest.permission.WRITE_SETTINGS,
            Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
    };

    public static void checkAndRequestPermissions(final Activity activity) {
        // 版本判断。当手机系统大于 23 时，才有必要去判断权限是否获取
        if (Build.VERSION.SDK_INT < 23) return;

        // 一个list，用来存放没有被授权的权限
        ArrayList<String> deniedArray = new ArrayList<>();

        // 遍历PERMISSIONS_GROUP，将没有被授权的权限存放进deniedArray
        String[] permissions = null;
        try {
            permissions = activity.getPackageManager().getPackageInfo(
                    activity.getPackageName(), PackageManager.GET_PERMISSIONS).requestedPermissions;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (permissions != null && permissions.length > 0) {
            List<String> ignored_List = Arrays.asList(PERMISSIONS_IGNORE);
            for (String permission : permissions) {
//                Log.i(TAG, "Requested Permission: "+permission);

                // Not in ignored list
                if (ignored_List != null && ignored_List.contains(permission)) {
//                    Log.i(TAG, "Ignored Permission: "+permission);
                    continue;
                }

                int grantCode = ActivityCompat.checkSelfPermission(activity, permission);
                if (grantCode == PackageManager.PERMISSION_DENIED) {
                    deniedArray.add(permission);
                }
            }
        }

        // 将denidArray转化为字符串数组，方便下面调用requestPermissions来请求授权
        String[] deniedPermissions = deniedArray.toArray(new String[deniedArray.size()]);

        // 如果该字符串数组长度大于0，说明有未被授权的权限
        if (deniedPermissions.length > 0) {
            requestPermissions(activity, deniedPermissions);
        }
    }

    /**
     * 对权限字符串数组中的所有权限进行申请授权，如果用户选择了“never ask again”，则不会弹出系统的Permission申请授权对话框
     */
    public static void requestPermissions(Activity activity, String[] permissions) {
        ActivityCompat.requestPermissions(activity, permissions, REQUEST_STATUS_CODE);
    }
}
