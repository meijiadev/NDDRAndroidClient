package ddr.example.com.nddrandroidclient.ui.dialog;


import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.base.BaseDialog;
import ddr.example.com.nddrandroidclient.common.MyDialogFragment;

public final class NormalDialog  {
    public static final class Builder extends MyDialogFragment.Builder<Builder> implements View.OnClickListener {

        private TextView tv_cancle;
        private OnListener onListener;

        public Builder(FragmentActivity activity) {
            super(activity);
            setContentView(R.layout.dialog_normal);
            setAnimStyle(BaseDialog.AnimStyle.IOS);

            tv_cancle=findViewById(R.id.tv_cancel_normal);

            tv_cancle.setOnClickListener(this);
        }

        public Builder setOnListener(OnListener onListener) {
            this.onListener = onListener;
            return this;
        }

        @Override
        public void onClick(View v) {
            if (onListener!=null){
                if (v==tv_cancle){
                    onListener.onCancel(getDialog());
                }
            }

        }
    }
    public interface OnListener {

        /**
         * 点击取消时回调
         */
        void onCancel(BaseDialog dialog);
    }
}
