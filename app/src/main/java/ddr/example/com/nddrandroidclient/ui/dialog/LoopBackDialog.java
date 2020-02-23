package ddr.example.com.nddrandroidclient.ui.dialog;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.StringRes;
import androidx.fragment.app.FragmentActivity;
import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.base.BaseDialog;
import ddr.example.com.nddrandroidclient.common.MyDialogFragment;
import ddr.example.com.nddrandroidclient.widget.view.ProgressView;

/**
 * time : 2020/02/20
 * desc : 回环检测的对话框
 */
public final class LoopBackDialog {
    public static final class Builder
            extends MyDialogFragment.Builder<Builder> implements View.OnClickListener{

        private final TextView mMessageView;
        private final TextView tv_button;
        private final ProgressView progressView;

        public Builder(FragmentActivity activity) {
            super(activity);
            setContentView(R.layout.dialog_loop_back);
            setAnimStyle(BaseDialog.AnimStyle.TOAST);
            setBackgroundDimEnabled(false);
            setCancelable(false);

            mMessageView = findViewById(R.id.tv_detection);
            tv_button=findViewById(R.id.tv_button);
            progressView=findViewById(R.id.pw_progress);
            tv_button.setOnClickListener(this);
        }

        public Builder setMessage(@StringRes int id) {
            return setMessage(getString(id));
        }
        public Builder setMessage(CharSequence text) {
            mMessageView.setText(text);
            mMessageView.setVisibility(text == null ? View.GONE : View.VISIBLE);
            return this;
        }
        public Builder setBottomMessage(String text){
            tv_button.setText(text);
            return this;
        }
        public Builder setProgressVisibility(int visibility){
            progressView.setVisibility(visibility);
            return this;
        }


        @Override
        public void onClick(View v) {
            dismiss();
        }
    }
}
