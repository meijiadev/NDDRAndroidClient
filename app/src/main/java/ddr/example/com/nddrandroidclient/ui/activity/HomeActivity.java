package ddr.example.com.nddrandroidclient.ui.activity;


import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.common.DDRActivity;
import ddr.example.com.nddrandroidclient.common.DDRLazyFragment;
import ddr.example.com.nddrandroidclient.helper.ActivityStackManager;
import ddr.example.com.nddrandroidclient.helper.DoubleClickHelper;
import ddr.example.com.nddrandroidclient.other.Logger;
import ddr.example.com.nddrandroidclient.ui.adapter.BaseFragmentAdapter;
import ddr.example.com.nddrandroidclient.ui.fragment.MapFragment;
import ddr.example.com.nddrandroidclient.ui.fragment.SetUpFragment;
import ddr.example.com.nddrandroidclient.ui.fragment.StatusFragment;
import ddr.example.com.nddrandroidclient.ui.fragment.TaskFragment;
import ddr.example.com.nddrandroidclient.ui.fragment.VersionFragment;
import ddr.example.com.nddrandroidclient.widget.DDRViewPager;

/**
 * time:2019/10/26
 * desc: 主页界面
 */
public class HomeActivity extends DDRActivity implements ViewPager.OnPageChangeListener {
    @BindView(R.id.vp_home_pager)
    DDRViewPager vpHomePager;
    @BindView(R.id.status)
    TextView tv_status;
    @BindView(R.id.mapmanager)
    TextView tv_mapmanager;
    @BindView(R.id.taskmanager)
    TextView tv_taskmanager;
    @BindView(R.id.highset)
    TextView tv_highset;
    @BindView(R.id.typeversion)
    TextView tv_typeversion;
    @BindView(R.id.xh1)
    TextView tv_xh1;
    @BindView(R.id.xh2)
    TextView tv_xh2;
    @BindView(R.id.xh3)
    TextView tv_xh3;
    @BindView(R.id.xh4)
    TextView tv_xh4;
    @BindView(R.id.xh5)
    TextView tv_xh5;



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
        mPagerAdapter = new BaseFragmentAdapter<DDRLazyFragment>(this);
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
        Logger.e("-----当前页面：" + position);
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
        if (mPagerAdapter.getCurrentFragment().onKeyDown(keyCode, event)) {
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

   

    @OnClick({R.id.status, R.id.mapmanager, R.id.taskmanager, R.id.highset, R.id.typeversion})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.status:
                Logger.e("---------setCurrentItem");
                vpHomePager.setCurrentItem(0);
                break;
            case R.id.mapmanager:
                vpHomePager.setCurrentItem(1);
                break;
            case R.id.taskmanager:
                vpHomePager.setCurrentItem(2);
                break;
            case R.id.highset:
                vpHomePager.setCurrentItem(3);
                break;
            case R.id.typeversion:
                vpHomePager.setCurrentItem(4);
                Logger.e("---------setCurrentItem");
                break;
        }
        isChecked();
    }

    protected void isChecked() {
        switch (vpHomePager.getCurrentItem()) {
            case 0:
                tv_xh1.setVisibility(View.VISIBLE);
                tv_xh2.setVisibility(View.GONE);
                tv_xh3.setVisibility(View.GONE);
                tv_xh4.setVisibility(View.GONE);
                tv_xh5.setVisibility(View.GONE);
                break;
            case 1:
                tv_xh1.setVisibility(View.GONE);
                tv_xh2.setVisibility(View.VISIBLE);
                tv_xh3.setVisibility(View.GONE);
                tv_xh4.setVisibility(View.GONE);
                tv_xh5.setVisibility(View.GONE);
                break;
            case 2:
                tv_xh1.setVisibility(View.GONE);
                tv_xh2.setVisibility(View.GONE);
                tv_xh3.setVisibility(View.VISIBLE);
                tv_xh4.setVisibility(View.GONE);
                tv_xh5.setVisibility(View.GONE);
                break;
            case 3:
                tv_xh1.setVisibility(View.GONE);
                tv_xh2.setVisibility(View.GONE);
                tv_xh3.setVisibility(View.GONE);
                tv_xh4.setVisibility(View.VISIBLE);
                tv_xh5.setVisibility(View.GONE);
                break;
            case 4:
                tv_xh1.setVisibility(View.GONE);
                tv_xh2.setVisibility(View.GONE);
                tv_xh3.setVisibility(View.GONE);
                tv_xh4.setVisibility(View.GONE);
                tv_xh5.setVisibility(View.VISIBLE);
                break;

        }
    }


}
