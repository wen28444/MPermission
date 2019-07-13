package win.permision;

/**
 * author: win
 * created on: 2019-07-13
 */
class Permission {

    private String mPermission;
    private String mRationale;
    private boolean mMustRequest;

    public Permission(String permission) {
        mPermission = permission;
    }

    public Permission(String permission, String rationale, boolean mustRequest) {
        mPermission = permission;
        mRationale = rationale;
        mMustRequest = mustRequest;
    }

    public String getPermission() {
        return mPermission;
    }

    public String getRationale() {
        return mRationale;
    }

    public boolean isMustRequest() {
        return mMustRequest;
    }
}
