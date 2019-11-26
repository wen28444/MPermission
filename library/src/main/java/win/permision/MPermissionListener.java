package win.permision;

import androidx.annotation.NonNull;

/**
 * author: win
 * created on: 2019-07-13
 */
public interface MPermissionListener {

    void onSucceed(@NonNull String[] permissions);

    void onFailed(@NonNull String[] permissions);

}