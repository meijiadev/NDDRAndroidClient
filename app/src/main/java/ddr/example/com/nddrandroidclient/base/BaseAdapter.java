package ddr.example.com.nddrandroidclient.base;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * time : 2019/11/2
 * desc : 万能适配器基类
 * @param <T>
 */
public abstract class BaseAdapter<T> extends BaseQuickAdapter< T,BaseViewHolder> {

    public BaseAdapter(int layoutResId) {
        super(layoutResId);
    }

    public BaseAdapter(int layoutResId, @Nullable List<T> data) {
        super(layoutResId, data);
    }


    @Override
    public void setNewData(@Nullable List<T> data) {
        super.setNewData(data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, T item) {

    }


}
