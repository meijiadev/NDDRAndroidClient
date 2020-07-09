package ddr.example.com.nddrandroidclient.ui.activity;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.google.protobuf.ByteString;
import com.jaygoo.widget.VerticalRangeSeekBar;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import DDRCommProto.BaseCmd;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import java.io.IOException;
import java.text.DecimalFormat;

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
import ddr.example.com.nddrandroidclient.ui.dialog.RelocationDialog;
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
    private String currentMap;          //当前运行的地图名
    private String currentBitmapPath;   //当前使用的图片存储地址
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
    private BaseDialog relocationDialog;
    /**
     * ViewPage 适配器
     */
    private BaseFragmentAdapter<DDRLazyFragment> mPagerAdapter;
    private int relocationStatus;


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void update(MessageEvent messageEvent) {
        switch (messageEvent.getType()) {
            case updateBaseStatus:
                initStatusBar();
                break;
            case updateRelocationStatus:
                relocationStatus= (int) messageEvent.getData();
                switch (relocationStatus){
                    case 0:
                        if (relocationDialog!=null&&relocationDialog.isShowing()){
                            relocationDialog.dismiss();
                            relocationDialog=null;
                        }
                        new InputDialog.Builder(this)
                                .setEditVisibility(View.GONE)
                                .setTitle(R.string.relocation_failed_1)
                                .setConfirm(R.string.common_yes)
                                .setCancel(R.string.common_no)
                                .setCanceledOnTouchOutside(false)    // 是否可以点击外部取消弹窗
                                .setListener(new InputDialog.OnListener() {
                                    @Override
                                    public void onConfirm(BaseDialog dialog, String content) {
                                        Intent intent = new Intent(HomeActivity.this, RelocationActivity.class);
                                        intent.putExtra("currentBitmap", currentBitmapPath);
                                        intent.putExtra("currentMapName", currentMap);
                                        startActivity(intent);
                                    }

                                    @Override
                                    public void onCancel(BaseDialog dialog) {

                                    }
                                }).show();
                        break;
                    case 1:
                        toast(R.string.relocation_succeed);
                        if (relocationDialog!=null){
                            relocationDialog.dismiss();
                            relocationDialog=null;
                        }
                        break;
                }
                if (relocationStatus==1&&vpHomePager!=null){
                    vpHomePager.setCurrentItem(0);
                }
                break;
            case updateMapList:
                Logger.d("-------------updateMapList");
                getMapInfo();
                break;
            case switchTaskSuccess:
                Logger.d("--------更新地图");
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
            case updateAiPort:
                udpIp1= (UdpIp) messageEvent.getData();
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
        //如果是采集模式直接进入采集页面
        if (notifyBaseStatusEx.getMode()==2){
            tcpClient.getAllLidarMap();
            Intent intent = new Intent(HomeActivity.this, CollectingActivity.class);
            startActivity(intent);
        }
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


    @OnClick({R.id.iv_menu,R.id.status, R.id.mapmanager, R.id.taskmanager, R.id.highset,R.id.tv_quit,R.id.tv_shutdown,R.id.tv_switch_language,R.id.tv_charging})
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
                onBack();
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
                if (!SpUtil.getInstance(context).getBoolean(SpUtil.CHARGE_STATUS)){
                    toast(R.string.auto_charge_notify_1);
                }else if (!haveChargePoint()){
                    toast(R.string.auto_charge_notify_2);
                }else {
                    toast(R.string.auto_charge_notify_3);
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
            currentBitmapPath = GlobalParameter.ROBOT_FOLDER + currentMap + "/" + "bkPic.png";
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
            //重定位中
            if (notifyBaseStatusEx.getSonMode()==15){
                Logger.e("进入重定位...");
                if (relocationDialog==null){
                    relocationDialog = new RelocationDialog.Builder(this)
                            .setAutoDismiss(true)
                            .setListener(new RelocationDialog.OnListener() {
                                @Override
                                public void onHandMovement() {
                                    tcpClient.exitModel();
                                    Intent intent = new Intent(HomeActivity.this, RelocationActivity.class);
                                    intent.putExtra("currentBitmap", currentBitmapPath);
                                    intent.putExtra("currentMapName", currentMap);
                                    startActivity(intent);
                                    relocationDialog=null;
                                }
                                @Override
                                public void onCancelRelocation() {
                                    tcpClient.exitModel();
                                    relocationDialog=null;
                                }
                            })
                            .show();
                }
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
        tcpClient.onDestroy();

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
        tcpClient.onDestroy();
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

    @Override
    public void onSoftKeyboardOpened(int keyboardHeight) {

    }

    @Override
    public void onSoftKeyboardClosed() {

    }

    @Override
    protected void onResume() {
        super.onResume();
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

