package ddr.example.com.nddrandroidclient.widget.view;

import android.content.Context;
import android.os.Looper;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import ddr.example.com.nddrandroidclient.other.Logger;

/**
 * time  : 2019/10/30
 * desc : 修正跨页面的动画效果
 */
public class DDRViewPager extends ViewPager {
    public DDRViewPager(@NonNull Context context) {
        super(context);
    }

    public DDRViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        super.setCurrentItem(item, smoothScroll);
    }

    @Override
    public void setCurrentItem(int item) {
        boolean smoothScroll;
        int currentItem = getCurrentItem();
        if (currentItem == 0) {
            // 如果当前是第一页，只有第二页才会有动画
            smoothScroll = item == currentItem + 1;
        } else if (currentItem == getCount() - 1) {
            // 如果当前是最后一页，只有最后第二页才会有动画
            smoothScroll = item == currentItem - 1;
        } else {
            // 如果当前是中间页，只有相邻页才会有动画
            smoothScroll = Math.abs(currentItem - item) == 1;
        }
        Logger.e("---------setCurrentItem");
        super.setCurrentItem(item, smoothScroll);
    }

    public int getCount() {
        PagerAdapter adapter = getAdapter();
        return adapter != null ? adapter.getCount() : 0;
    }

}
