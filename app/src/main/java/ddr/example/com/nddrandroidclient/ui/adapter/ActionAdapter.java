package ddr.example.com.nddrandroidclient.ui.adapter;

import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.base.BaseAdapter;
import ddr.example.com.nddrandroidclient.entity.point.PathLine;
import ddr.example.com.nddrandroidclient.widget.textview.DDRTextView;

/**
 * time : 2019/11/12
 * desc : 动作适配器
 */
public class ActionAdapter extends BaseAdapter<PathLine.PathPoint> {

    public ActionAdapter(int layoutResId) {
        super(layoutResId);
    }

    public ActionAdapter(int layoutResId, @Nullable List<PathLine.PathPoint> data) {
        super(layoutResId, data);
    }

    @Override
    public void setNewData(@Nullable List<PathLine.PathPoint> data) {
        super.setNewData(data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, PathLine.PathPoint item) {
        super.convert(helper, item);
        helper.setText(R.id.tv_action_name,item.getName())
                .addOnClickListener(R.id.tv_action_type)
                .addOnClickListener(R.id.tv_delete);
        DDRTextView tv_actionType=helper.getView(R.id.tv_action_type);
        tv_actionType.setValueText(item.getPointType());
    }

    @Override
    public void addData(int position, @NonNull PathLine.PathPoint data) {
        super.addData(position, data);
    }

    @Override
    public void setData(int index, @NonNull PathLine.PathPoint data) {
        super.setData(index, data);
    }

}
