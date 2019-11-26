package win.permision;

import android.content.Context;
import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * author: win
 * created on: 2019-07-13
 */
public class MPermissionRequest {

    /**
     * Context：Activity
     * */
    private Context mContext;
    /**
     * 需要申请的权限
     * */
    private List<Permission> mPermissions;
    /**
     * 权限申请结果回调
     * */
    private MPermissionListener mMPermissionListener;

    private MPermissionRequest(Builder builder) {
        mContext = builder.mContext;
        mPermissions = builder.mPermissionList;
        mMPermissionListener = builder.mMPermissionListener;
    }

    public Context getContext() {
        return mContext;
    }

    public List<Permission> getPermissions() {
        return mPermissions;
    }

    public MPermissionListener getCallback() {
        return mMPermissionListener;
    }

    public String[] permissionArray() {
        if (mPermissions != null) {
            String[] permissions = new String[mPermissions.size()];
            for (int i=0; i<mPermissions.size(); i++) {
                permissions[i] = mPermissions.get(i).getPermission();
            }
            return permissions;
        }
        return null;
    }

    public static final class Builder {
        private Context mContext;
        private List<Permission> mPermissionList;
        private MPermissionListener mMPermissionListener;

        public Builder() {
            mPermissionList = new ArrayList<>(2);
        }

        public Builder with(@NonNull Context context) {
            this.mContext = context;
            return this;
        }

        public Builder addPermission(@NonNull String permission) {
            this.mPermissionList.add(new Permission(permission));
            return this;
        }

        public Builder addPermission(@NonNull String permission, String rationale, boolean mustRequest) {
            this.mPermissionList.add(new Permission(permission, rationale, mustRequest));
            return this;
        }

        public Builder callback(@NonNull MPermissionListener permissionListener) {
            this.mMPermissionListener = permissionListener;
            return this;
        }

        public MPermissionRequest build() {
            return new MPermissionRequest(this);
        }
    }
}
