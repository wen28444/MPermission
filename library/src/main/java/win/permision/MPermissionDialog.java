package win.permision;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

/**
 * 自定义权限说明对话框
 *
 * author: win
 * created on: 2019-07-13
 */
class MPermissionDialog extends Dialog {

    private String mTitle;
    private String mMessage;
    private String mPositiveText;
    private String mNegativeText;
    private PositiveClickListener mPositiveClickListener;
    private NegativeClickListener mNegativeClickListener;
    private OnClickListener mOnClickListener;
    private String mActionText;

    public interface PositiveClickListener {
        void onClick();
    }

    public interface NegativeClickListener {
        void onClick();
    }

    public interface OnClickListener {
        void onClick();
    }

    private MPermissionDialog(@NonNull Builder builder) {
        super(builder.mContext);
        mTitle = builder.mTitle;
        mMessage = builder.mContent;
        mPositiveText = builder.mPositiveText;
        mNegativeText = builder.mNegativeText;
        mPositiveClickListener = builder.mPositiveClickListener;
        mNegativeClickListener = builder.mNegativeClickListener;
        mOnClickListener = builder.mOnClickListener;
        mActionText = builder.mActionText;
    }

    private MPermissionDialog(@NonNull Builder builder, int style) {
        super(builder.mContext, style);
        mTitle = builder.mTitle;
        mMessage = builder.mContent;
        mPositiveText = builder.mPositiveText;
        mNegativeText = builder.mNegativeText;
        mPositiveClickListener = builder.mPositiveClickListener;
        mNegativeClickListener = builder.mNegativeClickListener;
        mOnClickListener = builder.mOnClickListener;
        mActionText = builder.mActionText;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sui_permission_dialog_layout);
        setCancelable(false);
        setCanceledOnTouchOutside(false);

        initView();
    }

    private void initView() {
        TextView alertTitle = findViewById(R.id.alertTitle);
        TextView message = findViewById(R.id.message);
        Button action = findViewById(R.id.action);
        Button negative = findViewById(R.id.negative);
        Button positive = findViewById(R.id.positive);

        if (TextUtils.isEmpty(mTitle)) {
            alertTitle.setVisibility(View.GONE);
        } else {
            alertTitle.setText(mTitle);
        }
        message.setText(mMessage);
        if (mOnClickListener != null) {
            action.setVisibility(View.VISIBLE);
            findViewById(R.id.button_container).setVisibility(View.GONE);
            action.setText(mActionText);
            action.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                    mOnClickListener.onClick();
                }
            });
        } else {
            action.setVisibility(View.GONE);
            findViewById(R.id.button_container).setVisibility(View.VISIBLE);
            if (TextUtils.isEmpty(mNegativeText)) {
                mNegativeText = "取消";
            }
            negative.setText(mNegativeText);
            negative.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                    if (mNegativeClickListener != null) {
                        mNegativeClickListener.onClick();
                    }
                }
            });
            if (TextUtils.isEmpty(mPositiveText)) {
                mPositiveText = "确定";
            }
            positive.setText(mPositiveText);
            positive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                    if (mPositiveClickListener != null) {
                        mPositiveClickListener.onClick();
                    }
                }
            });
        }
    }

    public static class Builder {
        private Context mContext;
        private int mStyle;
        private PositiveClickListener mPositiveClickListener;
        private NegativeClickListener mNegativeClickListener;
        private OnClickListener mOnClickListener;
        private String mActionText;
        private String mTitle;
        private String mContent;
        private String mNegativeText;
        private String mPositiveText;

        public Builder(Context context) {
            this.mContext = context;
        }

        public Builder setStyle(int style) {
            this.mStyle = style;
            return this;
        }

        public Builder setTitle(String title) {
            this.mTitle = title;
            return this;
        }

        public Builder setMessage(String message) {
            this.mContent = message;
            return this;
        }

        public Builder setPositiveListener(PositiveClickListener listener, String text) {
            this.mPositiveClickListener = listener;
            mPositiveText = text;
            return this;
        }

        public Builder setNegativeListener(NegativeClickListener listener, String text) {
            this.mNegativeClickListener = listener;
            mNegativeText = text;
            return this;
        }

        public Builder setOnClickListener(OnClickListener listener, String text) {
            this.mOnClickListener = listener;
            mActionText = text;
            return this;
        }

        public MPermissionDialog create() {
            if (this.mStyle != 0) {
                return new MPermissionDialog(this, this.mStyle);
            } else {
                return new MPermissionDialog(this);
            }
        }
    }
}