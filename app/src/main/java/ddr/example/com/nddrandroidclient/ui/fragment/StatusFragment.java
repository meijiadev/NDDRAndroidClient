package ddr.example.com.nddrandroidclient.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import androidx.recyclerview.widget.RecyclerView;

import com.google.protobuf.ByteString;
import com.yhao.floatwindow.FloatWindow;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import DDRCommProto.BaseCmd;
import DDRVLNMapProto.DDRVLNMap;
import butterknife.BindView;
import butterknife.OnClick;
import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.base.BaseDialog;
import ddr.example.com.nddrandroidclient.common.DDRLazyFragment;
import ddr.example.com.nddrandroidclient.entity.info.MapFileStatus;
import ddr.example.com.nddrandroidclient.entity.MessageEvent;
import ddr.example.com.nddrandroidclient.entity.info.NotifyBaseStatusEx;
import ddr.example.com.nddrandroidclient.entity.info.NotifyEnvInfo;
import ddr.example.com.nddrandroidclient.entity.point.TargetPoint;
import ddr.example.com.nddrandroidclient.entity.point.TaskMode;
import ddr.example.com.nddrandroidclient.other.DpOrPxUtils;
import ddr.example.com.nddrandroidclient.other.Logger;
import ddr.example.com.nddrandroidclient.protocobuf.CmdSchedule;
import ddr.example.com.nddrandroidclient.protocobuf.dispatcher.ClientMessageDispatcher;
import ddr.example.com.nddrandroidclient.socket.TcpClient;
import ddr.example.com.nddrandroidclient.ui.activity.HomeActivity;
import ddr.example.com.nddrandroidclient.ui.activity.RelocationActivity;
import ddr.example.com.nddrandroidclient.ui.adapter.NGridLayoutManager;
import ddr.example.com.nddrandroidclient.ui.adapter.NLinearLayoutManager;
import ddr.example.com.nddrandroidclient.ui.adapter.StringAdapter;
import ddr.example.com.nddrandroidclient.ui.adapter.TargetPointAdapter;
import ddr.example.com.nddrandroidclient.ui.dialog.InputDialog;
import ddr.example.com.nddrandroidclient.ui.dialog.NormalDialog;
import ddr.example.com.nddrandroidclient.ui.dialog.WaitDialog;
import ddr.example.com.nddrandroidclient.widget.view.CircleBarView;
import ddr.example.com.nddrandroidclient.widget.view.CustomPopuWindow;
import ddr.example.com.nddrandroidclient.widget.StatusSwitchButton;
import ddr.example.com.nddrandroidclient.widget.zoomview.MapImageView;

/**
 * time: 2019/10/26
 * desc: 基础状态界面
 */
public  class StatusFragment extends DDRLazyFragment<HomeActivity>implements StatusSwitchButton.OnStatusSwitchListener,Animation.AnimationListener{

    @BindView(R.id.status_switch_bt)
    StatusSwitchButton statusSwitchButton;
    @BindView(R.id.circle)
    CircleBarView circleBarView;
    @BindView(R.id.iv_shrink)
    ImageView ivShrink;      //点击头部伸缩
    @BindView(R.id.shrink_tail_layout)
    RelativeLayout shrinkTailLayout; // 伸缩的尾部布局
    @BindView(R.id.shrink_layout)
    RelativeLayout shrinkLayout;
    @BindView(R.id.tv_warn)
    TextView tvWarn;                  // 无地图显示提醒

    @BindView(R.id.iv_map1)
    MapImageView mapImageView;
    @BindView(R.id.tv_now_task)
    TextView tv_now_task;
    @BindView(R.id.tv_now_device)
    TextView tv_now_device;
    @BindView(R.id.tv_now_map)
    TextView tv_now_map;
    @BindView(R.id.tv_work_statue)
    TextView tv_work_statue;
    @BindView(R.id.tv_task_num)
    TextView tv_task_num;
    @BindView(R.id.tv_task_speed)
    TextView tv_task_speed;
    @BindView(R.id.tv_work_time)
    TextView tv_work_time;
    @BindView(R.id.rel_step_description)
    RelativeLayout rel_step_description;
    @BindView(R.id.recycle_gopoint)
    RecyclerView recyclerGoPoint;
    @BindView(R.id.iv_cd_xs)
    ImageView iv_cd_xs;
    @BindView(R.id.iv_task_xl)
    ImageView iv_task_xl;
    @BindView(R.id.tv_set_go)
    TextView tv_set_go;
    @BindView(R.id.tv_restart_point)
    TextView tv_restart_point;
    @BindView(R.id.left_layout)
    RelativeLayout leftLayout;                //非充电状态下的左侧布局
    @BindView(R.id.charging_layout)
    RelativeLayout chargingLayout;             //充电模式下的左侧布局
    @BindView(R.id.iv_charge)
    ImageView ivCharge;                       //充电状态的图标  附带动画效果
    @BindView(R.id.tv_electric_quantity)
    TextView tvElectricQuantity;              //电池电量充电时的
    @BindView(R.id.bt_exit_charge)
    Button btExitCharge;                      //退出充电模式
    @BindView(R.id.tv_charge_notify)
    TextView tvChargeNotify;

    private Animation hideAnimation;  //布局隐藏时的动画
    private Animation showAnimation;  // 布局显示时的动画效果
    private AnimationDrawable chargeAnimation;

    private NotifyEnvInfo notifyEnvInfo;
    private NotifyBaseStatusEx notifyBaseStatusEx;

    private static String regEx="[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";//特殊字符
    private TcpClient tcpClient;
    private String mapName;//地图名
    private String taskName;//任务名
    public static String robotID;//机器人ID
    private String workStatus; //工作状态
    private int lsNum=1; //临时任务次数
    private List<String> groupList=new ArrayList<>();
    private List<TargetPoint> targetPoints= new ArrayList<>();
    private TargetPointAdapter targetPointAdapter;
    private MapFileStatus mapFileStatus;
    private StringAdapter taskCheckAdapter;
    private CustomPopuWindow customPopWindow;
    private String sPoint="未知点";
    private boolean isRabPoint;               //是否在跑ab点

    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void update(MessageEvent messageEvent){
        switch (messageEvent.getType()){
            case updateBaseStatus:
                initStatusBar();
                break;
            case getSwitchTaskSuccess:
                toast(R.string.add_task_success);
                break;
            case getSwitchTaskFailed:
                toast(R.string.add_task_faild);
                break;
            case getSpecificPoint:
                String pointName = (String) messageEvent.getData();
                if (sPoint.equals(pointName))
                toast(getString(R.string.start_goto)+ pointName);
                break;
            case getSpecificPoint1:
                Logger.e("------添加任务成功，等待前往"+sPoint);
                pointName = (String) messageEvent.getData();
                if (sPoint.equals(pointName)){
                    toast(getString(R.string.add_s_d_goto)+ pointName);
                }
                break;
            case getSpecificPoint2:
                toast(R.string.now_no_task);
                break;
            case getSpecificPoint3:
                toast(R.string.now_no_location);
                break;
            case getSpecificPoint4:
                toast(R.string.build_task_faild);
                isRabPoint =false;
                break;
            case getSpecificPoint5:
                toast(R.string.now_Self_calibration);
                break;
            case getSpecificPoint8:
                toast(R.string.back_Standby);
                break;
            case getSpecificPoint9:
                toast(R.string.s_now_task);
                isRabPoint =false;
                break;
            case getSpecificPoint10:
                toast(R.string.no_task_yd);
                break;
            case getSpecificPoint11:
                pointName = (String) messageEvent.getData();
                if (sPoint.equals(pointName))
                toast(getString(R.string.about_to_start)+sPoint);
                break;
            case switchMapSucceed:
                for (int i = 0; i < targetPoints.size(); i++) {
                    targetPoints.get(i).setSelected(false);
                }
                break;
            case GoToChargingPoint:
                BaseCmd.eCmdRspType eCmdRspType= (BaseCmd.eCmdRspType) messageEvent.getData();
                if (eCmdRspType.equals(BaseCmd.eCmdRspType.eSuccess)){
                    mapImageView.setTargetPoint(null);
                }else if (eCmdRspType.equals(BaseCmd.eCmdRspType.eCmdFailed)){
                    toast(R.string.commonFailedToCharge);
                }
                break;
            case updateDDRVLNMap:
                Logger.e("------地图名："+mapFileStatus.getMapName()+"当前"+mapName);
                if (mapFileStatus.getMapName().equals(mapName)){
                    tvWarn.setVisibility(View.GONE);
                    //Logger.e("group列数"+groupList.size()+"列数1"+mapFileStatus.getTaskModes().size()+" -- "+mapFileStatus.getcTaskModes().size());
                    mapImageView.setImageBitmap(mapName);
                    groupList = new ArrayList<>();
                    targetPoints=new ArrayList<>();
                    for (TaskMode taskMode:mapFileStatus.getcTaskModes()){
                        groupList.add(taskMode.getName());
                    }
                    taskCheckAdapter.setNewData(groupList);
                    targetPoints=mapFileStatus.getcTargetPoints();
                    targetPointAdapter.setNewData(targetPoints);
                    mapImageView.setABPointLine(isRabPoint);
                }
                break;
        }

    }

    public static StatusFragment newInstance(){
        return new StatusFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_status;
    }

    @Override
    protected void initView() {
        statusSwitchButton.setOnStatusListener(this);
        hideAnimation=AnimationUtils.loadAnimation(getAttachActivity(),R.anim.view_hide);
        showAnimation=AnimationUtils.loadAnimation(getAttachActivity(),R.anim.view_show);
        chargeAnimation= (AnimationDrawable) ivCharge.getBackground();
        taskCheckAdapter=new StringAdapter(R.layout.item_recycle_task_check);
        targetPointAdapter=new TargetPointAdapter(R.layout.item_recycle_gopoint);
        NGridLayoutManager gridLayoutManager = new NGridLayoutManager(getAttachActivity(), 4);
        recyclerGoPoint.setLayoutManager(gridLayoutManager);
        recyclerGoPoint.setAdapter(targetPointAdapter);
        onItemClick(2);
    }


    @Override
    protected void initData() {
        tcpClient= TcpClient.getInstance(getAttachActivity(), ClientMessageDispatcher.getInstance());
        notifyBaseStatusEx = NotifyBaseStatusEx.getInstance();
        notifyEnvInfo = NotifyEnvInfo.getInstance();
        mapFileStatus = MapFileStatus.getInstance();
        if (taskName!=null && !taskName.equals("PathError")){
            String showName=taskName.replaceAll("DDRTask_","");
            showName=showName.replaceAll(".task","");
            tv_now_task.setText(showName);
        }else {
            tv_now_task.setText(R.string.no_task);
        }
        for (int i=0;i<mapFileStatus.getcTaskModes().size();i++){
            groupList.add(mapFileStatus.getcTaskModes().get(i).getName());
        }
        taskCheckAdapter.setNewData(groupList);
        targetPointAdapter.setNewData(targetPoints);
        mapImageView.startThread();
    }
    private long timeSart;
    private long timeStop;
    private boolean isStartOne;
    private BaseDialog normalDialog;
    /**
     * 获取机器人状态信息
     */
    @SuppressLint("SetTextI18n")
    private void initStatusBar() {
        DecimalFormat df = new DecimalFormat("0");
        DecimalFormat format = new DecimalFormat("0.00");
        int h=60;
        int times=notifyBaseStatusEx.getTaskDuration();
        int batteryNum = Integer.parseInt(df.format(notifyEnvInfo.getBatt()));
        mapName = notifyBaseStatusEx.getCurroute();
        //运行次数
        int taskNum = notifyBaseStatusEx.getTaskCount();
        taskName = notifyBaseStatusEx.getCurrpath();
        lsNum=notifyBaseStatusEx.getTemopTaskNum();
        if (taskName!=null && !taskName.equals("PathError") && !taskName.equals("DDRTask_temporary.task")){
            String showName=taskName.replaceAll("DDRTask_","");
            showName=showName.replaceAll(".task","");
            tv_now_task.setText(showName);
        }else {
            tv_now_task.setText(R.string.no_task);
        }
        //工作时间
        int workTimes = Integer.parseInt(df.format(times / h));
        //工作速度
        double taskSpeed = Double.parseDouble(format.format(notifyBaseStatusEx.getPosLinespeed()));
        String showName=mapName.replaceAll("OneRoute_","");
        tv_now_map.setText(showName);
        if (mapName!=null){
            rel_step_description.setVisibility(View.GONE);
            recyclerGoPoint.setVisibility(View.VISIBLE);
            tv_set_go.setText(R.string.go_target_point);
        }else {
            rel_step_description.setVisibility(View.VISIBLE);
            recyclerGoPoint.setVisibility(View.GONE);
            tv_set_go.setText(R.string.create_task_steps);
        }
        tv_now_device.setText(robotID);
        tv_work_time.setText(workTimes +getString(R.string.common_minute));
        tv_task_speed.setText(taskSpeed +" m/s");
       // Logger.d("-------"+notifyBaseStatusEx.geteTaskMode());
        switch (notifyBaseStatusEx.geteTaskMode()){
            case 1:
                tv_task_num.setText(String.valueOf(taskNum)+"/"+lsNum+getString(R.string.common_times));
                if (mapImageView !=null&& isRabPoint){
                    isRabPoint =false;
                    mapImageView.setABPointLine(false);
                }
                break;
            case 2:
                tv_task_num.setText(String.valueOf(taskNum)+"/"+mapFileStatus.AllCount+getString(R.string.common_times));
                if (mapImageView !=null&& isRabPoint){
                    isRabPoint =false;
                    mapImageView.setABPointLine(false);
                }
                break;
            case 3:
                break;
            case 4:
            case 5:
                tv_task_num.setText(" ");
                if (mapImageView !=null&&!isRabPoint){
                    isRabPoint =true;
                    mapImageView.setABPointLine(true);
                }
                break;
        }
        switch (notifyBaseStatusEx.geteSelfCalibStatus()) {
            case 0:
                tv_work_statue.setText(R.string.in_the_calibration);
                //自标定
                break;
            case 1:
                switch (notifyBaseStatusEx.getMode()) {
                    case 1:
                        //Logger.e("待命模式" + modeView.getText());
                        if (taskName.equals("PathError")){
                            tv_work_statue.setText(R.string.common_waiting);
                            tv_now_task.setClickable(true);
                            tv_now_task.setBackgroundResource(R.drawable.bt_bg__map);
                            iv_task_xl.setVisibility(View.VISIBLE);
                        }else {
                            tv_work_statue.setText(R.string.common_runing);
                        }
                        //获取当前时间戳
                        timeStop = System.currentTimeMillis();
                        break;
                    case 2:
                        tv_work_statue.setText(R.string.common_collecting);
                        break;
                    case 3:
                        tv_work_statue.setText(R.string.common_runing);
                        tv_now_task.setClickable(false);
                        tv_now_task.setBackgroundResource(0);
                        iv_task_xl.setVisibility(View.GONE);
                        switch (notifyBaseStatusEx.getSonMode()){
                            case 3:
                                tv_work_statue.setText(R.string.common_error);
                                break;
                            case 15:
                                tv_work_statue.setText(R.string.common_relocationing);
                                break;
                        }
                         timeSart = System.currentTimeMillis();
                        break;
                }
                break;
        }
//        if (timeSart-timeStop<10000 && !isStartOne && 0<timeSart-timeStop){
//            isStartOne=true;
//            toast("第一次进入自动模式"+notifyBaseStatusEx.getSonMode());
//            switch (notifyBaseStatusEx.getSonMode()){
//                case 17:
//                    toast("前方有障碍物");
//                    normalDialog =new NormalDialog.Builder(getAttachActivity())
//                            .setOnListener(new NormalDialog.OnListener() {
//                                @Override
//                                public void onCancel(BaseDialog dialog) {
//                                    normalDialog.dismiss();
//                                }
//                            }).show();
//
//                    break;
//            }
//        }else{
//            isStartOne=false;
//        }
//        long result=timeSart-timeStop;
//        toast("结果"+result+"开始"+timeSart+"待命"+timeStop);
        switch (notifyBaseStatusEx.getChargingType()){
            case 1:
               // btExitCharge.setVisibility(View.VISIBLE);
            case 2:
                btExitCharge.setVisibility(View.VISIBLE);
                break;
        }
        Logger.d("------------是否在充电："+notifyBaseStatusEx.isChargingStatus()+"电量："+ batteryNum);
        if(!notifyBaseStatusEx.isChargingStatus()) {
            if (leftLayout.getVisibility()!=View.VISIBLE){            //如果当前处于非充电模式
                chargingLayout.setVisibility(View.GONE);
                leftLayout.setVisibility(View.VISIBLE);
                if (chargeAnimation.isRunning()){
                    chargeAnimation.stop();                           // 如果动画正在运行 则停止
                }
                Logger.d("-----d-关闭充电界面");
            }
            iv_cd_xs.setImageResource(R.mipmap.sd_def);
        }else {
            if (chargingLayout.getVisibility()!=View.VISIBLE){         // 如果当前处于充电模式，但充电布局不可见
                chargingLayout.setVisibility(View.VISIBLE);
                leftLayout.setVisibility(View.GONE);
            }
            if (!chargeAnimation.isRunning()){
                chargeAnimation.start();
            }
        }

        //只有在自动充电时才生效
        switch (notifyBaseStatusEx.getChargingSubStatus()){
            case 1:
                if (chargingLayout.getVisibility()!=View.VISIBLE){
                    chargingLayout.setVisibility(View.VISIBLE);
                    leftLayout.setVisibility(View.GONE);
                }
                tvChargeNotify.setText(R.string.common_charging_1);
                break;
            case 2:
                tvChargeNotify.setText(R.string.common_charging);
                if (chargingLayout.getVisibility()!=View.VISIBLE){         // 如果当前处于充电模式，但充电布局不可见
                    chargingLayout.setVisibility(View.VISIBLE);
                    leftLayout.setVisibility(View.GONE);
                }
                if (!chargeAnimation.isRunning()){
                    chargeAnimation.start();
                }
                break;
            case 4:
                tvChargeNotify.setText(R.string.common_charging_2);
                if (chargingLayout.getVisibility()!=View.VISIBLE){
                    chargingLayout.setVisibility(View.VISIBLE);
                    leftLayout.setVisibility(View.GONE);
                }
                break;
        }
        tvElectricQuantity.setText(batteryNum +"%");
        circleBarView.setProgress(batteryNum,0,Color.parseColor("#0399FF"));
        Logger.d("当前充电状态："+notifyBaseStatusEx.getChargingSubStatus());
    }

    /**
     * 路径选择弹窗
     * @param view
     */
    private void showTaskPopupWindow(View view) {
        Logger.e("---------showTaskPopupWindow");
        View contentView = null;
        contentView = LayoutInflater.from(getAttachActivity()).inflate(R.layout.recycle_task, null);
        customPopWindow = new CustomPopuWindow.PopupWindowBuilder(getAttachActivity())
                .setView(contentView)
                .enableOutsideTouchableDissmiss(false)
                .setClippingEnable(false)
                .create()
                .showAsDropDown(view, DpOrPxUtils.dip2px(getAttachActivity(), 0), 5);
        RecyclerView recycler_task_check = contentView.findViewById(R.id.recycler_task_check);
        NLinearLayoutManager layoutManager=new NLinearLayoutManager(getAttachActivity());
        recycler_task_check.setLayoutManager(layoutManager);
        recycler_task_check.setAdapter(taskCheckAdapter);
        onItemClick(1);
        customPopWindow.setOutsideTouchListener(()->{
            Logger.e("点击外部已关闭");
            customPopWindow.dissmiss();
            tv_now_task.setBackgroundResource(R.drawable.bt_bg__map);
            iv_task_xl.setImageResource(R.mipmap.xlright_5);
        });
    }


    @OnClick({R.id.iv_shrink,R.id.tv_now_task,R.id.tv_restart_point,R.id.tv_warn,R.id.bt_exit_charge,R.id.circle})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.iv_shrink:
                if (shrinkTailLayout.getVisibility()==View.VISIBLE){
                    shrinkLayout.startAnimation(hideAnimation);
                    hideAnimation.setAnimationListener(this);
                    ivShrink.setImageResource(R.mipmap.iv_shrink);
                }else if (shrinkTailLayout.getVisibility()==View.GONE){
                    shrinkTailLayout.setVisibility(View.VISIBLE);
                    shrinkLayout.startAnimation(showAnimation);
                    ivShrink.setImageResource(R.mipmap.iv_back);
                }
                break;
            case R.id.tv_warn:
                Logger.e("点击");
                if (!notifyBaseStatusEx.getCurroute().equals("")){
                    tcpClient.getMapInfo(ByteString.copyFromUtf8(notifyBaseStatusEx.getCurroute()));
                    toast(R.string.in_load_map);
                }else {
                    toast(R.string.not_map_using);
                }
                break;
            case R.id.tv_now_task:
                if (notifyBaseStatusEx.isLocationed()){
                    showTaskPopupWindow(tv_now_task);
                    tv_now_task.setBackgroundResource(R.drawable.task_check_bg);
                    iv_task_xl.setImageResource(R.mipmap.xl_5);
                }else {
                    showRelocationDialog();
                }
                break;
            case R.id.tv_restart_point:
                float theat= (float) 1.0;
                float x= (float) 1.0;
                float y= (float) 1.0;
                new InputDialog.Builder(getAttachActivity())
                        .setEditVisibility(View.GONE)
                        .setTitle("确认本次送料完成")
                        .setListener(new InputDialog.OnListener() {
                            @Override
                            public void onConfirm(BaseDialog dialog, String content) {
                                goPointLet(x,y,theat,ByteString.copyFromUtf8("one"),ByteString.copyFromUtf8(mapName),2);
//                                tv_restart_point.setVisibility(View.GONE);
                            }

                            @Override
                            public void onCancel(BaseDialog dialog) {
                                toast(R.string.common_cancel);
                            }
                        }).show();

                break;
            case R.id.bt_exit_charge:
                tcpClient.exitModel();
                toast(R.string.exit_charging);
                try {
                    FloatWindow.get().show();
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case R.id.circle:
                tcpClient.getAllLidarMap();
                break;
        }
    }

    /**
     * 机器人暂停/重新运动
     * @param value
     */
    private void pauseOrResume(String value){
        BaseCmd.reqCmdPauseResume reqCmdPauseResume=BaseCmd.reqCmdPauseResume.newBuilder()
                .setError(value)
                .build();
        tcpClient.sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eModuleServer),reqCmdPauseResume);
        Logger.e("机器人暂停/重新运动");
    }



    /**
     * 导航去目标点或者恢复
     * @param x
     * @param y
     * @param theta
     * @param pname
     * @param routeName
     */
    private void goPointLet(float x,float y,float theta,ByteString pname, ByteString routeName,int type){
        DDRVLNMap.eRunSpecificPointType eRunSpecificPointTyp;
        switch (type){
            case 1:
                eRunSpecificPointTyp=DDRVLNMap.eRunSpecificPointType.eRunSpecificPointTypeAdd;
                break;
            case 2:
                eRunSpecificPointTyp=DDRVLNMap.eRunSpecificPointType.eRunSpecificPointTypeResume;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
        DDRVLNMap.space_pointEx space_pointEx=DDRVLNMap.space_pointEx.newBuilder()
                .setX(x)
                .setY(y)
                .setTheta(theta)
                .build();
        DDRVLNMap.targetPtItem targetPtItem=DDRVLNMap.targetPtItem.newBuilder()
                .setPtName(pname)
                .setPtData(space_pointEx)
                .build();
        List<DDRVLNMap.targetPtItem> targetPtItemList=new ArrayList<>();
        targetPtItemList.add(targetPtItem);
        DDRVLNMap.reqRunSpecificPoint reqRunSpecificPoint=DDRVLNMap.reqRunSpecificPoint.newBuilder()
                .setOnerouteName(routeName)
                .addAllTargetPt(targetPtItemList)
                .setBIsDynamicOA(true)
                .setOptType(eRunSpecificPointTyp)
                .build();
        tcpClient.sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eModuleServer),reqRunSpecificPoint);

    }



    private void onItemClick(int type){
        switch (type){
            case 1:
                //任务列表点击事件
                Logger.e("task列表"+groupList.size());
                // Java 8 新特性 Lambda表达式，原来写法即下方注释
                taskCheckAdapter.setOnItemClickListener((adapter, view, position) ->  {
                    new InputDialog.Builder(getAttachActivity())
                            .setTitle(R.string.enter_run_times)
                            .setHint("1")
                            .setConfirm(R.string.common_execute)
                            .setEditNumAndSize(3)
                            .setListener(new InputDialog.OnListener() {
                                @Override
                                public void onConfirm(BaseDialog dialog, String content) {
                                    taskName=groupList.get(position);
                                    mapImageView.setTaskName(taskName);
                                    String showName=taskName.replaceAll("DDRTask_","");
                                    showName=showName.replaceAll(".task","");
                                    tv_now_task.setText(showName);
                                    if (!content.isEmpty() && Integer.parseInt(content)>0 && Integer.parseInt(content)<1000 ){
                                        try {
                                            lsNum=Integer.parseInt(content);
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }
                                    }else {
                                        lsNum=1;
                                    }
                                    tcpClient.addOrDetTemporary(ByteString.copyFromUtf8(mapName),ByteString.copyFromUtf8(taskName),lsNum,2);
                                    isRabPoint =false;
                                    Logger.e("当前临时任务状态"+BaseCmd.eCmdRspType.values().length);
                                }
                                @Override
                                public void onCancel(BaseDialog dialog) {
                                    toast(R.string.cancel_add);
                                }
                            }).show();
                    customPopWindow.dissmiss();
                    tv_now_task.setBackgroundResource(R.drawable.bt_bg__map);
                    iv_task_xl.setImageResource(R.mipmap.xlright_5);
                });
                break;
            case 2:
                //标记点列表点击事件
                    targetPointAdapter.setOnItemClickListener((adapter, view, position) -> {
                        if (notifyBaseStatusEx.isLocationed()){
                            float x=targetPoints.get(position).getX();
                            float y=targetPoints.get(position).getY();
                            float theta=targetPoints.get(position).getTheta();
                            mapImageView.setTargetPoint(targetPoints.get(position));
                            new InputDialog.Builder(getAttachActivity()).setEditVisibility(View.GONE)
                                    .setTitle(getString(R.string.is_go) + targetPoints.get(position).getName())
                                    .setListener(new InputDialog.OnListener() {
                                        @Override
                                        public void onConfirm(BaseDialog dialog, String content) {
                                            sPoint = targetPoints.get(position).getName();
                                            Logger.e("-----需要前往的点："+sPoint);
                                            goPointLet(x, y, theta, ByteString.copyFromUtf8(targetPoints.get(position).getName()), ByteString.copyFromUtf8(mapName), 1);
                                            tv_restart_point.setVisibility(View.VISIBLE);
                                            for (int i = 0; i < targetPoints.size(); i++) {
                                                targetPoints.get(i).setSelected(false);
                                            }
                                            targetPoints.get(position).setSelected(true);
                                            targetPointAdapter.setNewData(targetPoints);
                                            isRabPoint =true;
                                        }
                                        @Override
                                        public void onCancel(BaseDialog dialog) {
                                            toast(R.string.cancel_go_point);
                                        }
                                    })
                                    .show();
                        }else {
                            showRelocationDialog();
                        }
                });
                break;
        }
    }

    /**
     * 选择一种重定位的方式
     */
    private void showRelocationDialog(){
        new InputDialog.Builder(getAttachActivity())
                .setTitle(R.string.current_not_location)
                .setCancel(R.string.hand_location)
                .setConfirm(R.string.auto_location)
                .setEditVisibility(View.GONE)
                .setListener(new InputDialog.OnListener() {
                    @Override
                    public void onConfirm(BaseDialog dialog, String content) {
                        reqCmdRelocation();
                    }

                    @Override
                    public void onCancel(BaseDialog dialog) {
                        tcpClient.exitModel();
                        Intent intent = new Intent(getAttachActivity(), RelocationActivity.class);
                        intent.putExtra("currentBitmap", mapFileStatus.getCurrentBitmapPath());
                        intent.putExtra("currentMapName", mapFileStatus.getCurrentMapName());
                        startActivity(intent);
                    }
                })
                .show();
    }
    /**
     * 发送重定位
     */
    private void reqCmdRelocation(){
        if (notifyBaseStatusEx.geteSelfCalibStatus()==0){
            toast(R.string.can_not_relocation);
        }else if (notifyBaseStatusEx.geteSelfCalibStatus()==1){
            if (notifyBaseStatusEx.getMode() == 3) {
                if (notifyBaseStatusEx.getSonMode() == 15) {
                    toast(R.string.in_relocation);
                }
            } else {
                reqCmdRelocation1();
            }
        }else{
            reqCmdRelocation1();
        }
    }
    private void reqCmdRelocation1(){
        BaseCmd.reqCmdReloc reqCmdRelocation=BaseCmd.reqCmdReloc.newBuilder()
                .setTypeValue(0)
                .build();
        tcpClient.sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eModuleServer),reqCmdRelocation);
    }



    @Override
    public void onLeftClick() {
        switch (notifyBaseStatusEx.geteSelfCalibStatus()) {
            case 0:
                toast(R.string.please_wait_self_calibration);
                //自标定
                break;
            case 1:
                switch (notifyBaseStatusEx.getMode()) {
                    case 1:
                        Logger.e("----mapName:"+mapName+"taskName:"+taskName);
                        if (mapName!=null && taskName!=null && !taskName.equals("PathError")){
                            toast(R.string.please_wait_in);
                            tcpClient.addOrDetTemporary(ByteString.copyFromUtf8(mapName),ByteString.copyFromUtf8(taskName),lsNum,2);
                        }else {
                            toast(R.string.please_create_task);
                        }
                        break;
                    case 3:
                        switch (notifyBaseStatusEx.getSonMode()){
                            case 16:
                                toast(R.string.under_way);
                                break;
                            case 17:
                                toast(R.string.common_start);
                                pauseOrResume("Resume");
                                break;
                        }
                        break;
                }
                break;
        }


    }

    @Override
    public void onCentreClick() {
        switch (notifyBaseStatusEx.geteSelfCalibStatus()) {
            case 0:
                toast(R.string.please_wait_self_calibration);
                //自标定
                break;
            case 1:
                switch (notifyBaseStatusEx.getMode()) {
                    case 1:
                        //Logger.e("待命模式" + modeView.getText());
                        toast(R.string.in_wait_please);
                        break;
                    case 3:
                        switch (notifyBaseStatusEx.getSonMode()){
                            case 16:
                                toast(R.string.common_pause);
                                pauseOrResume("Pause");
                                break;
                            case 17:
                                toast(R.string.common_pause);
                                break;
                        }
                        break;
                }
                break;
        }

    }

    @Override
    public void onRightClick() {
        switch (notifyBaseStatusEx.geteSelfCalibStatus()) {
            case 0:
                toast(R.string.please_wait_self_calibration);
                //自标定
                break;
            case 1:
                switch (notifyBaseStatusEx.getMode()) {
                    case 1:
                        //Logger.e("待命模式" + modeView.getText());
                        toast(R.string.in_wait);
                        break;
                    case 3:
                        try {
                            toast(R.string.please_wait_back);
                            tcpClient.addOrDetTemporary(ByteString.copyFromUtf8(mapName),ByteString.copyFromUtf8(taskName),lsNum,1);
                            for (int i = 0; i < targetPoints.size(); i++) {
                                targetPoints.get(i).setSelected(false);
                            }
                        }catch (NullPointerException e){
                            e.printStackTrace();
                        }
                        break;
                }
                break;
        }

    }


    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        shrinkTailLayout.setVisibility(View.GONE);
        Logger.e("----动画效果结束");
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }


    /**
     * setUserVisibleHint的使用场景:FragmentPagerAdapter+ViewPager
     * 这种方式我们还是比较常见的,譬如,谷歌自带的TabLayout控件,此种场景下,当我们切换fragment的时候,会调用setUserVisibleHint方法,
     * 不会调用onHiddenChanged方法,也不会走fragment的生命周期方法(fragment初始化完成之后,注意这里需要重写viewpager中使用的适配器的方法,让fragment不会被销毁,不然还是会遇到问题)
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser){
            // 相当于onResume()方法--获取焦点
            Logger.e("可见");
            if (notifyBaseStatusEx!=null){
                tcpClient.getMapInfo(ByteString.copyFromUtf8(notifyBaseStatusEx.getCurroute()));
            }
            if (mapImageView !=null&&!mapImageView.drawThread.isAlive()){
                mapImageView.startThread();
                mapImageView.setImageBitmap(notifyBaseStatusEx.getCurroute());
                tvWarn.setVisibility(View.GONE);
            }
            //当服务断开时
            if (tcpClient!=null&&!tcpClient.isConnected()){
                tv_work_statue.setText(R.string.common_Disconnect);
            }
        }else {
            // 相当于onPause()方法---失去焦点
            Logger.e("不可见");
            if (mapImageView !=null&& mapImageView.drawThread.isAlive()){
                mapImageView.onStop();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (statusSwitchButton!=null&&mapImageView!=null)
            statusSwitchButton.onDestroy();
            mapImageView.onStop();
        }catch (NullPointerException e){
            e.printStackTrace();
        }

    }
}
