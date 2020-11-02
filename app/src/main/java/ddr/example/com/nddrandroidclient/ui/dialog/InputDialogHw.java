package ddr.example.com.nddrandroidclient.ui.dialog;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.base.BaseDialog;
import ddr.example.com.nddrandroidclient.common.MyDialogFragment;

public class InputDialogHw {
    public static final class Builder
            extends MyDialogFragment.Builder<InputDialogHw.Builder>
            implements View.OnClickListener {

        private InputDialogHw.OnListener mListener;
        private boolean mAutoDismiss = true;

        private final TextView mTitleView;
        private final TextView textBuild;
        private final TextView textFloor;

        private final TextView mCancelView;
        private final TextView mConfirmView;
        private final EditText mTextNum;

        public Builder(FragmentActivity activity) {
            super(activity);
            setContentView(R.layout.dialog_hw_input);
            setAnimStyle(BaseDialog.AnimStyle.IOS);

            mTitleView = findViewById(R.id.tv_input_title);
            textBuild = findViewById(R.id.tv_input_build);
            textFloor = findViewById(R.id.tv_input_flor);

            mCancelView = findViewById(R.id.tv_input_cancel);
            mConfirmView = findViewById(R.id.tv_input_confirm);
            mTextNum = findViewById(R.id.edit_num);

            mCancelView.setOnClickListener(this);
            mConfirmView.setOnClickListener(this);
            textBuild.setOnClickListener(this);
            textFloor.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            if (mAutoDismiss) {
                dismiss();
            }

            if (mListener != null) {
                if (v == mConfirmView) {
                    // 判断输入是否为空
                    mListener.onConfirm(getDialog());
                } else if (v == mCancelView) {
                    mListener.onCancel(getDialog());
                }else if (v==textBuild){
                    mListener.onBuild(getDialog());
                }else if (v==textFloor){
                    mListener.onFloor(getDialog());
                }
            }

        }

        /**
         * 判断是否自动弹窗消失
         * @param dismiss
         * @return
         */
        public InputDialogHw.Builder setAutoDismiss(boolean dismiss) {
            mAutoDismiss = dismiss;
            return this;
        }

        public InputDialogHw.Builder setListener(InputDialogHw.OnListener listener) {
            mListener = listener;
            return this;
        }
    }
    public interface OnListener {

        /**
         * 点击确定时回调
         */
        void onConfirm(BaseDialog dialog);

        /**
         * 点击取消时回调
         */
        void onCancel(BaseDialog dialog);

        /**
         * 点击建筑时回调
         */
        void onBuild(BaseDialog dialog);

        /**
         * 点击楼层时回调
         */
        void onFloor(BaseDialog dialog);
    }
}
