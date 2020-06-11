package ddr.example.com.nddrandroidclient.ui.dialog;

import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.base.BaseAdapter;
import ddr.example.com.nddrandroidclient.base.BaseDialog;
import ddr.example.com.nddrandroidclient.common.MyDialogFragment;
import ddr.example.com.nddrandroidclient.ui.adapter.NLinearLayoutManager;

/**
 *    time   : 2019/11/20
 *    desc   : 菜单选择框
 */
public final class MenuDialog {

    public static final class Builder
            extends MyDialogFragment.Builder<Builder>
            implements View.OnClickListener,BaseAdapter.OnItemClickListener {

        private OnListener mListener;
        private boolean mAutoDismiss = true;

        private final RecyclerView mRecyclerView;
        private final MenuAdapter mAdapter;
        private final TextView mCancelView;

        public Builder(FragmentActivity activity) {
            super(activity);
            setContentView(R.layout.dialog_menu);
            setAnimStyle(BaseDialog.AnimStyle.BOTTOM);

            mRecyclerView = findViewById(R.id.rv_menu_list);
            mCancelView  = findViewById(R.id.tv_menu_cancel);

            mRecyclerView.setLayoutManager(new NLinearLayoutManager(getContext()));
            mAdapter = new MenuAdapter(R.layout.item_menu);
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.setOnItemClickListener(this);

            mCancelView.setOnClickListener(this);
        }

        @Override
        public Builder setGravity(int gravity) {
            switch (gravity) {
                // 如果这个是在中间显示的
                case Gravity.CENTER:
                case Gravity.CENTER_VERTICAL:
                    // 不显示取消按钮
                    setCancel(null);
                    // 重新设置动画
                    setAnimStyle(BaseDialog.AnimStyle.SCALE);
                    break;
                default:
                    break;
            }
            return super.setGravity(gravity);
        }

        public Builder setList(int... ids) {
            List<String> data = new ArrayList<>(ids.length);
            for (int id : ids) {
                data.add(getString(id));
            }
            return setList(data);
        }

        public Builder setList(String... data) {
            return setList(Arrays.asList(data));
        }

        @SuppressWarnings("all")
        public Builder setList(List data) {
            mAdapter.setNewData(data);
            return this;
        }

        public Builder setCancel(@StringRes int id) {
            return setCancel(getString(id));
        }

        public Builder setCancel(CharSequence text) {
            mCancelView.setText(text);
            return this;
        }

        public Builder setAutoDismiss(boolean dismiss) {
            mAutoDismiss = dismiss;
            return this;
        }

        public Builder setListener(OnListener listener) {
            mListener = listener;
            return this;
        }

        /**
         * {@link View.OnClickListener}
         */
        @Override
        public void onClick(View v) {
            if (mAutoDismiss) {
                dismiss();
            }

            if (v == mCancelView) {
                if (mListener != null) {
                    mListener.onCancel(getDialog());
                }
            }
        }


        @Override
        public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
            if (mAutoDismiss) {
                dismiss();
            }

            if (mListener != null) {
                mListener.onSelected(getDialog(), position, mAdapter.getItem(position));
            }
        }
    }

    /**
     * 弹窗列表适配器
     */
    private static final class MenuAdapter extends BaseAdapter<Object>{
        public View mView;
        public MenuAdapter(int layoutResId) {
            super(layoutResId);
        }

        public MenuAdapter(int layoutResId, @Nullable List<Object> data) {
            super(layoutResId, data);
        }

        @Override
        public void setNewData(@Nullable List<Object> data) {
            super.setNewData(data);
        }

        @Override
        protected void convert(@NonNull BaseViewHolder helper, Object item) {
            super.convert(helper, item);
            helper.setText(R.id.tv_menu_name,item.toString());
            mView=(View) helper.getView(R.id.v_menu_line);

        }

        @Override
        public int getItemCount() {

            return super.getItemCount();
        }

        @Nullable
        @Override
        public Object getItem(int position) {

            return super.getItem(position);
        }
    }


    public interface OnListener<T> {

        /**
         * 选择条目时回调
         */
        void onSelected(BaseDialog dialog, int position, T t);

        /**
         * 点击取消时回调
         */
        void onCancel(BaseDialog dialog);
    }
}