package win.permision;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AppOpsManager;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.database.Cursor;
import android.hardware.Camera;
import android.os.Build;
import android.os.Environment;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 该类用于Android5.0、5.1系统权限检查
 *
 * author: win
 * created on: 2019-07-13
 */
class MPermissionChecker {

    static boolean checkPermission(@NonNull Context context, @NonNull String permission) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return true;
        }

        switch (permission) {
            case Manifest.permission.WRITE_EXTERNAL_STORAGE:
            case Manifest.permission.READ_EXTERNAL_STORAGE:
                return checkStorage(context);
            case Manifest.permission.CAMERA:
                return checkCamera(context);
            case Manifest.permission.READ_CONTACTS:
                return checkContact(context);
            case Manifest.permission.READ_CALL_LOG:
                return checkCallLog(context);
            default:
                return true;
        }
    }

    /**
     * 检查是否有SD卡权限或者SD卡是否可用
     */
    private static boolean checkStorage(Context context) {
        boolean enable;
        try {
            String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
            File testFile = new File(sdPath, ".mymoney_permission");
            if (testFile.exists()) {
                enable = testFile.delete();
            } else {
                enable = testFile.createNewFile();
            }
        } catch (Exception e) {
            enable = false;
        }
        return enable;
    }

    /**
     * 检查相机是否可用
     * */
    private static boolean checkCamera(Context context) {
        boolean enable = true;
        Camera camera = null;
        try {
            camera = Camera.open();
            Camera.Parameters parameters = camera.getParameters();
            camera.setParameters(parameters);
        } catch (Exception e) {
            enable = false;
        } finally {
            if (camera != null) {
                camera.release();
            }
        }
        return enable;
    }

    /**
     * 通话记录权限检查
     * */
    @SuppressLint("MissingPermission")
    private static boolean checkCallLog(Context context) {
        boolean enable = true;
        Cursor cursor = null;
        try {
            ContentResolver resolver = context.getContentResolver();
            cursor = resolver.query(CallLog.Calls.CONTENT_URI, null, null, null, null);
            if (cursor == null) {
                enable = false;
            } else {
                if (cursor.moveToNext()) {
                    cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME));
                    cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
                }
            }
        } catch (Exception e) {
            enable = false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return enable;
    }

    /**
     * 通讯录权限检查（魅族、小米）
     * */
    private static boolean checkContact(Context context) {
        boolean enable = true;
        Cursor cursor = null;
        try {
            ContentResolver resolver = context.getContentResolver();
            cursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
            if (cursor == null) {
                enable = false;
            } else {
                if (cursor.moveToNext()) {
                    cursor.getString(cursor.getColumnIndex(ContactsContract.Data.DATA1));
                    cursor.getString(cursor.getColumnIndex(ContactsContract.RawContacts.DISPLAY_NAME_PRIMARY));
                }
            }
        } catch (Exception e) {
            enable = false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return enable;
    }

    /**
     * 消息通知不属于权限范畴，正常来说是不需要检查的。由于项目中需要，该方法属于特殊存在
     *
     * 支持查询4.4及以上系统的消息通知权限状态
     * */
    static boolean checkNotification(Context context) {

        try {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                return checkAfterApi24(context);
            } else {
                return check(context, "OP_POST_NOTIFICATION");
            }
        } catch (Exception e) {

        }
        return true;
    }

    // FIXME: 2017/5/12 新版本SDK已经封装该方法，如后期升级可替换除该方法
    private static boolean check(Context context, String permission) throws Exception {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        ApplicationInfo appInfo = context.getApplicationInfo();
        String pkg = context.getApplicationContext().getPackageName();
        int uid = appInfo.uid;
        Class appOpsClass = Class.forName(AppOpsManager.class.getName());
        Method checkOpNoThrowMethod = appOpsClass.getMethod("checkOpNoThrow", Integer.TYPE, Integer.TYPE, String.class);
        Field opPostNotificationValue = appOpsClass.getDeclaredField(permission);
        int value = (int) opPostNotificationValue.get(Integer.class);
        int mode = (int) checkOpNoThrowMethod.invoke(appOps, value, uid, pkg);

        return mode == AppOpsManager.MODE_ALLOWED;
    }

    private static boolean checkAfterApi24(Context context) {
        try {
            NotificationManager mNotificationManager = null;
            Class<?> c = Class.forName("android.app.NotificationManager");
            Method method = c.getMethod("getService");
            Object obj = method.invoke(mNotificationManager);
            Class<?> clazz = Class.forName("android.app.INotificationManager$Stub$Proxy");
            Method areNotificationsEnabledForPackage = clazz.getMethod("areNotificationsEnabledForPackage", String.class, int.class);
            return (boolean) areNotificationsEnabledForPackage.invoke(obj, context.getPackageName(), android.os.Process.myUid());
        } catch (Exception e) {

        }

        return true;
    }
}