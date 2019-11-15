package ddr.example.com.nddrandroidclient.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.protobuf.ByteString;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import DDRCommProto.BaseCmd;
import DDRVLNMapProto.DDRVLNMap;
import butterknife.BindView;
import butterknife.OnClick;
import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.common.DDRLazyFragment;
import ddr.example.com.nddrandroidclient.entity.info.MapFileStatus;
import ddr.example.com.nddrandroidclient.entity.MessageEvent;
import ddr.example.com.nddrandroidclient.entity.info.NotifyBaseStatusEx;
import ddr.example.com.nddrandroidclient.entity.info.NotifyEnvInfo;
import ddr.example.com.nddrandroidclient.entity.point.PathLine;
import ddr.example.com.nddrandroidclient.entity.point.TargetPoint;
import ddr.example.com.nddrandroidclient.entity.point.TaskMode;
import ddr.example.com.nddrandroidclient.other.DpOrPxUtils;
import ddr.example.com.nddrandroidclient.other.Logger;
import ddr.example.com.nddrandroidclient.protocobuf.dispatcher.ClientMessageDispatcher;
import ddr.example.com.nddrandroidclient.socket.TcpClient;
import ddr.example.com.nddrandroidclient.ui.activity.HomeActivity;
import ddr.example.com.nddrandroidclient.ui.adapter.StringAdapter;
import ddr.example.com.nddrandroidclient.ui.adapter.TargetPointAdapter;
import ddr.example.com.nddrandroidclient.widget.view.CircleBarView;
import ddr.example.com.nddrandroidclient.widget.view.CustomPopuWindow;
import ddr.example.com.nddrandroidclient.widget.view.MapImageView;
import ddr.example.com.nddrandroidclient.widget.StatusSwitchButton;

/**
 * time: 2019/10/26
 * desc: 基础状态界面
 */
public final class StatusFragment extends DDRLazyFragment<HomeActivity>implements StatusSwitchButton.OnStatusSwitchListener,Animation.AnimationListener{

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
    @BindView(R.id.iv_map)
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
    RecyclerView recycle_gopoint;
    @BindView(R.id.iv_cd_xs)
    ImageView iv_cd_xs;


    private Animation hideAnimation;  //布局隐藏时的动画
    private Animation showAnimation;  // 布局显示时的动画效果

    private NotifyEnvInfo notifyEnvInfo;
    private NotifyBaseStatusEx notifyBaseStatusEx;

    private static String regEx="[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";//特殊字符
    private TcpClient tcpClient;
    private int batteryNum;
    private String mapName;//地图名
    private String taskName;//任务名
    public static String robotID;//机器人ID
    private String workStatus;
    private int taskNum;
    private double workTimes;
    private double taskSpeed;
    private List<String> groupList=new ArrayList<>();
    private List<TargetPoint> targetPoints= new ArrayList<>();
    private List<DDRVLNMap.task_itemEx> task_itemList;
    private List<DDRVLNMap.targetPtItem> targetPtItems;
    private TargetPoint targetPoint;
    private TargetPointAdapter targetPointAdapter;
    private MapFileStatus mapFileStatus;
    private StringAdapter taskCheckAdapter;
    private CustomPopuWindow customPopWindow;
    private DpOrPxUtils DpOrPxUtils;
    private StringAdapter robotIdAdapter;
    private RecyclerView  recycler_task_check;

    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void update(MessageEvent messageEvent){
        switch (messageEvent.getType()){
            case updateBaseStatus:
                initStatusBar();
                break;
            case updateDDRVLNMap:
                Logger.e("------地图名："+mapFileStatus.getMapName()+"当前"+mapName);
                if (mapFileStatus.getMapName().equals(mapName)){
                    Logger.e("group列数"+groupList.size()+"列数1"+mapFileStatus.getTaskModes().size()+" -- "+mapFileStatus.getcTaskModes().size());
                    mapImageView.setMapBitmap(mapName,taskName);
                    mapImageView.setMapBitmap(mapName,taskName);
                    groupList = new ArrayList<>();
                    targetPoints=new ArrayList<>();
                    for (int i=0;i<mapFileStatus.getcTaskModes().size();i++){
                        groupList.add(mapFileStatus.getcTaskModes().get(i).getName());
                        Logger.e("group列数"+groupList.size());
                    }
                    taskCheckAdapter.setNewData(groupList);
                    targetPoints=mapFileStatus.getTargetPoints();
                    targetPointAdapter.setNewData(targetPoints);
                    if (robotIdAdapter!=null){
                        robotIdAdapter.setNewData(groupList);
                        robotIdAdapter.notifyDataSetChanged();
                    }
                }
                break;
            case switchTaskSuccess:
                tcpClient.getMapInfo(ByteString.copyFromUtf8(mapName));
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
        taskCheckAdapter=new StringAdapter(R.layout.item_recycle_task_check);
        targetPointAdapter=new TargetPointAdapter(R.layout.item_recycle_gopoint);
        @SuppressLint("WrongConstant")
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getAttachActivity(), 4, LinearLayoutManager.VERTICAL, false);
        recycle_gopoint.setLayoutManager(gridLayoutManager);
        recycle_gopoint.setAdapter(targetPointAdapter);
        onItemClick(2);
    }

    @Override
    protected void initData() {
        tcpClient= TcpClient.getInstance(getContext(), ClientMessageDispatcher.getInstance());
        notifyBaseStatusEx = NotifyBaseStatusEx.getInstance();
        notifyEnvInfo = NotifyEnvInfo.getInstance();
        mapFileStatus = MapFileStatus.getInstance();
        for (int i=0;i<mapFileStatus.getcTaskModes().size();i++){
            groupList.add(mapFileStatus.getcTaskModes().get(i).getName());
            Logger.e("group列数"+groupList.size());
        }
        Logger.e("task列表"+groupList.size());
        taskCheckAdapter.setNewData(groupList);
        targetPointAdapter.setNewData(targetPoints);
        for (int i=0;i<10;i++){
            targetPoint=new TargetPoint();
            targetPoint.setName("呵呵"+i);
            targetPoint.setX(100);
            targetPoint.setY(100);
            targetPoint.setTheta(10);
            targetPoints.add(targetPoint);
        }
    }

    /**
     * 获取机器人状态信息
     */
    private void initStatusBar() {
        DecimalFormat df = new DecimalFormat("0");
        DecimalFormat format = new DecimalFormat("0.00");
        int h=60;
        int times=notifyBaseStatusEx.getTaskDuration();
//        Logger.e("电量"+batteryNum+"---"+df.format(notifyEnvInfo.getBatt()) + "%");
        batteryNum=Integer.parseInt(df.format(notifyEnvInfo.getBatt()));
        circleBarView.setProgress(batteryNum,0,Color.parseColor("#02B5F8"));
        mapName = notifyBaseStatusEx.getCurroute();
        taskName = notifyBaseStatusEx.getCurrpath();
        taskNum=notifyBaseStatusEx.getTaskCount();
        workTimes=Double.parseDouble(df.format((float) times/h));
        taskSpeed=Double.parseDouble(format.format(notifyBaseStatusEx.getPosLinespeed()));
//        Logger.e("次数"+taskNum+"时间"+workTimes+"速度"+taskSpeed);
        if (mapName!=null && taskName!=null){
            tv_now_task.setText(taskName);
            tv_now_map.setText(mapName);
        }
        tv_now_device.setText(robotID);
        tv_task_num.setText(String.valueOf(taskNum)+" 次");
        tv_work_time.setText(String.valueOf(workTimes)+" 分");
        tv_task_speed.setText(String.valueOf(taskSpeed)+" m/s");
        //Logger.e("模式"+notifyBaseStatusEx.getMode());
        //Logger.e("模式"+notifyBaseStatusEx.geteSelfCalibStatus());
        switch (notifyBaseStatusEx.geteSelfCalibStatus()) {
            case 0:
                //自标定
                break;
            case 1:
                switch (notifyBaseStatusEx.getMode()) {
                    case 1:
                        //Logger.e("待命模式" + modeView.getText());
                        tv_work_statue.setText("待命中");
                        rel_step_description.setVisibility(View.VISIBLE);
                        recycle_gopoint.setVisibility(View.GONE);
                        break;
                    case 3:
                        tv_work_statue.setText("运动中");
                        rel_step_description.setVisibility(View.GONE);
                        recycle_gopoint.setVisibility(View.VISIBLE);
                        break;
                }
                break;
        }
        if(notifyBaseStatusEx.isChargingStatus()) {
            iv_cd_xs.setImageResource(R.mipmap.sd_check);
        }else {
            iv_cd_xs.setImageResource(R.mipmap.sd_def);
        }
    }

    /**
     * 将机器id赋值
     */
    public static void setRobotID(String robotid, Context context){
        String stringd="DDR";
        if(!Pattern.compile(regEx).matcher(robotid).find()&&!robotid.contains("-")){
            robotID =robotid;
        }else {
            Toast.makeText(context,"机器目前为默认ID，请修改机器ID",Toast.LENGTH_SHORT).show();
            robotID=stringd+"00";
        }
    }
    /**
     * 路径选择弹窗
     * @param view
     */
    private void showTaskPopupWindow(View view) {
        Logger.e("---------showTaskPopupWindow");
        View contentView = null;
        contentView = getAttachActivity().getLayoutInflater().from(getAttachActivity()).inflate(R.layout.recycle_task, null);
        customPopWindow = new CustomPopuWindow.PopupWindowBuilder(getAttachActivity())
                .setView(contentView)
                .create()
                .showAsDropDown(view, DpOrPxUtils.dip2px(getAttachActivity(), 0), 5);
        recycler_task_check =contentView.findViewById(R.id.recycler_task_check);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getAttachActivity());
        recycler_task_check.setLayoutManager(layoutManager);
        recycler_task_check.setAdapter(taskCheckAdapter);
        onItemClick(1);
    }
    @OnClick({R.id.iv_shrink,R.id.tv_now_task})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.iv_shrink:
                if (shrinkTailLayout.getVisibility()==View.VISIBLE){
                    shrinkLayout.startAnimation(hideAnimation);
                    hideAnimation.setAnimationListener(this);
                }else if (shrinkTailLayout.getVisibility()==View.GONE){
                    shrinkTailLayout.setVisibility(View.VISIBLE);
                    shrinkLayout.startAnimation(showAnimation);
                }
                break;
            case R.id.tv_now_task:
                showTaskPopupWindow(tv_now_task);
                break;
        }
    }
    /**
     * 运行当前地图
     * @param eCmdActionMode
     */
    private void sendModel(BaseCmd.eCmdActionMode eCmdActionMode) {
        BaseCmd.reqCmdStartActionMode reqCmdStartActionMode = BaseCmd.reqCmdStartActionMode.newBuilder()
                .setMode(eCmdActionMode)
                .build();
        BaseCmd.CommonHeader commonHeader = BaseCmd.CommonHeader.newBuilder()
                .setFromCltType(BaseCmd.eCltType.eLocalAndroidClient)
                .setToCltType(BaseCmd.eCltType.eLSMSlamNavigation)
                .addFlowDirection(BaseCmd.CommonHeader.eFlowDir.Forward)
                .build();
        tcpClient.sendData(commonHeader, reqCmdStartActionMode);
    }

    /**
     * 退出当前模式
     */
    private void exitModel() {
        BaseCmd.reqCmdEndActionMode reqCmdEndActionMode = BaseCmd.reqCmdEndActionMode.newBuilder()
                .setError("noError")
                .build();
        BaseCmd.CommonHeader commonHeader = BaseCmd.CommonHeader.newBuilder()
                .setFromCltType(BaseCmd.eCltType.eLocalAndroidClient)
                .setToCltType(BaseCmd.eCltType.eLSMSlamNavigation)
                .addFlowDirection(BaseCmd.CommonHeader.eFlowDir.Forward)
                .build();
        tcpClient.sendData(commonHeader, reqCmdEndActionMode);
    }
    /**
     * 机器人暂停/重新运动
     * @param value
     */
    private void pauseOrResume(String value){
        BaseCmd.reqCmdPauseResume reqCmdPauseResume=BaseCmd.reqCmdPauseResume.newBuilder()
                .setError(value)
                .build();
        BaseCmd.CommonHeader commonHeader=BaseCmd.CommonHeader.newBuilder()
                .setFromCltType(BaseCmd.eCltType.eLocalAndroidClient)
                .setToCltType(BaseCmd.eCltType.eLSMSlamNavigation)
                .addFlowDirection(BaseCmd.CommonHeader.eFlowDir.Forward)
                .build();
        tcpClient.sendData(commonHeader,reqCmdPauseResume);
        Logger.e("机器人暂停/重新运动");
    }


    public void onItemClick(int type){
        switch (type){
            case 1:
                //任务列表点击事件
                Logger.e("task列表"+groupList.size());
                // Java 8 新特性 Lambda表达式，原来写法即下方注释
                taskCheckAdapter.setOnItemClickListener((adapter, view, position) ->  {
                    tv_now_task.setText(groupList.get(position));
                });
                break;
            case 2:
                //标记点列表点击事件
                targetPointAdapter.setOnItemClickListener((adapter, view, position) -> {
                });
                break;
        }

    }
    @Override
    public void onLeftClick() {
        switch (notifyBaseStatusEx.geteSelfCalibStatus()) {
            case 0:
                toast("请稍等，正在自标定");
                //自标定
                break;
            case 1:
                switch (notifyBaseStatusEx.getMode()) {
                    case 1:
                        //Logger.e("待命模式" + modeView.getText());
                        toast("请稍等，正在进入");
                        sendModel(BaseCmd.eCmdActionMode.eAutoDynamic);
                        break;
                    case 3:
                        switch (notifyBaseStatusEx.getSonMode()){
                            case 16:
                                toast("正在执行中");
                                break;
                            case 17:
                                toast("开始");
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
                toast("请稍等，正在自标定");
                //自标定
                break;
            case 1:
                switch (notifyBaseStatusEx.getMode()) {
                    case 1:
                        //Logger.e("待命模式" + modeView.getText());
                        toast("正在待命，请先进入执行状态");
                        break;
                    case 3:
                        switch (notifyBaseStatusEx.getSonMode()){
                            case 16:
                                toast("暂停");
                                pauseOrResume("stop");
                                break;
                            case 17:
                                toast("暂停状态中");
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
                toast("请稍等，正在自标定");
                //自标定
                break;
            case 1:
                switch (notifyBaseStatusEx.getMode()) {
                    case 1:
                        //Logger.e("待命模式" + modeView.getText());
                        toast("正在待命中");
                        break;
                    case 3:
                        toast("请稍等，正在退出");
                        exitModel();
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            statusSwitchButton.onDestroy();
        }catch (NullPointerException e){
            e.printStackTrace();
        }

    }




}
