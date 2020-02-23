package ddr.example.com.nddrandroidclient.ui.fragment;

import android.view.View;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import butterknife.BindView;
import butterknife.OnClick;
import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.base.BaseFragmentAdapter;
import ddr.example.com.nddrandroidclient.common.DDRLazyFragment;
import ddr.example.com.nddrandroidclient.other.KeyboardWatcher;
import ddr.example.com.nddrandroidclient.other.Logger;
import ddr.example.com.nddrandroidclient.ui.activity.HomeActivity;
import ddr.example.com.nddrandroidclient.ui.fragment.secondFragment.AutoChargingSet;
import ddr.example.com.nddrandroidclient.ui.fragment.secondFragment.EditManagerSet;
import ddr.example.com.nddrandroidclient.ui.fragment.secondFragment.HelpFeedbackSet;
import ddr.example.com.nddrandroidclient.ui.fragment.secondFragment.NaParameterSet;
import ddr.example.com.nddrandroidclient.ui.fragment.secondFragment.RobotTestSet;
import ddr.example.com.nddrandroidclient.ui.fragment.secondFragment.SensorSet;
import ddr.example.com.nddrandroidclient.widget.view.DDRViewPager;

/**
 * time: 2019/10/26
 * desc: 高级设置界面
 */
public class SetUpFragment extends DDRLazyFragment<HomeActivity> implements ViewPager.OnPageChangeListener{
    @BindView(R.id.vp_home_pager)
    DDRViewPager viewPager;
    @BindView(R.id.tv_naParam)
    TextView tv_naParam;
    @BindView(R.id.tv_autoCharging)
    TextView tv_autoCharging;

    private BaseFragmentAdapter<DDRLazyFragment> mPagerAdapter;

    public static SetUpFragment newInstance(){
        return new SetUpFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_setup;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        mPagerAdapter = new BaseFragmentAdapter<DDRLazyFragment>(this);
        mPagerAdapter.addFragment(NaParameterSet.newInstance());
        mPagerAdapter.addFragment(AutoChargingSet.newInstance());
        mPagerAdapter.addFragment(SensorSet.newInstance());
        mPagerAdapter.addFragment(RobotTestSet.newInstance());
        mPagerAdapter.addFragment(EditManagerSet.newInstance());
        mPagerAdapter.addFragment(HelpFeedbackSet.newInstance());
        viewPager.setAdapter(mPagerAdapter);
        viewPager.setOffscreenPageLimit(mPagerAdapter.getCount());
        viewPager.addOnPageChangeListener(this);
        viewPager.setCurrentItem(0);
    }

    @OnClick({R.id.tv_naParam,R.id.tv_autoCharging,R.id.tv_sensorSet,R.id.tv_robotTest,R.id.tv_editionManager,R.id.tv_helpFeedback})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.tv_naParam:
                viewPager.setCurrentItem(0);
                Logger.e("页数"+viewPager.getCurrentItem());
                break;
            case R.id.tv_autoCharging:
                viewPager.setCurrentItem(1);
                Logger.e("页数"+viewPager.getCurrentItem());
                break;
            case R.id.tv_sensorSet:
                viewPager.setCurrentItem(2); //传感器配置
                break;
            case R.id.tv_robotTest:
                viewPager.setCurrentItem(3);//机器检测
                break;
            case R.id.tv_editionManager:
                viewPager.setCurrentItem(4); //版本管理
                break;
            case R.id.tv_helpFeedback:
                viewPager.setCurrentItem(5); //帮助与反馈
                break;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Logger.e("------onRestart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Logger.e("-----onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Logger.e("------onPause");
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
}
