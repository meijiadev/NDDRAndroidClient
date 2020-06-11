package ddr.example.com.nddrandroidclient.ui.adapter;

import android.content.Context;
import android.util.AttributeSet;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ddr.example.com.nddrandroidclient.other.Logger;

public class NGridLayoutManager extends GridLayoutManager {
    public NGridLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public NGridLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    public NGridLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
    }

    @Override
    public boolean supportsPredictiveItemAnimations() {
        return false;
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        Logger.v("---------NGridLayoutManager");
        try {
            //把异常抛出来，不至于程序崩溃
            super.onLayoutChildren(recycler, state);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
