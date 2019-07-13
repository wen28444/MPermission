package win.permision;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import java.util.List;

/**
 * author: win
 * created on: 2019-07-13
 */
class MPermissionSettingHelper {

    //MIUI8以下的权限设置页路径
    private static final String MIUI_PERMISSION_ACTIVITY = "com.miui.permcenter.permissions.AppPermissionsEditorActivity";
    //MIUI8的新的权限页，注意这个权限页并不是MIUI8.0出现的，而是中间某个版本
    private static final String MIUI_NEW_PERMISSION_ACTIVITY = "com.miui.permcenter.permissions.PermissionsEditorActivity";

    public static Intent getPermissionSettingIntent(Context context) {
        Intent intent;
        if (isMIUI()) {
            intent = getMIUIPermissionSettings(context);
        } else if (isHUAWEI()) {
            intent = getHuaWeiPermissionSettings(context);
        } else {
            intent = getAppDetailSetting(context);
        }
        return intent;
    }

    /**
     * 检查手机是否是miui
     */
    public static boolean isMIUI() {
        String device = Build.MANUFACTURER;
        if ("Xiaomi".equals(device)) {
            return true;
        }
        return false;
    }

    /**
     * 检查手机是否是HUAWEI
     */
    public static boolean isHUAWEI() {
        String device = Build.MANUFACTURER;
        if ("HUAWEI".equals(device)) {
            return true;
        }
        return false;
    }

    public static Intent getAppNotificationSetting(Context context) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);

        //for Android 5-7
        intent.putExtra("app_package", context.getPackageName());
        intent.putExtra("app_uid", context.getApplicationInfo().uid);

        // for Android O
        intent.putExtra("android.provider.extra.APP_PACKAGE", context.getPackageName());

        if (isIntentAvailable(context, intent)) {
            return intent;
        }
        return getAppDetailSetting(context);
    }

    /**
     * 检查是否有这个activity
     *
     * @param context
     * @param intent
     * @return
     */
    private static boolean isIntentAvailable(Context context, Intent intent) {
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(
                intent, PackageManager.MATCH_DEFAULT_ONLY);
        return !list.isEmpty();
    }


    /**
     * 小米手机的应用权限设置页
     */
    private static Intent getMIUIPermissionSettings(Context context) {
        Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
        intent.putExtra("extra_pkgname", context.getPackageName());

        //MIUI8
        intent.setClassName("com.miui.securitycenter", MIUI_NEW_PERMISSION_ACTIVITY);
        if (isIntentAvailable(context, intent)) {
            return intent;
        }

        //MIUI8以下
        intent.setClassName("com.miui.securitycenter", MIUI_PERMISSION_ACTIVITY);
        if (isIntentAvailable(context, intent)) {
            return intent;
        }
        return getAppDetailSetting(context);
    }


    /**
     * 跳转到华为的权限列表页面
     *
     * @return
     */
    private static Intent getHuaWeiPermissionSettings(Context context) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MainActivity"));

        if (isIntentAvailable(context, intent)) {
            return intent;
        }

        return getAppDetailSetting(context);
    }

    public static Intent getAppDetailSetting(Context context) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + context.getPackageName()));

        if (isIntentAvailable(context, intent)) {
            return intent;
        }

        return getSettingIntent(context);
    }

    public static Intent getSettingIntent(Context context) {
        Intent intent = new Intent(Settings.ACTION_SETTINGS);
        return intent;
    }
}
