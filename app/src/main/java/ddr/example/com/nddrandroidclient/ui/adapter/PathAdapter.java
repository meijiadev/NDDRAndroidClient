package ddr.example.com.nddrandroidclient.ui.adapter;

import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.base.BaseAdapter;
import ddr.example.com.nddrandroidclient.entity.point.PathLine;

/**
 *  time : 2019/11/12
 *  desc : 路径列表适配器
 */
public class PathAdapter extends BaseAdapter<PathLine> {
    public PathAdapter(int layoutResId) {
        super(layoutResId);
    }

    public PathAdapter(int layoutResId, @Nullable List<PathLine> data) {
        super(layoutResId, data);
    }


    @Override
    public void setNewData(@Nullable List<PathLine> data) {
        super.setNewData(data);
    }


    @Override
    protected void convert(@NonNull BaseViewHolder helper, PathLine item) {
        super.convert(helper, item);
        helper.setText(R.id.tv_target_name,item.getName());
    }
}
