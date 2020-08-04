package ddr.example.com.nddrandroidclient.ui.adapter;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.base.BaseAdapter;
import ddr.example.com.nddrandroidclient.entity.other.SensorSea;

public class SensorAdapter extends BaseAdapter<SensorSea> {

    private TextView tv_id;
    private ImageView tv_tra;
    private TextView tv_value;
    public SensorAdapter(int layoutResId) {
        super(layoutResId);
    }

    public SensorAdapter(int layoutResId, @Nullable List<SensorSea> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, SensorSea item) {
        super.convert(helper, item);
        tv_id=helper.getView(R.id.tv_c_id);
        tv_tra=helper.getView(R.id.iv_cs_status);
        tv_value=helper.getView(R.id.tv_cs_distance);
        helper.setText(R.id.tv_cs_distance,String.valueOf(item.getValue()))
                .setText(R.id.tv_c_id,String.valueOf(item.getID()));
        switch (item.getTriggerStat()){
            case 0:
                helper.setImageResource(R.id.iv_cs_status,R.mipmap.nocheckedwg);
                break;
            case 1:
                helper.setImageResource(R.id.iv_cs_status,R.mipmap.checkedwg);
                break;
        }
    }

    @Override
    public void setNewData(@Nullable List<SensorSea> data) {
        super.setNewData(data);
    }

    @Override
    public void addData(int position, @NonNull SensorSea data) {
        super.addData(position, data);
    }

    @Override
    public void setData(int index, @NonNull SensorSea data) {
        super.setData(index, data);
    }

    @Nullable
    @Override
    public SensorSea getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public void setVisibility(boolean isVisible, BaseViewHolder helper) {
        super.setVisibility(isVisible, helper);
    }
}
