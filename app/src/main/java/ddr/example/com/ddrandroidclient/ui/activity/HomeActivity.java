package ddr.example.com.ddrandroidclient.ui.activity;


import android.view.KeyEvent;

import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import ddr.example.com.ddrandroidclient.R;
import ddr.example.com.ddrandroidclient.common.DDRActivity;
import ddr.example.com.ddrandroidclient.common.DDRLazyFragment;
import ddr.example.com.ddrandroidclient.helper.ActivityStackManager;
import ddr.example.com.ddrandroidclient.helper.DoubleClickHelper;
import ddr.example.com.ddrandroidclient.ui.adapter.BaseFragmentAdapter;
import ddr.example.com.ddrandroidclient.ui.fragment.MapFragment;
import ddr.example.com.ddrandroidclient.ui.fragment.SetUpFragment;
import ddr.example.com.ddrandroidclient.ui.fragment.StatusFragment;
import ddr.example.com.ddrandroidclient.ui.fragment.TaskFragment;
import ddr.example.com.ddrandroidclient.ui.fragment.VersionFragment;

/**
 * time:2019/10/26
 * desc: 主页界面
 */
public class HomeActivity extends DDRActivity implements ViewPager.OnPageChangeListener {
    @BindView(R.id.vp_home_pager)
    ViewPager vpHomePager;

    /**
     * ViewPage 适配器
     */
    private BaseFragmentAdapter<DDRLazyFragment> mPagerAdapter;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_home;
    }

    @Override
    protected void initView() {
        vpHomePager.addOnPageChangeListener(this);
    }

    @Override
    protected void initData() {
        mPagerAdapter=new BaseFragmentAdapter<DDRLazyFragment>(this);
        mPagerAdapter.addFragment(StatusFragment.newInstance());
        mPagerAdapter.addFragment(MapFragment.newInstance());
        mPagerAdapter.addFragment(TaskFragment.newInstance());
        mPagerAdapter.addFragment(SetUpFragment.newInstance());
        mPagerAdapter.addFragment(VersionFragment.newInstance());

        vpHomePager.setAdapter(mPagerAdapter);
        //限制页面的数量
        vpHomePager.setOffscreenPageLimit(mPagerAdapter.getCount());
    }



    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    @Override
    public void onBackPressed() {
        if (DoubleClickHelper.isOnDoubleClick()) {
            //移动到上一个任务栈，避免侧滑引起的不良反应
            moveTaskToBack(false);
            postDelayed(new Runnable() {

                @Override
                public void run() {
                    // 进行内存优化，销毁掉所有的界面
                    ActivityStackManager.getInstance().finishAllActivities();
                    // 销毁进程（请注意：调用此 API 可能导致当前 Activity onDestroy 方法无法正常回调）
                    // System.exit(0);
                }
            }, 300);
        } else {
            toast("再按一次退出");
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mPagerAdapter.getCurrentFragment().onKeyDown(keyCode,event)){
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        vpHomePager.removeOnPageChangeListener(this);
        vpHomePager.setAdapter(null);
        super.onDestroy();
    }
}
