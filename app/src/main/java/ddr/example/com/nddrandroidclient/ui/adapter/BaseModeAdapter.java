package ddr.example.com.nddrandroidclient.ui.adapter;

import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.base.BaseAdapter;
import ddr.example.com.nddrandroidclient.entity.point.BaseMode;
import ddr.example.com.nddrandroidclient.entity.point.PathLine;
import ddr.example.com.nddrandroidclient.entity.point.TargetPoint;
import ddr.example.com.nddrandroidclient.other.Logger;

/**
 * time ：2019/11/13
 * desc : Task的子项中排序的列表适配器
 */
public class BaseModeAdapter extends BaseAdapter<BaseMode> {

    public BaseModeAdapter(int layoutResId) {
        super(layoutResId);
    }

    public BaseModeAdapter(int layoutResId, @Nullable List<BaseMode> data) {
        super(layoutResId, data);
    }

    @Override
    public void setNewData(@Nullable List<BaseMode> data) {
        super.setNewData(data);
        Logger.e("--------列表数量："+data.size());
    }


    @Override
    protected void convert(@NonNull BaseViewHolder helper, BaseMode item) {
        super.convert(helper, item);
        Logger.e("------:"+item.getType());
        if (item.getType()==1){
            PathLine pathLine= (PathLine) item;
            helper.setText(R.id.tv_name,pathLine.getName());
        }else if (item.getType()==2){
            TargetPoint targetPoint= (TargetPoint) item;
            helper.setText(R.id.tv_name,targetPoint.getName());
        }

    }


}
