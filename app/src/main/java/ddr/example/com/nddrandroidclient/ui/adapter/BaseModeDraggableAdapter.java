package ddr.example.com.nddrandroidclient.ui.adapter;

import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.entity.point.BaseMode;
import ddr.example.com.nddrandroidclient.entity.point.PathLine;
import ddr.example.com.nddrandroidclient.entity.point.TargetPoint;

/**
 * time ：2020/04/14
 * desc : Task的子项列表适配器(可拖拽)
 */
public class BaseModeDraggableAdapter extends BaseItemDraggableAdapter<BaseMode,BaseViewHolder>{

    public BaseModeDraggableAdapter(List<BaseMode> data) {
        super(data);
    }

    public BaseModeDraggableAdapter(int layoutResId, List<BaseMode> data) {
        super(layoutResId, data);
    }


    @Override
    protected void convert(@NonNull BaseViewHolder helper, BaseMode item) {
        if (item.getType()==1){
            PathLine pathLine= (PathLine) item;
            helper.setText(R.id.item_name,pathLine.getName());
        }else if (item.getType()==2){
            TargetPoint targetPoint= (TargetPoint) item;
            helper.setText(R.id.item_name,targetPoint.getName());
        }
        helper.addOnClickListener(R.id.iv_delete);
    }

    @Override
    public void setNewData(@Nullable List<BaseMode> data) {
        super.setNewData(data);
    }
}
