package ddr.example.com.nddrandroidclient.ui.activity;


import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.google.protobuf.ByteString;
import com.jaygoo.widget.OnRangeChangedListener;
import com.jaygoo.widget.RangeSeekBar;
import com.jaygoo.widget.VerticalRangeSeekBar;
import com.yhao.floatwindow.FloatWindow;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import DDRCommProto.BaseCmd;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;

import butterknife.OnClick;
import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.base.BaseApplication;
import ddr.example.com.nddrandroidclient.base.BaseDialog;
import ddr.example.com.nddrandroidclient.common.DDRActivity;
import ddr.example.com.nddrandroidclient.common.DDRLazyFragment;

import ddr.example.com.nddrandroidclient.common.GlobalParameter;
import ddr.example.com.nddrandroidclient.entity.MessageEvent;
import ddr.example.com.nddrandroidclient.entity.PointType;
import ddr.example.com.nddrandroidclient.entity.info.MapFileStatus;
import ddr.example.com.nddrandroidclient.entity.info.NotifyBaseStatusEx;
import ddr.example.com.nddrandroidclient.entity.info.NotifyEnvInfo;
import ddr.example.com.nddrandroidclient.entity.other.ComputerEditions;
import ddr.example.com.nddrandroidclient.entity.other.UdpIp;
import ddr.example.com.nddrandroidclient.entity.point.TargetPoint;
import ddr.example.com.nddrandroidclient.glide.ImageLoader;
import ddr.example.com.nddrandroidclient.helper.ActivityStackManager;
import ddr.example.com.nddrandroidclient.helper.DoubleClickHelper;
import ddr.example.com.nddrandroidclient.language.LanguageType;
import ddr.example.com.nddrandroidclient.language.LanguageUtil;
import ddr.example.com.nddrandroidclient.language.SpUtil;
import ddr.example.com.nddrandroidclient.other.DpOrPxUtils;
import ddr.example.com.nddrandroidclient.other.KeyboardWatcher;
import ddr.example.com.nddrandroidclient.other.Logger;
import ddr.example.com.nddrandroidclient.protocobuf.CmdSchedule;
import ddr.example.com.nddrandroidclient.protocobuf.dispatcher.ClientMessageDispatcher;
import ddr.example.com.nddrandroidclient.socket.TcpAiClient;
import ddr.example.com.nddrandroidclient.socket.TcpClient;
import ddr.example.com.nddrandroidclient.base.BaseFragmentAdapter;
import ddr.example.com.nddrandroidclient.socket.UdpClient;
import ddr.example.com.nddrandroidclient.ui.dialog.ControlPopupWindow;
import ddr.example.com.nddrandroidclient.ui.dialog.InputDialog;
import ddr.example.com.nddrandroidclient.ui.dialog.WaitDialog;
import ddr.example.com.nddrandroidclient.ui.fragment.MapFragment;
import ddr.example.com.nddrandroidclient.ui.fragment.SetUpFragment;
import ddr.example.com.nddrandroidclient.ui.fragment.StatusFragment;
import ddr.example.com.nddrandroidclient.ui.fragment.TaskFragment;
import ddr.example.com.nddrandroidclient.widget.view.CustomPopuWindow;
import ddr.example.com.nddrandroidclient.widget.view.DDRViewPager;
import ddr.example.com.nddrandroidclient.widget.textview.LineTextView;
import ddr.example.com.nddrandroidclient.widget.view.RockerView;

import static ddr.example.com.nddrandroidclient.widget.view.RockerView.DirectionMode.DIRECTION_2_HORIZONTAL;
import static ddr.example.com.nddrandroidclient.widget.view.RockerView.DirectionMode.DIRECTION_2_VERTICAL;

/**
 * time:2019/10/26
 * desc: 主页界面
 */
public class HomeActivity extends DDRActivity implements ViewPager.OnPageChangeListener,KeyboardWatcher.SoftKeyboardStateListener {
    @BindView(R.id.vp_home_pager)
    DDRViewPager vpHomePager;
    @BindView(R.id.status)
    LineTextView tv_status;
    @BindView(R.id.mapmanager)
    LineTextView tv_mapManager;
    @BindView(R.id.taskmanager)
    LineTextView tv_taskManager;
    @BindView(R.id.highset)
    LineTextView tv_highSet;
    @BindView(R.id.tv_quit)
    TextView tv_quit;
    @BindView(R.id.iv_jt_def)
    TextView iv_jt_def;
    @BindView(R.id.iv_yk_def)
    TextView iv_yk_def;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.drawer_layout_left)
    LinearLayout drawerLayoutLeft;
    @BindView(R.id.iv_menu)
    ImageView ivMenu;
    @BindView(R.id.tv_switch_language)
    TextView tv_switch_language;



    private TcpClient tcpClient;
    private NotifyBaseStatusEx notifyBaseStatusEx;
    private String currentMap;     //当前运行的地图名
    private String currentTask;   //当前运行的任务
    private CustomPopuWindow customPopuWindow;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private float lineSpeed, palstance;  //线速度 ，角速度
    private double maxSpeed = 0.4;       //设置的最大速度
    private boolean isforward, isGoRight; //左右摇杆当前的方向
    private VerticalRangeSeekBar seekBar;
    private CheckBox fixedSpeed;
    private  RockerView myRocker;
    private RockerView myRockerZy;
    private TextView tvSpeed;
    private ImageView iv_quit_yk;
    private TextView tv_xsu;//线速度
    private TextView tv_jsu;//角速度
    private String xsu;
    private String jsu;
    private boolean ishaveChecked = false;
    private String LAN_IP_AI="192.168.0.95";
    private ComputerEditions computerEditions;
    private String language;
    private MapFileStatus mapFileStatus;

    /**
     * ViewPage 适配器
     */
    private BaseFragmentAdapter<DDRLazyFragment> mPagerAdapter;
    private BaseDialog waitDialog;


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void update(MessageEvent messageEvent) {
        switch (messageEvent.getType()) {
            case updateBaseStatus:
                initStatusBar();
                break;
            case updateMapList:
                Logger.e("-------------updateMapList");
                getMapInfo();
                break;
            case switchTaskSuccess:
                Logger.e("--------更新地图");
                getMapInfo();
                Logger.e("---------" + currentMap);
                break;
            case switchMapSucceed:
                postDelayed(()->{
                    getMapInfo();
                },800);
                break;
            case touchFloatWindow:
                String className=ActivityStackManager.getInstance().getTopActivity().getClass().toString();
                Logger.e("--------当前栈顶的活动:"+className+";"+HomeActivity.class.toString());
                if (className.equals(HomeActivity.class.toString()))
                new ControlPopupWindow(this).showControlPopupWindow(findViewById(R.id.taskmanager));
                break;
            case notifyTCPDisconnected:
                Logger.e("----------断开连接页面：HomeActivity");
                netWorkStatusDialog();
                break;
            case updateRelocationStatus:
                int relocationStatus= (int) messageEvent.getData();
                if (relocationStatus==1&&vpHomePager!=null){
                    vpHomePager.setCurrentItem(0);
                }
                break;
            case updateAiPort:
                udpIp1= (UdpIp) messageEvent.getData();
//                Logger.e(globalParameter.isLan()+"是否局域网");
                Logger.e("AIip"+udpIp1.getIp()+"端口"+udpIp1.getPort());
                    tcpAiClient.createConnect(LAN_IP_AI,udpIp1.getPort());
                break;
            case tcpAiConnected:
                Logger.e("TcpAI服务开始连接");
                tcpAiClient.sendData(null, CmdSchedule.localLogin("admin_android","admin_android",4));
                break;
            case LoginAiSuccess:
                toast(R.string.ai_connected);
                UdpClient.getInstance(context,ClientMessageDispatcher.getInstance()).close();
                break;
            case updateVersion:
                computerEditions= ComputerEditions.getInstance();
                Logger.e("机器类型"+computerEditions.getRobotType());
                if (computerEditions.getRobotType()==3){

                }else {
                    UdpClient.getInstance(context,ClientMessageDispatcher.getInstance()).close();
                  Logger.e("非消杀无需连接Ai");
                }
                break;
        }

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_home;
    }

    @Override
    protected void initView() {
        KeyboardWatcher.with(this).setListener(this);
        mPagerAdapter = new BaseFragmentAdapter<DDRLazyFragment>(this);
        mPagerAdapter.addFragment(StatusFragment.newInstance());
        mPagerAdapter.addFragment(MapFragment.newInstance());
        mPagerAdapter.addFragment(TaskFragment.newInstance());
        mPagerAdapter.addFragment(SetUpFragment.newInstance());
        vpHomePager.setAdapter(mPagerAdapter);
        //限制页面的数量
        vpHomePager.setOffscreenPageLimit(mPagerAdapter.getCount());
        vpHomePager.addOnPageChangeListener(this);
        isChecked();
        language = SpUtil.getInstance(this).getString(SpUtil.LANGUAGE);
        Logger.e("当前的语言："+language);
        if (language.equals(LanguageType.CHINESE.getLanguage())){
            tv_switch_language.setText(R.string.switch_to_en);
            language=LanguageType.ENGLISH.getLanguage();
        }else if (language.equals(LanguageType.ENGLISH.getLanguage())){
            language=LanguageType.CHINESE.getLanguage();
            tv_switch_language.setText(R.string.switch_to_cn);
        }else {
            language=LanguageType.ENGLISH.getLanguage();
            tv_switch_language.setText(R.string.switch_to_en);
        }
    }


    @Override
    protected void initData() {
        tcpClient = TcpClient.getInstance(context, ClientMessageDispatcher.getInstance());
        mapFileStatus=MapFileStatus.getInstance();
        getHostComputerEdition();
        receiveAiBroadcast();
        tcpAiClient=TcpAiClient.getInstance(context,ClientMessageDispatcher.getInstance());
        notifyBaseStatusEx = NotifyBaseStatusEx.getInstance();
        ImageLoader.clear(this); //清除图片缓存
        tcpClient.requestFile();     //请求所有地图
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();

    }




    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        //Logger.e("-----当前页面：" + position);
        isChecked();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    @OnClick({R.id.iv_menu,R.id.status, R.id.mapmanager, R.id.taskmanager, R.id.highset,R.id.tv_quit,R.id.tv_shutdown,R.id.tv_switch_language})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_menu:
                if (drawerLayout.isDrawerOpen(drawerLayoutLeft)){
                    drawerLayout.closeDrawer(drawerLayoutLeft);
                }else {
                    drawerLayout.openDrawer(drawerLayoutLeft);
                }
                break;
            case R.id.status:
                //Logger.e("---------setCurrentItem");
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
            case R.id.tv_quit:
                new InputDialog.Builder(getActivity())
                        .setTitle(R.string.is_back)
                        .setEditVisibility(View.GONE)
                        .setListener(new InputDialog.OnListener() {
                            @Override
                            public void onConfirm(BaseDialog dialog, String content) {
                                onBack();
                            }
                            @Override
                            public void onCancel(BaseDialog dialog) {
                            }
                        }).show();

                break;
            case R.id.tv_shutdown:
                new InputDialog.Builder(this).setEditVisibility(View.GONE).setConfirm("关机").setCancel("重启").setListener(new InputDialog.OnListener() {
                    @Override
                    public void onConfirm(BaseDialog dialog, String content) {
                        tcpClient.reqCmdIpcMethod(BaseCmd.eCmdIPCMode.eShutDown);
                        toast(R.string.robot_is_shutdown);
                    }
                    @Override
                    public void onCancel(BaseDialog dialog) {
                        tcpClient.reqCmdIpcMethod(BaseCmd.eCmdIPCMode.eReStart);
                        toast(R.string.robot_is_restart);
                    }
                }).show();
                break;
            case R.id.tv_switch_language:
                changeLanguage(language);
                break;
            case R.id.tv_charging:
                if (!GlobalParameter.isIsAutoCharge()){
                    toast(R.string.auto_charge_notify_1);
                }else if (!haveChargePoint()){
                    toast(R.string.auto_charge_notify_2);
                }else {
                    tcpClient.goToCharge();
                }
                break;
        }
        isChecked();
    }

    /**
     * 如果是7.0以下，我们需要调用changeAppLanguage方法，
     * 如果是7.0及以上系统，直接把我们想要切换的语言类型保存在SharedPreferences中即可
     * 然后重新启动MainActivity
     * @param language
     */
    private void changeLanguage(String language) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            LanguageUtil.changeAppLanguage(BaseApplication.getContext(), language);
        }
        SpUtil.getInstance(this).putString(SpUtil.LANGUAGE, language);
        Intent intent = new Intent(this, SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
    /**
     * 是否存在充电点
     * @return
     */
    private boolean haveChargePoint(){
        for (TargetPoint targetPoint:mapFileStatus.getcTargetPoints()){
            if (targetPoint.getPointType().equals(PointType.eMarkingTypeCharging)){
                return true;
            }
        }
        return false;
    }
    /**
     * 判断哪个页面是否被选中
     */
    protected void isChecked() {
        switch (vpHomePager.getCurrentItem()) {
            case 0:
                tv_status.isChecked(true);
                tv_mapManager.isChecked(false);
                tv_highSet.isChecked(false);
                tv_taskManager.isChecked(false);
                tv_status.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.status_check), null, null, null);
                tv_mapManager.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.map_def), null, null, null);
                tv_highSet.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.hightset_def), null, null, null);
                tv_taskManager.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.version_def), null, null, null);
                break;
            case 1:
                tv_status.isChecked(false);
                tv_mapManager.isChecked(true);
                tv_highSet.isChecked(false);
                tv_taskManager.isChecked(false);
                tv_status.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.status_def), null, null, null);
                tv_mapManager.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.map_check), null, null, null);
                tv_highSet.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.hightset_def), null, null, null);
                tv_taskManager.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.version_def), null, null, null);
                break;
            case 2:
                tv_status.isChecked(false);
                tv_mapManager.isChecked(false);
                tv_highSet.isChecked(false);
                tv_taskManager.isChecked(true);
                tv_status.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.status_def), null, null, null);
                tv_mapManager.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.map_def), null, null, null);
                tv_highSet.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.hightset_def), null, null, null);
                tv_taskManager.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.version_check), null, null, null);
                break;
            case 3:
                tv_status.isChecked(false);
                tv_mapManager.isChecked(false);
                tv_highSet.isChecked(true);
                tv_taskManager.isChecked(false);
                tv_status.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.status_def), null, null, null);
                tv_mapManager.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.map_def), null, null, null);
                tv_highSet.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.hightset_check), null, null, null);
                tv_taskManager.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.version_def), null, null, null);
                break;
        }
    }

    private void initStatusBar() {
        if (notifyBaseStatusEx != null) {
            DecimalFormat df = new DecimalFormat("0");
            DecimalFormat format = new DecimalFormat("0.00");
            currentMap = notifyBaseStatusEx.getCurroute();
            currentTask = notifyBaseStatusEx.getCurrpath();
            xsu=String.valueOf(format.format(notifyBaseStatusEx.getPosLinespeed()));
            jsu=String.valueOf(format.format(notifyBaseStatusEx.getPosAngulauspeed()));
            if(tv_xsu!=null && tv_jsu!=null){
                tv_xsu.setText(getString(R.string.line_speed)+xsu+" m/s");
                tv_jsu.setText(getString(R.string.angulau_speed)+jsu+getString(R.string.angulau_speed_1));
            }
            switch (notifyBaseStatusEx.getStopStat()) {
                case 4:
                    iv_jt_def.setVisibility(View.VISIBLE);
                    iv_jt_def.setCompoundDrawablesWithIntrinsicBounds(null,getResources().getDrawable(R.mipmap.jt_nodef),null,null);
                    iv_yk_def.setVisibility(View.GONE);
                    iv_jt_def.setTextColor(getResources().getColor(R.color.white));
                    iv_yk_def.setTextColor(getResources().getColor(R.color.text_gray));
                    break;
                case 8:
                    iv_yk_def.setVisibility(View.VISIBLE);
                    iv_jt_def.setVisibility(View.GONE);
                    iv_yk_def.setCompoundDrawablesWithIntrinsicBounds(null,getResources().getDrawable(R.mipmap.yk_nodef),null,null);
                    iv_jt_def.setTextColor(getResources().getColor(R.color.text_gray));
                    iv_yk_def.setTextColor(getResources().getColor(R.color.white));
                    break;
                case 12:
                    iv_jt_def.setVisibility(View.VISIBLE);
                    iv_yk_def.setVisibility(View.VISIBLE);
                    iv_jt_def.setCompoundDrawablesWithIntrinsicBounds(null,getResources().getDrawable(R.mipmap.jt_nodef),null,null);
                    iv_yk_def.setCompoundDrawablesWithIntrinsicBounds(null,getResources().getDrawable(R.mipmap.yk_nodef),null,null);
                    iv_jt_def.setTextColor(getResources().getColor(R.color.white));
                    iv_yk_def.setTextColor(getResources().getColor(R.color.white));
                    break;
                case 0:
                    iv_jt_def.setVisibility(View.GONE);
                    iv_yk_def.setVisibility(View.GONE);
                    iv_jt_def.setTextColor(getResources().getColor(R.color.text_gray));
                    iv_yk_def.setTextColor(getResources().getColor(R.color.text_gray));
                    break;
            }
        }
    }

    /**
     * 设置状态栏颜色为浅色
     *
     * @return 返回为true 为黑色
     */
    @Override
    public boolean statusBarDarkFont() {
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
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
                    tcpClient.onDestroy();
                    // 销毁进程（请注意：调用此 API 可能导致当前 Activity onDestroy 方法无法正常回调）
                    // System.exit(0);
                }
            }, 300);
        } else {
            toast(R.string.home_exit_hint);
        }
    }

    /**
     * 返回登陆界面
     */
    public void onBack() {
        Intent intent_login = new Intent();
        intent_login.setClass(HomeActivity.this, LoginActivity.class);
        intent_login.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //关键的一句，将新的activity置为栈顶
        startActivity(intent_login);
        finish();

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
        tcpClient.disConnect();
        tcpAiClient.disConnect();
        editor.putFloat("speed", (float) maxSpeed);
        editor.commit();
        super.onDestroy();
    }



    private boolean exit=false;       //线程是否被终止
    public void getMapInfo() {
        new Thread(() -> {
            while (!exit) {
                if (currentMap != null && !currentMap.equals("PathError")) {
                    Logger.e("-----------" + currentMap);
                    tcpClient.getMapInfo(ByteString.copyFromUtf8(currentMap));  //获取某个地图信息
                    exit=true;
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    /**
     * 遥控弹窗
     */
    private void showControlPopupWindow() {
        View contentView = null;
        contentView = LayoutInflater.from(this).inflate(R.layout.dialog_yk_check, null);
        customPopuWindow = new CustomPopuWindow.PopupWindowBuilder(this)
                .setView(contentView)
                .size(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                .enableOutsideTouchableDissmiss(true)// 设置点击PopupWindow之外的地方，popWindow不关闭，如果不设置这个属性或者为true，则关闭
                .setOutsideTouchable(false)//是否PopupWindow 以外触摸dissmiss
                .create()
                .showAsDropDown(findViewById(R.id.taskmanager),0,-10);
        seekBar=contentView.findViewById(R.id.seek_bar_control);
        fixedSpeed=contentView.findViewById(R.id.fixed_speed);
        myRocker=contentView.findViewById(R.id.my_rocker_control);
        myRockerZy=contentView.findViewById(R.id.my_rocker_zy_control);
        tvSpeed=contentView.findViewById(R.id.tv_speed_control);
        iv_quit_yk=contentView.findViewById(R.id.iv_quit_yk);
        tv_xsu=contentView.findViewById(R.id.robot_xsu);//线速度
        tv_jsu=contentView.findViewById(R.id.robot_jsu);//角速度
        initSeekBar();
        initRockerView();
        initTimer();
        setFixedSpeed();
        maxSpeed = sharedPreferences.getFloat("speed", (float) 0.4);
        seekBar.setProgress((float) maxSpeed);
        tvSpeed.setText(String.valueOf(maxSpeed));
        iv_quit_yk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customPopuWindow.dissmiss();
                timer.cancel();
                task.cancel();
                try {
                    FloatWindow.get().show();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

    }

    @SuppressLint("NewApi")
    private void initSeekBar() {
        seekBar.setOnRangeChangedListener(new OnRangeChangedListener() {
            @Override
            public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {
                if (!ishaveChecked) {
                    editor.putFloat("speed", (float) maxSpeed);                 //保存最近的改变速度
                    editor.commit();
                    tvSpeed.setText(String.valueOf(maxSpeed));
                }
                Logger.e("------" + seekBar.getLeftSeekBar().getProgress());
                maxSpeed = seekBar.getLeftSeekBar().getProgress();
            }

            @Override
            public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {

            }

            @Override
            public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft) {

            }
        });
    }

    /**
     * 自定义摇杆View的相关操作
     * 作用：监听摇杆的方向，角度，距离
     */
    private void initRockerView() {
        myRocker.setOnShakeListener(DIRECTION_2_VERTICAL, new RockerView.OnShakeListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void direction(RockerView.Direction direction) {
                if (direction == RockerView.Direction.DIRECTION_CENTER) {           // "当前方向：中心"
                    //Logger.e("---中心");
                    lineSpeed = 0;
                    myRocker.setmAreaBackground(R.mipmap.rocker_base_default);
                } else if (direction == RockerView.Direction.DIRECTION_DOWN) {     // 当前方向：下
                    isforward = false;
                    myRocker.setmAreaBackground(R.mipmap.rocker_backward);
                    //Logger.e("下");
                } else if (direction == RockerView.Direction.DIRECTION_LEFT) {    //当前方向：左

                } else if (direction == RockerView.Direction.DIRECTION_UP) {      //当前方向：上
                    isforward = true;
                    myRocker.setmAreaBackground(R.mipmap.rocker_forward);
                    //Logger.e("上");
                } else if (direction == RockerView.Direction.DIRECTION_RIGHT) {

                }
            }

            @Override
            public void onFinish() {

            }
        });

        myRockerZy.setOnShakeListener(DIRECTION_2_HORIZONTAL, new RockerView.OnShakeListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void direction(RockerView.Direction direction) {
                if (direction == RockerView.Direction.DIRECTION_CENTER) {           // "当前方向：中心"
                    // Logger.e("---中心");
                    myRockerZy.setmAreaBackground(R.mipmap.rocker_default_zy);
                    palstance = 0;
                } else if (direction == RockerView.Direction.DIRECTION_DOWN) {

                } else if (direction == RockerView.Direction.DIRECTION_LEFT) {    //当前方向：左
                    isGoRight = false;
                    myRockerZy.setmAreaBackground(R.mipmap.rocker_go_left);
                    // Logger.e("左");
                } else if (direction == RockerView.Direction.DIRECTION_UP) {      //当前方向：上

                } else if (direction == RockerView.Direction.DIRECTION_RIGHT) {
                    //Logger.e("右");
                    isGoRight = true;
                    myRockerZy.setmAreaBackground(R.mipmap.rocker_go_right);
                }
            }

            @Override
            public void onFinish() {

            }
        });

        /*** lambda 表达式 Java8*/
        myRockerZy.setOnDistanceLevelListener((level) -> {
                    DecimalFormat df = new DecimalFormat("#.00");
                    palstance = Float.parseFloat(df.format(maxSpeed * level / 10));
                    if (isGoRight) {
                        palstance = -palstance;
                    }
                }
        );

        myRocker.setOnDistanceLevelListener((level -> {
            DecimalFormat df = new DecimalFormat("#.00");
            lineSpeed = Float.parseFloat(df.format(maxSpeed * level / 10));
            if (!isforward) {
                lineSpeed = -lineSpeed;
            }
        }));

    }

    Timer timer;
    TimerTask task;
    int a = 0;

    /**
     * 定时器，每90毫秒执行一次
     */
    private void initTimer() {
        timer = new Timer();
        task = new TimerTask() {
            @Override

            public void run() {
                // Logger.e("线速度，角速度："+lineSpeed+";"+palstance);
                if (lineSpeed == 0 && palstance == 0) {
                    a++;
                    if (a <= 5) {
                        //Logger.e("----a:" + a);
                        tcpClient.sendSpeed(lineSpeed, palstance);
                    }
                } else {
                    a = 0;
                    //Logger.e("线速度，角速度：" + lineSpeed + ";" + palstance);
                    tcpClient.sendSpeed(lineSpeed, palstance);
                }
            }
        };
        timer.schedule(task, 0, 50);
    }


    /**
     * 固定速度
     */
    public void setFixedSpeed() {
        fixedSpeed.setOnCheckedChangeListener(((buttonView, isChecked) -> {
            if (isChecked) {
                ishaveChecked = isChecked;
                maxSpeed = sharedPreferences.getFloat("speed", (float) 0.4);
                Logger.e("-----" + maxSpeed);
                tvSpeed.setText(String.valueOf(maxSpeed));
                seekBar.setEnabled(false);
                toast(R.string.common_lock);
            } else {
                seekBar.setEnabled(true);
                ishaveChecked = isChecked;
                maxSpeed = sharedPreferences.getFloat("speed", (float) 0.4);
                seekBar.setProgress((float) maxSpeed);
                tvSpeed.setText(String.valueOf(maxSpeed));
                toast(R.string.common_cancel_lock);

            }
        }));
    }

    @Override
    public void onSoftKeyboardOpened(int keyboardHeight) {

    }

    @Override
    public void onSoftKeyboardClosed() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!tcpClient.isConnected()){
            Logger.e("网络已断开");
            //netWorkStatusDialog();
        }
    }

    /**
     * 显示网络连接弹窗
     */
    private void  netWorkStatusDialog(){
        waitDialog=new WaitDialog.Builder(this).setMessage(R.string.common_network_connecting).show();
        postDelayed(()->{
            if (waitDialog.isShowing()){
                toast(R.string.network_not_connect);
                ActivityStackManager.getInstance().finishAllActivities();
                startActivity(LoginActivity.class);
            }
        },6000);
    }

    public UdpClient udpClient;
    private int aiPort=18888;
    public TcpAiClient tcpAiClient;
    private UdpIp udpIp1=new UdpIp();
    /**
     * 接收AIServer广播
     */
    private void receiveAiBroadcast(){
        Logger.e("接受AI广播");
        udpClient= UdpClient.getInstance(this,ClientMessageDispatcher.getInstance());
        try {
            udpClient.connect(aiPort);
        } catch (IOException e) {
            e.printStackTrace();
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


}

