package win.permision;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

/**
 * author: win
 * created on: 2019-07-13
 */
public class MPermissionFragment extends Fragment {
    public static final String TAG = "MPermissionFragment";

    private static final int REQUEST_CODE_SETTING = 0x0010;
    private static final int REQUEST_CODE_PERMISSION = 0x0011;

    private List<Permission> mGrantedPermissions = new ArrayList<>();
    private List<Permission> mDeniedPermissions = new ArrayList<>();
    private MPermissionListener mMPermissionListener;

    public MPermissionFragment() {
        setRetainInstance(true);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (REQUEST_CODE_PERMISSION == requestCode) {
            for (int i=0; i<permissions.length; i++) {
                boolean granted = grantResults[i] == PackageManager.PERMISSION_GRANTED;
                if (granted) {
                    addToGranted(permissions[i]);
                }
            }
            checkShowPermissionDialog();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SETTING) {
            if (mDeniedPermissions.size() > 0) {
                boolean checkShow = false;
                String[] deniedArray = getPermissionArray(mDeniedPermissions);
                for (String permission: deniedArray) {
                    if (ActivityCompat.checkSelfPermission(getActivity(), permission) == PackageManager.PERMISSION_GRANTED) {
                        addToGranted(permission);
                        checkShow = true;
                    }
                }
                if (checkShow) {
                    checkShowPermissionDialog();
                } else {
                    permissionFailed(mDeniedPermissions);
                }
            }
        }
    }

    private void checkShowPermissionDialog() {
        if (mDeniedPermissions.size() == 0) {
            permissionSuccess(mGrantedPermissions);
        } else {
            //当用户拒绝了权限并且勾选了不再询问，则shouldShowRequestPermissionRationale为false
            //Permission permission = mDeniedPermissions.get(0);
            //if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permission.getPermission())) {
            //    showRationaleDialog(permission.getRationale());
            //} else
            Permission mustRequest = null;
            for (Permission permission: mDeniedPermissions) {
                if (permission.isMustRequest()) {
                    mustRequest = permission;
                    break;
                }
            }
            if (mustRequest != null && !TextUtils.isEmpty(mustRequest.getRationale())) {
                showRationaleDialog(mustRequest.getRationale());
            } else {
                permissionFailed(mDeniedPermissions);
            }
        }
    }

    /**
     * 请求权限
     *
     * @param permissions 请求的权限
     */
    public void requestPermission(List<Permission> permissions, MPermissionListener permissionListener) {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        mMPermissionListener = permissionListener;
        mGrantedPermissions.clear();
        mDeniedPermissions.clear();
        if (isOverMarshmallow()) {
            for (Permission permission : permissions) {
                if (ContextCompat.checkSelfPermission(getActivity(), permission.getPermission()) == PackageManager.PERMISSION_GRANTED) {
                    mGrantedPermissions.add(permission);
                } else {
                    mDeniedPermissions.add(permission);
                }
            }
            if (mDeniedPermissions.size() == 0) {
                permissionSuccess(permissions);
            } else {
                requestPermissions(getPermissionArray(mDeniedPermissions), REQUEST_CODE_PERMISSION);
            }
        } else {
            permissionSuccess(permissions);
        }
    }

    /**
     * 判断当前手机Android版本是否 >= 6.0
     */
    private static boolean isOverMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    /**
     * 检测所有的权限是否都已授权
     *
     * @param permissions
     * @return
     */
    private boolean checkPermissions(String[] permissions) {
        if (isOverMarshmallow()) {
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(getActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 确认所有的权限是否都已授权
     *
     * @param grantResults
     * @return
     */
    private boolean verifyPermissions(int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void addToGranted(String permission) {
        Permission permissionGranted = null;
        for (Permission p: mDeniedPermissions) {
            if (permission.equals(p.getPermission())) {
                permissionGranted = p;
                break;
            }
        }
        if (permissionGranted != null) {
            boolean result = mDeniedPermissions.remove(permissionGranted);
            if (result) {
                mGrantedPermissions.add(permissionGranted);
            }
        }
    }

    private void showRationaleDialog(String desc) {
        new MPermissionDialog.Builder(getActivity())
                .setStyle(R.style.PermissionDialog)
                .setTitle("权限申请")
                .setMessage(desc)
                .setNegativeListener(new MPermissionDialog.NegativeClickListener() {
                    @Override
                    public void onClick() {
                        permissionFailed(mDeniedPermissions);
                    }
                }, "取消")
                .setPositiveListener(new MPermissionDialog.PositiveClickListener() {
                    @Override
                    public void onClick() {
                        startAppSettings();
                    }
                }, "去设置")
                .create().show();
    }

    /**
     * 启动当前应用设置页面
     */
    private void startAppSettings() {
        if (getActivity() != null) {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + getActivity().getPackageName()));
            if (isIntentAvailable(intent)) {
                startActivityForResult(intent, REQUEST_CODE_SETTING);
            }
        }
    }

    private boolean isIntentAvailable(final Intent intent) {
        return getActivity().getPackageManager()
                .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
                .size() > 0;
    }

    private String[] getPermissionArray(List<Permission> list) {
        String[] permissions = new String[list.size()];
        for (int i=0; i<list.size(); i++) {
            permissions[i] = list.get(i).getPermission();
        }
        return permissions;
    }

    private void permissionSuccess(List<Permission> list) {
        if (mMPermissionListener != null) {
            mMPermissionListener.onSucceed(getPermissionArray(list));
        }
    }

    private void permissionFailed(List<Permission> list) {
        if (mMPermissionListener != null) {
            mMPermissionListener.onFailed(getPermissionArray(list));
        }
    }
}
