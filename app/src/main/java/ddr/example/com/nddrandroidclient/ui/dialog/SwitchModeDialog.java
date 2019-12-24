package ddr.example.com.nddrandroidclient.ui.dialog;

import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.base.BaseDialog;
import ddr.example.com.nddrandroidclient.common.MyDialogFragment;

/**
 * time :2019/12/18
 * desc :模式切换的弹窗
 */
public final class SwitchModeDialog {
    public static final class Builder extends MyDialogFragment.Builder<Builder> implements View.OnClickListener{
        private static OnListener mListener;
        private static int modeType=2;        //选择的模式 默认自主导航模式
        public static final int eABNaviTypeFollowLine=1;
        public static final int eABNaviTypeAutoPlanning=2;
        private static int touchType;      //选择的模式
        private TextView tv_navigation,tv_line_patrol,tv_cancel,tv_confirm;
        public Builder(FragmentActivity activity) {
            super(activity);
            setContentView(R.layout.dialog_switch_mode);
            setAnimStyle(BaseDialog.AnimStyle.BOTTOM);

            tv_navigation=findViewById(R.id.tv_navigation);
            tv_line_patrol=findViewById(R.id.tv_line_patrol);
            tv_cancel=findViewById(R.id.tv_cancel);
            tv_confirm=findViewById(R.id.tv_confirm);

            tv_confirm.setOnClickListener(this);
            tv_cancel.setOnClickListener(this);
            tv_line_patrol.setOnClickListener(this);
            tv_navigation.setOnClickListener(this);
        }

        public Builder setListener(OnListener listener) {
            mListener = listener;
            return this;
        }

        public Builder setModeType(int type){
            modeType=type;
            if (modeType==eABNaviTypeAutoPlanning){
                tv_navigation.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.iv_selected_blue),null,null,null);
                tv_line_patrol.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.iv_selected_gray),null,null,null);
            }else if (modeType==eABNaviTypeFollowLine){
                tv_navigation.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.iv_selected_gray),null,null,null);
                tv_line_patrol.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.iv_selected_blue),null,null,null);
            }
            return this;
        }

        @Override
        public Builder setGravity(int gravity) {
            switch (gravity) {
                // 如果这个是在中间显示的
                case Gravity.CENTER:
                case Gravity.CENTER_VERTICAL:
                    // 重新设置动画
                    setAnimStyle(BaseDialog.AnimStyle.SCALE);
                    break;
                default:
                    break;
            }
            return super.setGravity(gravity);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.tv_navigation:
                    tv_navigation.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.iv_selected_blue),null,null,null);
                    tv_line_patrol.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.iv_selected_gray),null,null,null);
                    touchType=eABNaviTypeAutoPlanning;
                    break;
                case R.id.tv_line_patrol:
                    tv_line_patrol.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.iv_selected_blue),null,null,null);
                    tv_navigation.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.iv_selected_gray),null,null,null);
                    touchType=eABNaviTypeFollowLine;
                    break;
                case R.id.tv_cancel:
                    dismiss();
                    break;
                case R.id.tv_confirm:
                    modeType=touchType;
                    if (mListener!=null)
                    mListener.onConfirm(modeType);
                    dismiss();
                    break;

            }
        }
    }


    public interface OnListener<T> {

        /**
         * 点击确认
         * @param type 选择的模式
         */
        void onConfirm(int type);
    }
}
