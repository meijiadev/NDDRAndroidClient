package ddr.example.com.nddrandroidclient.ui.adapter;

import android.content.Context;
import android.util.AttributeSet;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ddr.example.com.nddrandroidclient.other.Logger;

/**
 *desc： 避免RecyclerView 偶然出现下标越界错误
 *time： 2020/5/20
 */
public class NLinearLayoutManager extends LinearLayoutManager {
    public NLinearLayoutManager(Context context) {
        super(context);
    }

    public NLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public NLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean supportsPredictiveItemAnimations() {
        return false;
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        Logger.v("-----------NLinearLayoutManager");
        try {
            super.onLayoutChildren(recycler, state);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
