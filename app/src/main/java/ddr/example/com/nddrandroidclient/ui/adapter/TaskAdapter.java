package ddr.example.com.nddrandroidclient.ui.adapter;

import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.base.BaseAdapter;
import ddr.example.com.nddrandroidclient.entity.point.TaskMode;

/**
 * time : 2019/11/12
 * desc : 任务列表适配器
 */
public class TaskAdapter extends BaseAdapter<TaskMode> {
    public TaskAdapter(int layoutResId) {
        super(layoutResId);
    }

    public TaskAdapter(int layoutResId, @Nullable List<TaskMode> data) {
        super(layoutResId, data);
    }

    @Override
    public void setNewData(@Nullable List<TaskMode> data) {
        super.setNewData(data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, TaskMode item) {
        super.convert(helper, item);
        helper.setText(R.id.tv_target_name,item.getName());
    }

}
