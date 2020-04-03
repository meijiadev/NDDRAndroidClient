package ddr.example.com.nddrandroidclient.ui.adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.base.BaseAdapter;
import ddr.example.com.nddrandroidclient.entity.point.TargetPoint;
import ddr.example.com.nddrandroidclient.other.Logger;


/**
 * time: 2019/11/7
 * desc: 标记点列表适配器
 */
public class TargetPointAdapter extends BaseAdapter<TargetPoint> {


    public TargetPointAdapter(int layoutResId) {
        super(layoutResId);
    }


    public TargetPointAdapter(int layoutResId, @Nullable List<TargetPoint> data) {
        super(layoutResId, data);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    protected void convert(@NonNull BaseViewHolder helper,TargetPoint item) {
        super.convert(helper, item);
        switch (viewType){
            case R.layout.item_recycle_gopoint:
                // 状态页面的 前往目标点布局
                Logger.e("-----------状态页面的 前往目标点布局");
                helper.setText(R.id.item_recycle_gopoint,item.getName());
                TextView tv_select=helper.getView(R.id.item_recycle_gopoint);
                //Logger.e("GOPOINT状态"+item.isSelected());
                if (item.isSelected()){
                   tv_select.setBackgroundResource(R.drawable.task_check_bg);
                }else {
                    tv_select.setBackgroundResource(R.drawable.bt_bg__map);
                }
                break;
            case R.layout.item_target_point:
                //地图管理页面的布局
                Logger.e("-----------地图管理页面的布局");
                if (item.isSelected()){
                    helper.setText(R.id.tv_target_name,item.getName()).setTextColor(R.id.tv_target_name,Color.parseColor("#0399ff"));
                }else {
                    helper.setText(R.id.tv_target_name,item.getName())
                            .setTextColor(R.id.tv_target_name,Color.parseColor("#ffffff"));
                }
                break;
            case R.layout.item_task_select:
                helper.setText(R.id.tv_name,item.getName());
                ImageView iv_select=helper.getView(R.id.iv_select);
                if (item.isInTask()){
                    iv_select.setImageResource(R.mipmap.checkedwg);
                }else {
                    iv_select.setImageResource(R.mipmap.nocheckedwg);
                }
                break;
            case R.layout.item_show_recycler:
                if (item.isMultiple()){
                    helper.setText(R.id.tv_show_name,item.getName()).setTextColor(R.id.tv_show_name,Color.parseColor("#FFFFFFFF"))
                    .setImageResource(R.id.iv_select,R.mipmap.item_show);
                }else {
                    helper.setText(R.id.tv_show_name,item.getName())
                            .setTextColor(R.id.tv_show_name,Color.parseColor("#66ffffff"))
                            .setImageResource(R.id.iv_select,R.mipmap.item_hide);
                }
                break;
            case R.layout.item_point_to_path:
                if (item.isMultiple()){
                    helper.setText(R.id.tv_name,item.getName())
                            .setImageResource(R.id.iv_select,R.mipmap.checkedwg);
                }else {
                    helper.setText(R.id.tv_name,item.getName())
                            .setImageResource(R.id.iv_select,R.mipmap.nocheckedwg);
                }
                break;
            case R.layout.item_recycle_task_check:
                helper.setText(R.id.item_recycle_t_chenck,item.getName());
                break;

        }


    }


    @Override
    public void setNewData(@Nullable List<TargetPoint> data) {
        super.setNewData(data);
        Logger.e("设置列表");

    }



    @Override
    public void setData(int index, @NonNull TargetPoint data) {
        super.setData(index, data);
    }
}
