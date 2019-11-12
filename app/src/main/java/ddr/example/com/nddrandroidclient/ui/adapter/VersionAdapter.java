package ddr.example.com.nddrandroidclient.ui.adapter;



import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.base.BaseAdapter;
import ddr.example.com.nddrandroidclient.entity.other.ComputerEdition;
import ddr.example.com.nddrandroidclient.other.Logger;

public class VersionAdapter extends BaseAdapter<ComputerEdition> {
    public VersionAdapter(int layoutResId) {
        super(layoutResId);
    }

    public VersionAdapter(int layoutResId, @Nullable List<ComputerEdition> data) {
        super(layoutResId, data);
    }

    @Override
    public void setNewData(@Nullable List<ComputerEdition> data) {
        super.setNewData(data);
        for (int i=0;i<data.size();i++){
            Logger.e("------"+data.get(i).getType());
        }
    }

    @Override
    public void addData(int position, @NonNull ComputerEdition data) {
        super.addData(position, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, ComputerEdition item) {
        super.convert(helper, item);
        helper.setText(R.id.tv_type,String.valueOf(item.getType()))
                .setText(R.id.tv_version,String.valueOf(item.getVersion()))
                .setText(R.id.tv_data,String.valueOf(item.getData()));
    }



    @Nullable
    @Override
    public ComputerEdition getItem(int position) {
        Logger.e("------:");
        return super.getItem(position);
    }


}
