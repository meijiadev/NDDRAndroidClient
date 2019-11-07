package ddr.example.com.nddrandroidclient.ui.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.base.BaseAdapter;

public class TaskCheckAdapter extends BaseAdapter<String> {
    private Context mContext;

    public TaskCheckAdapter(int layoutResId) {
        super(layoutResId);
    }
    public TaskCheckAdapter(int layoutResId, @Nullable List<String> data) {
        super(layoutResId, data);
    }

    @Override
    public void setNewData(@Nullable List<String> data) {
        super.setNewData(data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, String item) {
        super.convert(helper, item);
        helper.setText(R.id.item_recycle_t_chenck,item);
    }

    @Override
    public void addData(int position, @NonNull String data) {
        super.addData(position, data);
    }
}
