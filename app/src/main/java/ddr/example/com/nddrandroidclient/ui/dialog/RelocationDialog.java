package ddr.example.com.nddrandroidclient.ui.dialog;



import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.base.BaseDialog;
import ddr.example.com.nddrandroidclient.common.MyDialogFragment;

/**
 * desc: 重定位弹窗
 * time：2020/4/1
 */
public final class RelocationDialog {
    public static final class Builder extends MyDialogFragment.Builder<Builder>implements View.OnClickListener{
        public final TextView  tvCancelRelocation;
        public final TextView tvHandMovement;
        public OnListener mListener;
        public boolean mAutoDismiss;
        public Builder(FragmentActivity activity) {
            super(activity);
            setContentView(R.layout.dialog_relocation);
            setAnimStyle(BaseDialog.AnimStyle.TOAST);
            setCancelable(false);
            tvCancelRelocation=findViewById(R.id.tv_cancel_relocation);
            tvHandMovement=findViewById(R.id.tv_hand_movement);

            tvHandMovement.setOnClickListener(this);
            tvCancelRelocation.setOnClickListener(this);

        }

        public Builder setListener(OnListener listener) {
            mListener = listener;
            return this;
        }

        /**
         * 判断是否自动弹窗消失
         * @param dismiss
         * @return
         */
        public Builder setAutoDismiss(boolean dismiss) {
            mAutoDismiss = dismiss;
            return this;
        }

        @Override
        public void onClick(View v) {
            if (mAutoDismiss) {
                dismiss();
            }

            if (mListener != null) {
                if (v == tvHandMovement) {
                    // 判断输入是否为空
                    mListener.onHandMovement();
                } else if (v == tvCancelRelocation) {
                    mListener.onCancelRelocation();
                }
            }
        }


    }



    public interface OnListener {

        /**
         * 点击手动定位
         */
        void onHandMovement();

        /**
         * 点击取消定位
         */
        void onCancelRelocation();
    }
}
