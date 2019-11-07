package ddr.example.com.nddrandroidclient.ui.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.base.BaseAdapter;
import ddr.example.com.nddrandroidclient.entity.MapInfo;

/**
 * time : 2019/11/2
 * desc : 地图列表的适配器
 */
public class MapAdapter extends BaseAdapter<MapInfo>{
    private Context mContext;
    private boolean allSelected;

    public MapAdapter(int layoutResId, Context context) {
        super(layoutResId);
        this.mContext=context;
    }

    public MapAdapter(int layoutResId, @Nullable List<MapInfo> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, MapInfo item) {
        super.convert(helper, item);
        Glide.with(mContext).load(item.getBitmap()).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into((ImageView) helper.getView(R.id.iv_map));
        helper.setText(R.id.tv_map_name,item.getMapName())
                .setText(R.id.tv_size,String.valueOf(item.getWidth())+"x"+String.valueOf(item.getHeight())+"m²")
                .setText(R.id.tv_selected_centre,"使用中")
                .setGone(R.id.tv_selected_centre,item.isUsing())
                .setBackgroundRes(R.id.item_head_layout,R.drawable.item_map_head_dark);
        if (item.isUsing()){
            helper.getView(R.id.item_head_layout).setBackgroundResource(R.drawable.item_map_head_blue);
        }
        if (allSelected){
            helper.getView(R.id.iv_select).setVisibility(View.VISIBLE);
        }else {
            helper.getView(R.id.iv_select).setVisibility(View.GONE);
        }

    }

    @Override
    public void setNewData(@Nullable List<MapInfo> data) {
        super.setNewData(data);
    }

    @Override
    public void addData(int position, @NonNull MapInfo data) {
        super.addData(position, data);
    }

    /**
     * 改变某条数据
     * @param index
     * @param data
     */
    @Override
    public void setData(int index, @NonNull MapInfo data) {
        super.setData(index, data);
    }


    @Nullable
    @Override
    public MapInfo getItem(int position) {
        return super.getItem(position);
    }

    /**
     * 是否显示批量选择的按钮
     * @param isSelected true 为显示 false 为隐藏
     */
    public void showSelected(boolean isSelected){
        allSelected=isSelected;
    }
}
