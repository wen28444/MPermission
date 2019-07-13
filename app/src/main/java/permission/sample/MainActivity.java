package permission.sample;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import win.permision.MPermission;
import win.permision.MPermissionListener;
import win.permision.MPermissionRequest;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.check_simple_permission).setOnClickListener(this);
        findViewById(R.id.check_multi_permission).setOnClickListener(this);

        findViewById(R.id.simple_permission).setOnClickListener(this);
        findViewById(R.id.multi_permission).setOnClickListener(this);

        findViewById(R.id.request_permission_show_dialog).setOnClickListener(this);
        findViewById(R.id.request_multi_permission_show_dialog).setOnClickListener(this);
    }

    private void checkSimplePermission() {
        if (MPermission.checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            Toast.makeText(this, "有定位权限", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "无定位权限", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkMultiPermission() {
        if (MPermission.checkPermission(this,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.READ_CALL_LOG)) {
            Toast.makeText(this, "有通讯录和通话记录权限", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "没有通讯录或通话记录权限", Toast.LENGTH_SHORT).show();
        }
    }

    private void requestSimplePermission() {
        MPermissionRequest request = new MPermissionRequest.Builder()
                .with(this)
                .addPermission(Manifest.permission.READ_CONTACTS)
                .callback(new MPermissionListener() {
                    @Override
                    public void onSucceed(@NonNull String[] permissions) {
                        Toast.makeText(MainActivity.this, "申请通讯录权限成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailed(@NonNull String[] permissions) {
                        Toast.makeText(MainActivity.this, "申请通讯录权限失败", Toast.LENGTH_SHORT).show();
                    }
                }).build();
        MPermission.requestPermission(request);
    }

    private void requestMultiPermission() {
        MPermissionRequest request = new MPermissionRequest.Builder()
                .with(this)
                .addPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .addPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .callback(new MPermissionListener() {
                    @Override
                    public void onSucceed(@NonNull String[] permissions) {
                        Toast.makeText(MainActivity.this, "申请读写存储权限成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailed(@NonNull String[] permissions) {
                        Toast.makeText(MainActivity.this, "申请读写存储权限失败", Toast.LENGTH_SHORT).show();
                    }
                }).build();
        MPermission.requestPermission(request);
    }

    private void requestSimplePermissionWithDialog() {
        MPermissionRequest request = new MPermissionRequest.Builder()
                .with(this)
                .addPermission(Manifest.permission.READ_SMS, "完成该操作需要短信权限", true)
                .callback(new MPermissionListener() {
                    @Override
                    public void onSucceed(@NonNull String[] permissions) {
                        Toast.makeText(MainActivity.this, "申请短信权限成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailed(@NonNull String[] permissions) {
                        Toast.makeText(MainActivity.this, "申请短信权限失败", Toast.LENGTH_SHORT).show();
                    }
                }).build();
        MPermission.requestPermission(request);
    }

    private void requestMultiPermissionWithDialog() {
        MPermissionRequest request = new MPermissionRequest.Builder()
                .with(this)
                .addPermission(Manifest.permission.READ_CALENDAR, "完成该操作需要日历权限", true)
                .addPermission(Manifest.permission.READ_PHONE_STATE, "完成该操作需要手机标识码权限", true)
                .callback(new MPermissionListener() {
                    @Override
                    public void onSucceed(@NonNull String[] permissions) {
                        Toast.makeText(MainActivity.this, "申请权限成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailed(@NonNull String[] permissions) {
                        Toast.makeText(MainActivity.this, "申请权限失败", Toast.LENGTH_SHORT).show();
                    }
                }).build();
        MPermission.requestPermission(request);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.check_simple_permission:
                checkSimplePermission();
                break;
            case R.id.check_multi_permission:
                checkMultiPermission();
                break;
            case R.id.simple_permission:
                requestSimplePermission();
                break;
            case R.id.multi_permission:
                requestMultiPermission();
                break;
            case R.id.request_permission_show_dialog:
                requestSimplePermissionWithDialog();
                break;
            case R.id.request_multi_permission_show_dialog:
                requestMultiPermissionWithDialog();
                break;
            default:
                break;
        }
    }
}
