package win.permision;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;


/**
 * author: win
 * created on: 2019-07-13
 */
public class MPermission {
    public static final String TAG = "MPermission";

    /**
     * 动态权限申请方法
     */
    public static void requestPermission(@NonNull MPermissionRequest request) {
        if (isOverMarshmallow()) {
            verifyObject(request.getContext());

            FragmentActivity activity = getActivity(request.getContext());
            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            MPermissionFragment mPermissionFragment = (MPermissionFragment) fragmentManager.findFragmentByTag(MPermissionFragment.TAG);

            if (mPermissionFragment == null) {
                mPermissionFragment = new MPermissionFragment();
                fragmentManager.beginTransaction()
                        .add(mPermissionFragment, MPermissionFragment.TAG)
                        .commitAllowingStateLoss();
                fragmentManager.executePendingTransactions();
            }
            mPermissionFragment.requestPermission(request.getPermissions(), request.getCallback());
        } else {
            if (request.getCallback() != null) {
                request.getCallback().onSucceed(request.permissionArray());
            }
        }
    }

    /**
     * 权限检查方法，可传多个权限
     * */
    public static boolean checkPermission(@NonNull Context context, @NonNull String... permissions) {
        if (isOverMarshmallow()) {
            for (String permission: permissions) {
                if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        } else {
            for (String permission: permissions) {
                if (!MPermissionChecker.checkPermission(context, permission)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 权限检查方法
     * */
    public static boolean checkPermission(@NonNull Context context, @NonNull String permission) {
        boolean enable;
        if (isOverMarshmallow()) {
            int result = context.checkSelfPermission(permission);
            enable = result == PackageManager.PERMISSION_GRANTED;
        } else {
            enable = MPermissionChecker.checkPermission(context, permission);
        }
        return enable;
    }

    /**
     * 检查权限是否被用户选择了不再提示
     * */
    public static boolean somePermissionPermanentlyDenied(@NonNull Context context, @NonNull String... perms) {
        if (isOverMarshmallow()) {
            if (context instanceof Activity) {
                Activity activity = (Activity) context;
                for (String deniedPermission : perms) {
                    if (permissionPermanentlyDenied(activity, deniedPermission)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static boolean permissionPermanentlyDenied(@NonNull Activity activity, @NonNull String perms) {
        return !ActivityCompat.shouldShowRequestPermissionRationale(activity, perms);
    }

    /**
     * 支持查询4.4及以上系统的消息通知权限状态
     * */
    public static boolean isNotificationEnabled(@NonNull Context context) {
        return MPermissionChecker.checkNotification(context);
    }

    /**
     * 跳转到设置页面
     *
     * @return
     */
    public static void gotoSetting(Context context) {
        try {
            Intent intent = MPermissionSettingHelper.getSettingIntent(context);
            context.startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    /**
     * 跳转到应用详情页
     * */
    public static void gotoAppDetailSetting(@NonNull Context context) {
        try {
            Intent intent = MPermissionSettingHelper.getAppDetailSetting(context);
            context.startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    /**
     * 跳转权限设置页
     * */
    public static void goPermissionActivity(@NonNull Context context) {
        try {
            Intent intent = MPermissionSettingHelper.getPermissionSettingIntent(context);
            context.startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    /**
     * 跳转通知设置页，没有就跳到app详情页
     * */
    public static void gotoAppNotificationSetting(@NonNull Context context) {
        try {
            Intent intent = MPermissionSettingHelper.getAppNotificationSetting(context);
            context.startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    private static FragmentActivity getActivity(Object context) {
        if (context instanceof Fragment) {
            return ((Fragment) context).getActivity();
        } else {
            return (FragmentActivity) context;
        }
    }

    /**
     * 检查参数有效性
     *
     * @param object 只支持support 的activity 和 Fragment
     */
    private static void verifyObject(Object object) {
        boolean success = false;
        if (object instanceof FragmentActivity) {
            success = true;
        } else if (object instanceof Fragment) {
            success = true;
        }
        if (!success) {
            throw new IllegalArgumentException("object should be support Activity or Fragment !!!");
        }
    }

    /**
     * 判断当前设备Android版本是否 >= 6.0
     */
    public static boolean isOverMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    /**
     * 判断当前设备Android版本是否 >= 5.0
     * */
    public static boolean isOverLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }
}