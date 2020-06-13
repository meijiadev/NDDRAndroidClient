package ddr.example.com.nddrandroidclient.ui.fragment;

import android.view.View;

import androidx.viewpager.widget.ViewPager;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import DDRCommProto.BaseCmd;
import butterknife.BindView;
import butterknife.OnClick;
import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.base.BaseFragmentAdapter;
import ddr.example.com.nddrandroidclient.common.DDRLazyFragment;
import ddr.example.com.nddrandroidclient.entity.MessageEvent;
import ddr.example.com.nddrandroidclient.entity.other.ComputerEditions;
import ddr.example.com.nddrandroidclient.other.Logger;
import ddr.example.com.nddrandroidclient.protocobuf.CmdSchedule;
import ddr.example.com.nddrandroidclient.protocobuf.dispatcher.ClientMessageDispatcher;
import ddr.example.com.nddrandroidclient.socket.TcpClient;
import ddr.example.com.nddrandroidclient.ui.activity.HomeActivity;
import ddr.example.com.nddrandroidclient.ui.fragment.secondFragment.AutoChargingSetFragment;
import ddr.example.com.nddrandroidclient.ui.fragment.secondFragment.EditManagerSetFragment;
import ddr.example.com.nddrandroidclient.ui.fragment.secondFragment.NaParameterSetFragment;
import ddr.example.com.nddrandroidclient.ui.fragment.secondFragment.RobotTestSetFragment;
import ddr.example.com.nddrandroidclient.ui.fragment.secondFragment.SensorSetFragment;
import ddr.example.com.nddrandroidclient.ui.fragment.secondFragment.VoiceSetFragment;
import ddr.example.com.nddrandroidclient.widget.textview.LineTextView;
import ddr.example.com.nddrandroidclient.widget.view.DDRViewPager;

/**
 * time: 2019/10/26
 * desc: 高级设置界面
 */
public class SetUpFragment extends DDRLazyFragment<HomeActivity> implements ViewPager.OnPageChangeListener{
    @BindView(R.id.vp_home_pager)
    DDRViewPager viewPager;
    @BindView(R.id.tv_naParam)
    LineTextView tv_naParam;
    @BindView(R.id.tv_autoCharging)
    LineTextView tv_autoCharging;
    @BindView(R.id.tv_sensorSet)
    LineTextView tv_sensorSet;
    @BindView(R.id.tv_robotTest)
    LineTextView tv_robotTest;
    @BindView(R.id.tv_editionManager)
    LineTextView tv_editionManager;
    @BindView(R.id.tv_helpFeedback)
    LineTextView tv_helpFeedback;
    @BindView(R.id.tv_voice)
    LineTextView tv_voice;


    private BaseFragmentAdapter<DDRLazyFragment> mPagerAdapter;
    private TcpClient tcpClient;
    private ComputerEditions computerEditions;

    public static SetUpFragment newInstance(){
        return new SetUpFragment();
    }

    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void update(MessageEvent messageEvent) {
        switch (messageEvent.getType()) {
            case updateVersion:
                computerEditions=ComputerEditions.getInstance();
                if (computerEditions.getRobotType()==3){
                    tv_voice.setVisibility(View.VISIBLE);
                }else {
                    tv_voice.setVisibility(View.GONE);
                }
                break;
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_setup;
    }

    @Override
    protected void initView() {
        tcpClient = TcpClient.getInstance(getContext(), ClientMessageDispatcher.getInstance());
        mPagerAdapter = new BaseFragmentAdapter<DDRLazyFragment>(this);
        mPagerAdapter.addFragment(NaParameterSetFragment.getInstance());
        mPagerAdapter.addFragment(AutoChargingSetFragment.newInstance());
        mPagerAdapter.addFragment(SensorSetFragment.newInstance());
        mPagerAdapter.addFragment(RobotTestSetFragment.newInstance());
        mPagerAdapter.addFragment(EditManagerSetFragment.newInstance());
        mPagerAdapter.addFragment(VoiceSetFragment.newInstance());
        viewPager.setAdapter(mPagerAdapter);
        viewPager.setOffscreenPageLimit(mPagerAdapter.getCount());
        viewPager.addOnPageChangeListener(this);
        viewPager.setCurrentItem(0);
        isChecked();
    }

    @Override
    protected void initData() {
        tcpClient = TcpClient.getInstance(getContext(), ClientMessageDispatcher.getInstance());
        getHostComputerEdition();
    }

    @OnClick({R.id.tv_naParam,R.id.tv_autoCharging,R.id.tv_sensorSet,R.id.tv_robotTest,R.id.tv_editionManager,R.id.tv_voice})
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
            case R.id.tv_voice:
                viewPager.setCurrentItem(5); //语料播报
                break;
        }
        isChecked();
    }

    /**
     * 判断哪个页面是否被选中
     */
    protected void isChecked() {
        tv_naParam.isStitle(true);
        tv_autoCharging.isStitle(true);
        tv_sensorSet.isStitle(true);
        tv_robotTest.isStitle(true);
        tv_editionManager.isStitle(true);
        tv_voice.isStitle(true);
        switch (viewPager.getCurrentItem()){
            case 0:
                tv_naParam.isChecked(true);
                tv_autoCharging.isChecked(false);
                tv_sensorSet.isChecked(false);
                tv_robotTest.isChecked(false);
                tv_editionManager.isChecked(false);
                tv_voice.isChecked(false);
                break;
            case 1:
                tv_naParam.isChecked(false);
                tv_autoCharging.isChecked(true);
                tv_sensorSet.isChecked(false);
                tv_robotTest.isChecked(false);
                tv_editionManager.isChecked(false);
                tv_voice.isChecked(false);
                break;
            case 2:
                tv_naParam.isChecked(false);
                tv_autoCharging.isChecked(false);
                tv_sensorSet.isChecked(true);
                tv_robotTest.isChecked(false);
                tv_editionManager.isChecked(false);
                tv_voice.isChecked(false);
                break;
            case 3:
                tv_naParam.isChecked(false);
                tv_autoCharging.isChecked(false);
                tv_sensorSet.isChecked(false);
                tv_robotTest.isChecked(true);
                tv_editionManager.isChecked(false);
                tv_voice.isChecked(false);
                break;
            case 4:
                tv_naParam.isChecked(false);
                tv_autoCharging.isChecked(false);
                tv_sensorSet.isChecked(false);
                tv_robotTest.isChecked(false);
                tv_editionManager.isChecked(true);
                tv_voice.isChecked(false);
                break;
            case 5:
                tv_naParam.isChecked(false);
                tv_autoCharging.isChecked(false);
                tv_sensorSet.isChecked(false);
                tv_robotTest.isChecked(false);
                tv_editionManager.isChecked(false);
                tv_voice.isChecked(true);
                break;

        }
    }

    /**
     * 获取上位机版本信息
     */
    private void getHostComputerEdition() {
        BaseCmd.reqGetSysVersion reqGetSysVersion = BaseCmd.reqGetSysVersion.newBuilder()
                .build();
        tcpClient.sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eModuleServer), reqGetSysVersion);
    }

    private NaParameterSetFragment naParameterSetFragment;
    @Override
    protected void onRestart() {
        super.onRestart();
        viewPager.setCurrentItem(0);
        naParameterSetFragment= NaParameterSetFragment.getInstance();
        naParameterSetFragment.getNaParmeter(1);
        naParameterSetFragment.setNaparmeter();
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
        isChecked();

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
