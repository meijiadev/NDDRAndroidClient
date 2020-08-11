package ddr.example.com.nddrandroidclient.ui.dialog;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.StringRes;
import androidx.fragment.app.FragmentActivity;
import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.base.BaseDialog;
import ddr.example.com.nddrandroidclient.common.MyDialogFragment;
import ddr.example.com.nddrandroidclient.other.Logger;

/**
 *    time   : 2018/12/2
 *    desc   : 等待加载对话框
 */
public final class WaitDialog {
    public static String message;
    public static final class Builder
            extends MyDialogFragment.Builder<Builder> implements View.OnClickListener{

        public final TextView mMessageView;
        private final ImageView ivquit;
        private OnListener mListener;

        public Builder(FragmentActivity activity) {
            super(activity);
            setContentView(R.layout.dialog_wait);
            setAnimStyle(BaseDialog.AnimStyle.TOAST);
            setBackgroundDimEnabled(false);
            setCancelable(false);

            mMessageView = findViewById(R.id.tv_wait_message);
            ivquit = findViewById(R.id.iv_rel_quit);

            ivquit.setOnClickListener(this);
        }

        public Builder setMessage(@StringRes int id) {
            return setMessage(getString(id));
        }
        public Builder setMessage(CharSequence text) {
            mMessageView.setText(text);
            mMessageView.setVisibility(text == null ? View.GONE : View.VISIBLE);
            return this;
        }

        @Override
        public Builder setVisibility(int id, int visibility) {
            ivquit.setVisibility(View.VISIBLE);
            return super.setVisibility(id, visibility);
        }

        public Builder setVisibility() {
            ivquit.setVisibility(View.VISIBLE);
            return this;
        }

        public Builder setListener(OnListener listener) {
            mListener = listener;
            return this;
        }

        @Override
        public void onClick(View v) {
            if (mListener!=null){
                if (v==ivquit){
                    Logger.d("点击----前");
                    mListener.onCancel(getDialog());
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