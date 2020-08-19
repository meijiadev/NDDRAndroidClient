package ddr.example.com.nddrandroidclient.ui.activity;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.protobuf.ByteString;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.List;

import DDRCommProto.BaseCmd;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.base.BaseApplication;
import ddr.example.com.nddrandroidclient.base.BaseDialog;
import ddr.example.com.nddrandroidclient.base.BaseFragmentAdapter;
import ddr.example.com.nddrandroidclient.common.DDRActivity;
import ddr.example.com.nddrandroidclient.common.DDRLazyFragment;
import ddr.example.com.nddrandroidclient.common.GlobalParameter;
import ddr.example.com.nddrandroidclient.entity.MessageEvent;
import ddr.example.com.nddrandroidclient.entity.PointType;
import ddr.example.com.nddrandroidclient.entity.info.MapFileStatus;
import ddr.example.com.nddrandroidclient.entity.info.NotifyBaseStatusEx;
import ddr.example.com.nddrandroidclient.entity.other.ComputerEditions;
import ddr.example.com.nddrandroidclient.entity.other.SensorSea;
import ddr.example.com.nddrandroidclient.entity.other.SensorSeas;
import ddr.example.com.nddrandroidclient.entity.other.Sensors;
import ddr.example.com.nddrandroidclient.entity.other.UdpIp;
import ddr.example.com.nddrandroidclient.entity.point.TargetPoint;
import ddr.example.com.nddrandroidclient.glide.ImageLoader;
import ddr.example.com.nddrandroidclient.helper.ActivityStackManager;
import ddr.example.com.nddrandroidclient.helper.DoubleClickHelper;
import ddr.example.com.nddrandroidclient.helper.SpUtil;
import ddr.example.com.nddrandroidclient.language.LanguageType;
import ddr.example.com.nddrandroidclient.language.LanguageUtil;
import ddr.example.com.nddrandroidclient.other.DpOrPxUtils;
import ddr.example.com.nddrandroidclient.other.KeyboardWatcher;
import ddr.example.com.nddrandroidclient.other.Logger;
import ddr.example.com.nddrandroidclient.protocobuf.CmdSchedule;
import ddr.example.com.nddrandroidclient.protocobuf.dispatcher.ClientMessageDispatcher;
import ddr.example.com.nddrandroidclient.socket.TcpAiClient;
import ddr.example.com.nddrandroidclient.socket.TcpClient;
import ddr.example.com.nddrandroidclient.socket.UdpClient;
import ddr.example.com.nddrandroidclient.ui.adapter.SensorAdapter;
import ddr.example.com.nddrandroidclient.ui.dialog.ControlPopupWindow;
import ddr.example.com.nddrandroidclient.ui.dialog.InputDialog;
import ddr.example.com.nddrandroidclient.ui.dialog.RelocationDialog;
import ddr.example.com.nddrandroidclient.ui.fragment.MapFragment;
import ddr.example.com.nddrandroidclient.ui.fragment.SetUpFragment;
import ddr.example.com.nddrandroidclient.ui.fragment.StatusFragment;
import ddr.example.com.nddrandroidclient.ui.fragment.TaskFragment;
import ddr.example.com.nddrandroidclient.widget.textview.LineTextView;
import ddr.example.com.nddrandroidclient.widget.view.CustomPopuWindow;
import ddr.example.com.nddrandroidclient.widget.view.DDRViewPager;

/**
 * time:2019/10/26
 * desc: 主页界面
 */
public class HomeActivity extends DDRActivity implements ViewPager.OnPageChangeListener, KeyboardWatcher.SoftKeyboardStateListener {
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
    @BindView(R.id.iv_see_obstacle)
    TextView ivSeeObstacle;


    private TcpClient tcpClient;
    private NotifyBaseStatusEx notifyBaseStatusEx;
    private String currentMap;          //当前运行的地图名
    private String currentBitmapPath;   //当前使用的图片存储地址
    private CustomPopuWindow customPopuWindow;
    private SharedPreferences.Editor editor;

    private String language;
    private MapFileStatus mapFileStatus;
    private BaseDialog relocationDialog;
    boolean isstart=false;
    /**
     * ViewPage 适配器
     */
    private BaseFragmentAdapter<DDRLazyFragment> mPagerAdapter;


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void update(MessageEvent messageEvent) {
        String LAN_IP_AI = "192.168.0.95";
        switch (messageEvent.getType()) {
            case updateBaseStatus:
                initStatusBar();
                break;
            case updateRelocationStatus:
                int relocationStatus = (int) messageEvent.getData();
                switch (relocationStatus) {
                    case 0:
                        if (relocationDialog != null && relocationDialog.isShowing()) {
                            relocationDialog.dismiss();
                            relocationDialog = null;
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
                        if (relocationDialog != null) {
                            relocationDialog.dismiss();
                            relocationDialog = null;
                        }
                        break;
                }
                if (relocationStatus == 1 && vpHomePager != null) {
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
                postDelayed(this::getMapInfo, 800);
                break;
            case touchFloatWindow:
                if (notifyBaseStatusEx.isChargingStatus()) {
                    toast("正在充电，底盘已关闭运动");
                } else {
                    switch (notifyBaseStatusEx.getChargingSubStatus()) {
                        case 1:
                        case 2:
                        case 4:
                            toast("正在充电，底盘已关闭运动");
                            break;
                        default:
                            String className = ActivityStackManager.getInstance().getTopActivity().getClass().toString();
                            Logger.e("--------当前栈顶的活动:" + className + ";" + HomeActivity.class.toString());
                            if (className.equals(HomeActivity.class.toString()))
                                new ControlPopupWindow(this).showControlPopupWindow(findViewById(R.id.taskmanager));
                            break;
                    }
                }
                break;
            case updateAiPort:
                UdpIp udpIp1 = (UdpIp) messageEvent.getData();
                Logger.e("AIip" + udpIp1.getIp() + "端口" + udpIp1.getPort());
                tcpAiClient.createConnect(LAN_IP_AI, udpIp1.getPort());
                break;
            case tcpAiConnected:
                Logger.e("TcpAI服务开始连接");
                tcpAiClient.sendData(null, CmdSchedule.localLogin("admin_android", "admin_android", 4));
                break;
            case LoginAiSuccess:
                toast(R.string.ai_connected);
                UdpClient.getInstance(context, ClientMessageDispatcher.getInstance()).close();
                break;
            case updateVersion:
                ComputerEditions computerEditions = ComputerEditions.getInstance();
                Logger.e("机器类型" + computerEditions.getRobotType());
                if (computerEditions.getRobotType() == 3) {

                } else {
                    UdpClient.getInstance(context, ClientMessageDispatcher.getInstance()).close();
                    Logger.e("非消杀无需连接Ai");
                }
                break;
            case exceptCode_NoLocated:
                toast(R.string.now_no_location);
                break;
            case exceptCode_GeneralPathFailed:
                toast(R.string.build_task_faild);
                break;
            case exceptCode_NoChargingPoint:
                toast(R.string.no_charging_point);
                break;
            case updateSenesorSea:
                sensorSeaList= sensorSeas.getSensorSeaList();
                if (sensorAdapter!=null){
                    sensorAdapter.setNewData(sensorSeaList);
                }
                for (int i =0;i<sensorSeaList.size();i++){
                    if (sensorSeaList.get(i).getTriggerStat()==1){
                        isstart=true;
                        break;
                    }
                    isstart=false;
                }
                if (isstart){
                    ivSeeObstacle.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap.obstacle_check),null,null);
                }else {
                    ivSeeObstacle.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap.no_obstacle),null,null);
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
        Logger.e("当前的语言：" + language);
        if (language.equals(LanguageType.CHINESE.getLanguage())) {
            tv_switch_language.setText(R.string.switch_to_en);
            language = LanguageType.ENGLISH.getLanguage();
        } else if (language.equals(LanguageType.ENGLISH.getLanguage())) {
            language = LanguageType.CHINESE.getLanguage();
            tv_switch_language.setText(R.string.switch_to_cn);
        } else {
            language = LanguageType.ENGLISH.getLanguage();
            tv_switch_language.setText(R.string.switch_to_en);
        }
    }


    @SuppressLint("CommitPrefEdits")
    @Override
    protected void initData() {
        tcpClient = TcpClient.getInstance(context, ClientMessageDispatcher.getInstance());
        mapFileStatus = MapFileStatus.getInstance();
        tcpClient.getHostComputerEdition();
        getSensorParam();
        receiveAiBroadcast();
        tcpAiClient = TcpAiClient.getInstance(context, ClientMessageDispatcher.getInstance());
        notifyBaseStatusEx = NotifyBaseStatusEx.getInstance();
        ImageLoader.clear(this); //清除图片缓存
        tcpClient.requestFile();     //请求所有地图
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();
        //如果是采集模式直接进入采集页面
        if (notifyBaseStatusEx.getMode() == 2) {
            postDelayed(() -> {
                tcpClient.getAllLidarMap();
                Logger.e("查询本地数据库！");
                EventBus.getDefault().postSticky(new MessageEvent(MessageEvent.Type.getRobotCoordinates));
                Intent intent = new Intent(HomeActivity.this, CollectingActivity.class);
                startActivity(intent);
            }, 1000);
        }
        sensorSeas=SensorSeas.getInstance();
        sensorSeaList=sensorSeas.getSensorSeaList();
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


    @OnClick({R.id.iv_menu, R.id.status, R.id.mapmanager, R.id.taskmanager, R.id.highset, R.id.tv_quit, R.id.tv_shutdown, R.id.tv_switch_language, R.id.tv_charging,R.id.iv_see_obstacle})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_menu:
                if (drawerLayout.isDrawerOpen(drawerLayoutLeft)) {
                    drawerLayout.closeDrawer(drawerLayoutLeft);
                } else {
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
                if (!SpUtil.getInstance(context).getBoolean(SpUtil.CHARGE_STATUS)) {
                    toast(R.string.auto_charge_notify_1);
                } else if (!haveChargePoint()) {
                    toast(R.string.auto_charge_notify_2);
                } else {
                    toast(R.string.auto_charge_notify_3);
                    tcpClient.goToCharge();
                }
                break;
            case R.id.iv_see_obstacle:
//                showControlPopupWindow(ivSeeObstacle);
                break;
        }
        isChecked();
    }

    /**
     * 如果是7.0以下，我们需要调用changeAppLanguage方法，
     * 如果是7.0及以上系统，直接把我们想要切换的语言类型保存在SharedPreferences中即可
     * 然后重新启动MainActivity
     *
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
     *
     * @return
     */
    private boolean haveChargePoint() {
        for (TargetPoint targetPoint : mapFileStatus.getcTargetPoints()) {
            if (targetPoint.getPointType().equals(PointType.eMarkingTypeCharging)) {
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
            currentMap = notifyBaseStatusEx.getCurroute();
            currentBitmapPath = GlobalParameter.ROBOT_FOLDER + currentMap + "/" + "bkPic.png";
            switch (notifyBaseStatusEx.getStopStat()) {
                case 4:
                    iv_jt_def.setVisibility(View.VISIBLE);
                    iv_jt_def.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap.jt_nodef), null, null);
                    iv_yk_def.setVisibility(View.GONE);
                    iv_jt_def.setTextColor(getResources().getColor(R.color.white));
                    iv_yk_def.setTextColor(getResources().getColor(R.color.text_gray));
                    break;
                case 8:
                    iv_yk_def.setVisibility(View.VISIBLE);
                    iv_jt_def.setVisibility(View.GONE);
                    iv_yk_def.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap.yk_nodef), null, null);
                    iv_jt_def.setTextColor(getResources().getColor(R.color.text_gray));
                    iv_yk_def.setTextColor(getResources().getColor(R.color.white));
                    break;
                case 12:
                    iv_jt_def.setVisibility(View.VISIBLE);
                    iv_yk_def.setVisibility(View.VISIBLE);
                    iv_jt_def.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap.jt_nodef), null, null);
                    iv_yk_def.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap.yk_nodef), null, null);
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
            if (notifyBaseStatusEx.getSonMode() == 15) {
                Logger.e("进入重定位...");
                if (relocationDialog == null || !relocationDialog.isShowing()) {
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
                                    relocationDialog = null;
                                }

                                @Override
                                public void onCancelRelocation() {
                                    tcpClient.exitModel();
                                    relocationDialog = null;
                                }
                            })
                            .show();
                }
            }
            try {
                if (notifyBaseStatusEx.getSonMode()==17){
                    if (tv_ld!=null){
                        tv_ld.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.mipmap.checkedwg),null);
                    }
                    ivSeeObstacle.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap.obstacle_check),null,null);
                }else{
                    if (tv_ld!=null){
                        tv_ld.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.mipmap.nocheckedwg),null);
                    }
                    ivSeeObstacle.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap.no_obstacle),null,null);
                }
            }catch (Exception e){
                e.printStackTrace();
            }


        }
    }

    /**
     * 获取避障信息
     */
    private CustomPopuWindow customPopWindow;
    private RecyclerView recyclerView_c_sensor;
    private SensorSeas sensorSeas;
    private List<SensorSea> sensorSeaList;
    private SensorAdapter sensorAdapter;
    private TextView tv_ld;
    public void showControlPopupWindow(View view) {
        View contentView = null;
        contentView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_see_ob, null);
        Logger.d("长宽"+contentView.findViewById(R.id.rel_see).getWidth()+"---"+contentView.findViewById(R.id.rel_see).getHeight());
        customPopWindow = new CustomPopuWindow.PopupWindowBuilder(getActivity())
                .setView(contentView)
                .size(600,400)
                .enableOutsideTouchableDissmiss(true)// 设置点击PopupWindow之外的地方，popWindow不关闭，如果不设置这个属性或者为true，则关闭
                .setOutsideTouchable(false)//是否PopupWindow 以外触摸dissmiss
                .create()
                .showAsDropDown(view, DpOrPxUtils.dip2px(getActivity(), 0), 5);
        recyclerView_c_sensor =contentView.findViewById(R.id.recycle_see_cs);
        tv_ld=contentView.findViewById(R.id.tv_see_ld);
        sensorAdapter = new SensorAdapter(R.layout.item_recycle_seeob);
        GridLayoutManager gridLayoutManager =new GridLayoutManager(getActivity(),4);
        recyclerView_c_sensor.setLayoutManager(gridLayoutManager);
        recyclerView_c_sensor.setAdapter(sensorAdapter);
        sensorAdapter.setNewData(sensorSeaList);
    }

    //获取传感器参数
    private void getSensorParam(){
        BaseCmd.eSensorConfigItemOptType eSensorConfigItemOptType;
        eSensorConfigItemOptType=BaseCmd.eSensorConfigItemOptType.eSensorConfigOptTypeGetData;
        BaseCmd.reqSensorConfigOperational reqSensorConfigOperational = BaseCmd.reqSensorConfigOperational.newBuilder()
                .setType(eSensorConfigItemOptType)
                .build();
        tcpClient.sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eModuleServer), reqSensorConfigOperational);
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
                    tcpClient.disConnect();
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
        tcpClient.disConnect();

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
        //设置的最大速度
        double maxSpeed = 0.4;
        editor.putFloat("speed", (float) maxSpeed);
        editor.commit();
        super.onDestroy();
    }


    private boolean exit = false;       //线程是否被终止

    public void getMapInfo() {
        new Thread(() -> {
            while (!exit) {
                if (currentMap != null && !currentMap.equals("PathError")) {
                    Logger.e("-----------" + currentMap);
                    tcpClient.getMapInfo(ByteString.copyFromUtf8(currentMap));  //获取某个地图信息
                    exit = true;
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


    @Override
    protected void onPause() {
        super.onPause();
    }

    public UdpClient udpClient;
    public TcpAiClient tcpAiClient;

    /**
     * 接收AIServer广播
     */
    private void receiveAiBroadcast() {
        Logger.e("接受AI广播");
        udpClient = UdpClient.getInstance(this, ClientMessageDispatcher.getInstance());
        try {
            int aiPort = 18888;
            udpClient.connect(aiPort);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}

