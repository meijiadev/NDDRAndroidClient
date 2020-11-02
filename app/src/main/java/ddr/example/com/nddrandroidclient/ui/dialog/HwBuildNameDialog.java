package ddr.example.com.nddrandroidclient.ui.dialog;

import android.view.View;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.base.BaseDialog;
import ddr.example.com.nddrandroidclient.common.MyDialogFragment;
import ddr.example.com.nddrandroidclient.ui.adapter.NLinearLayoutManager;
import ddr.example.com.nddrandroidclient.ui.adapter.StringAdapter;

public class HwBuildNameDialog {
    public static final class Builder extends MyDialogFragment.Builder<HwBuildNameDialog.Builder> implements View.OnClickListener{

        private RecyclerView recycler_build_check;
        private HwBuildNameDialog.OnListener mListener;
        private boolean mAutoDismiss = true;
        public Builder(FragmentActivity activity) {
            super(activity);
            setContentView(R.layout.recycle_hw_build);
            setAnimStyle(BaseDialog.AnimStyle.IOS);

            recycler_build_check=findViewById(R.id.recycler_task_check);
        }

        @Override
        public void onClick(View v) {
            if (mAutoDismiss) {
                dismiss();
            }
        }

        public Builder setAdapter(StringAdapter stringAdapter){
            NLinearLayoutManager layoutManager = new NLinearLayoutManager(getActivity());
            recycler_build_check.setLayoutManager(layoutManager);
            recycler_build_check.setAdapter(stringAdapter);
            return this;
        }

        /**
         * 判断是否自动弹窗消失
         * @param dismiss
         * @return
         */
        public HwBuildNameDialog.Builder setAutoDismiss(boolean dismiss) {
            mAutoDismiss = dismiss;
            return this;
        }

        public HwBuildNameDialog.Builder setListener(HwBuildNameDialog.OnListener listener) {
            mListener = listener;
            return this;
        }
    }

    public interface OnListener {

    }
}
