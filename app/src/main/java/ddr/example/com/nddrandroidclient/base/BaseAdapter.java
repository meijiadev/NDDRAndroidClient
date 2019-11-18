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

    public  int viewType;


    public BaseAdapter(int layoutResId) {
        super(layoutResId);
        this.viewType=layoutResId;
    }



    public BaseAdapter(int layoutResId, @Nullable List<T> data) {
        super(layoutResId, data);
        this.viewType=layoutResId;
    }


    @Override
    public void setNewData(@Nullable List<T> data) {
        super.setNewData(data);
    }

    /**
     * 新增子项
     * @param position
     * @param data
     */
    @Override
    public void addData(int position, @NonNull T data) {
        super.addData(position, data);
    }

    /**
     * 改变某个子项
     * @param index
     * @param data
     */
    @Override
    public void setData(int index, @NonNull T data) {
        super.setData(index, data);
    }



    @Override
    protected void convert(@NonNull BaseViewHolder helper, T item) {

    }

    @Nullable
    @Override
    public T getItem(int position) {
        return super.getItem(position);
    }








}
