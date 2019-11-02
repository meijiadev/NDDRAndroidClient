package ddr.example.com.nddrandroidclient.ui.adapter;

import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.base.BaseAdapter;

/**
 * time : 2019/11/2
 * desc : 机器人ID列表适配器
 */
public class RobotIdAdapter extends BaseAdapter<String> {

    public RobotIdAdapter(int layoutResId) {
        super(layoutResId);
    }

    public RobotIdAdapter(int layoutResId, @Nullable List<String> data) {
        super(layoutResId, data);
    }

    @Override
    public void setNewData(@Nullable List<String> data) {
        super.setNewData(data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, String item) {
        super.convert(helper, item);
        helper.setText(R.id.tv_robot_id,item);
    }
}
