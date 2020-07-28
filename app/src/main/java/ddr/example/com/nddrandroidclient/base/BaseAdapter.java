package ddr.example.com.nddrandroidclient.base;

import android.view.View;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

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


    /**
     * 将某个item直接隐藏
     * @param isVisible
     * @param helper
     */
    public void setVisibility(boolean isVisible,BaseViewHolder helper){
        RecyclerView.LayoutParams param = (RecyclerView.LayoutParams)helper.itemView.getLayoutParams();
        if (isVisible){
            param.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            param.width = LinearLayout.LayoutParams.MATCH_PARENT;
            helper.itemView.setVisibility(View.VISIBLE);
        }else{
            helper.itemView.setVisibility(View.GONE);
            param.height = 0;
            param.width = 0;
        }
        helper.itemView.setLayoutParams(param);
    }





}
