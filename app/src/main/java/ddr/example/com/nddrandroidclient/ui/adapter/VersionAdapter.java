package ddr.example.com.nddrandroidclient.ui.adapter;



import android.content.Context;

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
    private Context context;
    public VersionAdapter(int layoutResId, @Nullable List<ComputerEdition> data) {
        super(layoutResId, data);
    }

    public VersionAdapter(int layoutResId, Context context) {
        super(layoutResId);
        this.context=context;
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
        String type = null;
        switch (item.getType()){
            case 0:
                type=context.getString(R.string.au_host_computer);
                break;
            case 1:
                type=context.getString(R.string.au_lidar_module);
                break;
            case 2:
                type=context.getString(R.string.au_vision_module);
                break;
            case 3:
                type=context.getString(R.string.au_route_plan);
                break;
            case 4:
                type=context.getString(R.string.au_equipment_management);
                break;
            case 5:
                type=context.getString(R.string.au_embedded);
                break;
        }
//        Logger.e("类型"+type);
        helper.setText(R.id.tv_type,type)
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
