### Android权限机制

Android6.0以前，Android的权限机制比较简单，开发者在AndroidManifest文件中声明需要的权限，APP安装时，系统提示用户APP将获取的权限，需要用户同意授权才能继续安装，从此APP便永久的获得了授权（其实并不绝对，后面会谈到）。然而，同期的iOS对于权限的处理会更加灵活，权限的授予并不是在安装时，而是在APP运行时，用户可以根据自身的需要，决定是否授予APP某一权限，同时，用户也可以很方便回收授予的权限。显然，动态权限管理的机制，对于用户的隐私保护是更加适用的，因此，Android6.0也发布了动态权限的机制。

首先，Android6.0对权限进行了分组，一般有如下三类：

1、正常权限

对用户隐私没有较大影响或者不会打来安全问题。 安装后就赋予这些权限，不需要显示提醒用户，用户也不能取消这些权限。

2、特殊权限 

就是一些特别敏感的权限，在Android系统中，主要由两个：

- SYSTEM_ALERT_WINDOW，设置悬浮窗，进行一些黑科技
- WRITE_SETTINGS 修改系统设置 

关于上面两个特殊权限的授权，做法是使用startActivityForResult启动授权界面来完成。

请求SYSTEMALERTWINDOW：

```
private static final int REQUEST_CODE = 1;
private  void requestAlertWindowPermission() {
    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
    intent.setData(Uri.parse("package:" + getPackageName()));
    startActivityForResult(intent, REQUEST_CODE);
}
 
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == REQUEST_CODE) {
        if (Settings.canDrawOverlays(this)) {
          Log.i(LOGTAG, "onActivityResult granted");
        }
    }
}
```

请求WRITE_SETTINGS:

```
private static final int REQUEST_CODE_WRITE_SETTINGS = 2;
private void requestWriteSettings() {
    Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
    intent.setData(Uri.parse("package:" + getPackageName()));
    startActivityForResult(intent, REQUEST_CODE_WRITE_SETTINGS );
}
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == REQUEST_CODE_WRITE_SETTINGS) {
        if (Settings.System.canWrite(this)) {
            Log.i(LOGTAG, "onActivityResult write settings granted" );
        }
    }
}
```

3、危险权限 

危险权限就是需要运行时申请才能使用的，这是运行时权限主要处理的对象。危险权限有哪些，可参考：

[危险权限和权限组](https://developer.android.com/guide/topics/security/permissions.html?hl=zh-cn#normal-dangerous)


### 权限申请处理

由于不同的机型Android版本不一致，所以需要针对不同版本做兼容处理。

1、当 targetSdkVersion<23，终端设备是6.0以上系统时，因为没有适配权限的申请相关逻辑，仍然采用安装时授权的方案，在app 安装时会询问AndroidManifest.xml文件中的权限，但是用户可以在设置列表中关闭相关权限，这种情况可能会对app正常运行造成一定影响。

2、当 targetSdkVersion>=23，终端设备是6.0以上系统时就需要做动态权限处理了，这部分权限检查使用官方方案就可以了。

首先，在动态权限申请的流程中，开发者主要关注流程和API如下：

2.1、检查权限是否授予

```
ContextCompat.checkSelfPermission(@NonNull Context context, @NonNull String permission)

```

2.2、申请权限

```
//Activity中使用
ActivityCompat.requestPermissions( new String[permission1,permission2,...], requestCode)
 
//Fragment中直接使用Fragment的方法
requestPermissions(@NonNull String[] permissions, int requestCode)
```
这个时候，会弹出系统授权弹窗。

2.3、权限回调

```
//用户在系统弹窗里面选择后，结果会通过Activity或者Fragment的onRequestPermissionsResult方法回调APP。
public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    //继续执行逻辑或者提示权限获取失败
}
```
2.4、权限说明

用户如果选择了拒绝，下一次在需要声明该权限的时候，Google建议APP开发者给予用户更多的说明，因此提供了下面这个API。

```
public boolean shouldShowRequestPermissionRationale(permission){
    1、APP没有申请这个权限的话，返回false
    2、用户拒绝时，勾选了不再提示的话，返回false
    3、用户拒绝，但是没有勾选不再提示的话，返回true
     
    可以在用户拒绝并且勾选了不再提示的时候弹一个自定义对话框，告诉用户为什么需要该权限。
}

```

3、当终端设备系统小于6.0时，本来是不需要处理权限的问题，但是在实测中发现，目前有不少国产Rom手机在6.0之前就有关闭权限的开关，这种情况也是我们兼容的对象。

### MPermission

MPermission是一个动态权限处理库，实现的功能包括：

- 检查单个、多个权限
- 申请单个、多个权限
- 权限申请成功、失败回调
- 当用户拒绝后，可配置是否弹出权限提示框

具体使用查看 demo。