package ddr.example.com.nddrandroidclient.ui.dialog;

import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseViewHolder;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.base.BaseAdapter;
import ddr.example.com.nddrandroidclient.base.BaseDialog;
import ddr.example.com.nddrandroidclient.common.MyDialogFragment;
import ddr.example.com.nddrandroidclient.entity.point.PathLine;
import ddr.example.com.nddrandroidclient.other.DpOrPxUtils;
import ddr.example.com.nddrandroidclient.other.Logger;
import ddr.example.com.nddrandroidclient.ui.adapter.NLinearLayoutManager;
import ddr.example.com.nddrandroidclient.widget.edit.DDREditText;
import ddr.example.com.nddrandroidclient.widget.view.CustomPopuWindow;

public final class SelectDialog {
    public static final class Builder extends MyDialogFragment.Builder<Builder> implements View.OnClickListener {
        private static OnListener mListener;
        private static TextView tv_point_name;
        private static TextView tv_action_name;
        private static DDREditText et_toward;
        private static TextView tv_cancel;
        private static TextView tv_canfirm;
        private static FragmentActivity fragmentActivity;
        private static  SelectAdapter mAdapter;
        private static List actionData;
        private static List pathPoints;

        public Builder(FragmentActivity activity) {
            super(activity);
            fragmentActivity=activity;
            setContentView(R.layout.dialog_add_action);
            setAnimStyle(BaseDialog.AnimStyle.BOTTOM);

            tv_point_name=findViewById(R.id.tv_point_name);
            tv_action_name=findViewById(R.id.tv_action_name);
            et_toward=findViewById(R.id.et_toward);
            tv_cancel=findViewById(R.id.tv_cancel);
            tv_canfirm=findViewById(R.id.tv_confirm);

            tv_point_name.setOnClickListener(this);
            tv_action_name.setOnClickListener(this);
            tv_cancel.setOnClickListener(this);
            tv_canfirm.setOnClickListener(this);

        }

        public Builder setListener(OnListener listener) {
            mListener = listener;
            return this;
        }

        public Builder setActionList(List actionData){
            this.actionData=actionData;
            return this;
        }

        public Builder setPointList(List pointList){
            this.pathPoints=pointList;
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
        public Builder setThemeStyle(int id) {
            return super.setThemeStyle(id);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.tv_point_name:
                    showListPopupWindow(tv_point_name);
                    break;
                case R.id.tv_action_name:
                    showListPopupWindow(tv_action_name);
                    break;
                case R.id.tv_cancel:
                    Logger.e("点击取消");
                    dismiss();
                    break;
                case R.id.tv_confirm:
                    Logger.e("点击确定");
                    if (tv_point_name.getText().toString().equals("选择路径点")){
                        toast(R.string.please_select_rpoint);
                    }else if (tv_action_name.getText().toString().equals("选择动作类型")){
                        toast(R.string.please_select_atype);
                    }else {
                        PathLine.PathPoint pathPoint= (PathLine.PathPoint) pathPoints.get(mPosition);
                        pathPoint.setRotationAngle(et_toward.getFloatText());
                        mListener.onConfirm();
                        dismiss();
                    }
                    break;

            }
        }
        private static CustomPopuWindow customPopuWindow;
        private static RecyclerView showRecycler;
        private static int mPosition;
        /**
         *
         * @param view
         */
        private static void showListPopupWindow(View view){
           View contentView = fragmentActivity.getLayoutInflater().from(fragmentActivity).inflate(R.layout.recycle_task, null);
            customPopuWindow = new CustomPopuWindow.PopupWindowBuilder(fragmentActivity)
                    .setView(contentView)
                    .enableOutsideTouchableDissmiss(false)
                    .create()
                    .showAsDropDown(view, DpOrPxUtils.dip2px(fragmentActivity, 0), 5);
            showRecycler =contentView.findViewById(R.id.recycler_task_check);
            NLinearLayoutManager layoutManager=new NLinearLayoutManager(fragmentActivity);
            showRecycler.setLayoutManager(layoutManager);
            mAdapter=new SelectAdapter(R.layout.item_recycle_task_check);
            showRecycler.setAdapter(mAdapter);
            if (view==tv_point_name){
                mAdapter.setNewData(pathPoints);
            }else if (view==tv_action_name){
                mAdapter.setNewData(actionData);
            }
            mAdapter.setOnItemClickListener((adapter, view1, position) -> {
                if (view==tv_point_name){
                    mListener.onSelected(position,mAdapter.getItem(position));
                    PathLine.PathPoint pathPoint= (PathLine.PathPoint) pathPoints.get(position);
                    tv_point_name.setText(pathPoint.getName());
                    mPosition=position;
                    customPopuWindow.dissmiss();
                }else if (view==tv_action_name){
                    mListener.onSelectedAction(position,mAdapter.getItem(position));
                    tv_action_name.setText(mAdapter.getItem(position).toString());
                    PathLine.PathPoint pathPoint= (PathLine.PathPoint) pathPoints.get(mPosition);
                    pathPoint.setPointType(position+1);
                    pathPoint.setRotationAngle(et_toward.getFloatText());
                    customPopuWindow.dissmiss();
                }
            });
        }
    }



    /**
     * 弹窗列表适配器
     */
    private static final class SelectAdapter extends BaseAdapter<Object> {
        public SelectAdapter(int layoutResId) {
            super(layoutResId);
        }

        public SelectAdapter(int layoutResId, @Nullable List<Object> data) {
            super(layoutResId, data);
        }

        @Override
        public void setNewData(@Nullable List<Object> data) {
            super.setNewData(data);
        }

        @Override
        protected void convert(@NonNull BaseViewHolder helper, Object item) {
            super.convert(helper, item);
            if (item instanceof String){
                helper.setText(R.id.item_recycle_t_chenck,item.toString());
            }else if (item instanceof PathLine.PathPoint){
                PathLine.PathPoint pathPoint= (PathLine.PathPoint) item;
                helper.setText(R.id.item_recycle_t_chenck,pathPoint.getName());
            }
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
        void onSelected(int position, T t);

        /**
         * 选择动作点时的回调
         * @param position
         * @param t
         */
        void onSelectedAction(int position, T t);

         void onConfirm();
    }



}
