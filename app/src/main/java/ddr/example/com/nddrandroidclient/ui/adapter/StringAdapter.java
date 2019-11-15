package ddr.example.com.nddrandroidclient.ui.adapter;

import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.base.BaseAdapter;

/**
 * time : 2019/11/2
 * desc : String类型的列表适配器
 */
public class StringAdapter extends BaseAdapter<String> {

    public StringAdapter(int layoutResId) {
        super(layoutResId);
    }

    public StringAdapter(int layoutResId, @Nullable List<String> data) {
        super(layoutResId, data);
    }

    @Override
    public void setNewData(@Nullable List<String> data) {
        super.setNewData(data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, String item) {
        super.convert(helper, item);
        switch (viewType){
            case R.layout.item_recycle_task_check:
                helper.setText(R.id.item_recycle_t_chenck,item);
                break;
            case R.layout.item_recycle_robot_id:
                helper.setText(R.id.tv_robot_id,item);
                break;
            case R.layout.item_path_mode:
                helper.setText(R.id.mode_name,item);
                break;
        }

    }




}
