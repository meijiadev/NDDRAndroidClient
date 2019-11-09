package ddr.example.com.nddrandroidclient.ui.adapter;

import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.base.BaseAdapter;
import ddr.example.com.nddrandroidclient.entity.point.TargetPoint;

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

    @Override
    protected void convert(@NonNull BaseViewHolder helper,TargetPoint item) {
        super.convert(helper, item);
        // 状态页面的 前往目标点布局
        helper.setText(R.id.item_recycle_gopoint,item.getName());
        //地图管理页面的布局
        helper.setText(R.id.tv_target_name,item.getName());


    }

    @Override
    public void setNewData(@Nullable List<TargetPoint> data) {
        super.setNewData(data);
    }


}
