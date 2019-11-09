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
import ddr.example.com.nddrandroidclient.entity.point.TargetPoint;
import ddr.example.com.nddrandroidclient.other.DpOrPxUtils;
import ddr.example.com.nddrandroidclient.other.Logger;
import ddr.example.com.nddrandroidclient.protocobuf.dispatcher.ClientMessageDispatcher;
import ddr.example.com.nddrandroidclient.socket.TcpClient;
import ddr.example.com.nddrandroidclient.ui.activity.HomeActivity;
import ddr.example.com.nddrandroidclient.ui.adapter.RobotIdAdapter;
import ddr.example.com.nddrandroidclient.ui.adapter.TargetPointAdapter;
import ddr.example.com.nddrandroidclient.ui.adapter.TaskCheckAdapter;
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
    @BindView(R.id.tv_new_map)
    TextView tv_new_map;
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
    private int workTimes;
    private float taskSpeed;
    private List<String> groupList=new ArrayList<>();
    private List<TargetPoint> targetPoints= new ArrayList<>();
    private List<DDRVLNMap.task_itemEx> task_itemList;
    private List<DDRVLNMap.targetPtItem> targetPtItems;
    private TargetPoint targetPoint;
    private TargetPointAdapter targetPointAdapter;
    private MapFileStatus mapFileStatus;
    private TaskCheckAdapter taskCheckAdapter;
    private CustomPopuWindow customPopWindow;
    private DpOrPxUtils DpOrPxUtils;
    private RobotIdAdapter robotIdAdapter;
    private RecyclerView  recycler_task_check;

    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void update(MessageEvent messageEvent){
        switch (messageEvent.getType()){
            case updateBaseStatus:
                initStatusBar();
                break;
            case updateDDRVLNMap:
                Logger.e("------地图名："+mapFileStatus.getRspGetDDRVLNMapEx().getData().getBasedata().getName().toStringUtf8());
                if (mapFileStatus.getRspGetDDRVLNMapEx().getData().getBasedata().getName().toStringUtf8().equals(mapName)){
                    mapImageView.setMapBitmap(mapName,taskName);
                    groupList = new ArrayList<>();
                    targetPoints=new ArrayList<>();
                    task_itemList = new ArrayList<>(mapFileStatus.getRspGetDDRVLNMapEx().getData().getTaskSetList());
                    targetPtItems = new ArrayList<>(mapFileStatus.getRspGetDDRVLNMapEx().getData().getTargetPtdata().getTargetPtList());
                    Logger.e("-----" + task_itemList.size());
                    for (int i = 0; i < task_itemList.size(); i++) {
                        groupList.add(task_itemList.get(i).getName().toStringUtf8());
                    }
                    taskCheckAdapter.setNewData(groupList);
                    for (int i=0;i<targetPtItems.size();i++){
                        targetPoint=new TargetPoint();
                        targetPoint.setName(targetPtItems.get(i).getPtName().toStringUtf8());
                        targetPoint.setX(targetPtItems.get(i).getPtData().getX());
                        targetPoint.setY(targetPtItems.get(i).getPtData().getY());
                        targetPoint.setTheta(targetPtItems.get(i).getPtData().getTheta());
                        targetPoints.add(targetPoint);
                    }
                    targetPointAdapter.setNewData(targetPoints);
                    if (robotIdAdapter!=null){
                        robotIdAdapter.setNewData(groupList);
                        robotIdAdapter.notifyDataSetChanged();
                    }
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
        taskCheckAdapter=new TaskCheckAdapter(R.layout.item_recycle_task_check);

        targetPointAdapter=new TargetPointAdapter(R.layout.item_recycle_gopoint);
        @SuppressLint("WrongConstant")
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getAttachActivity(), 4, LinearLayoutManager.VERTICAL, false);
        recycle_gopoint.setLayoutManager(gridLayoutManager);
        recycle_gopoint.setAdapter(targetPointAdapter);
    }

    @Override
    protected void initData() {
        tcpClient= TcpClient.getInstance(getContext(), ClientMessageDispatcher.getInstance());
        notifyBaseStatusEx = NotifyBaseStatusEx.getInstance();
        notifyEnvInfo = NotifyEnvInfo.getInstance();
        mapFileStatus = MapFileStatus.getInstance();
        Logger.e("task列表"+groupList.size());
        targetPointAdapter.setNewData(targetPoints);

        for (int i=0;i<10;i++){
            targetPoint=new TargetPoint();
            targetPoint.setName("呵呵");
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
//        Logger.e("电量"+batteryNum+"---"+df.format(notifyEnvInfo.getBatt()) + "%");
        batteryNum=Integer.parseInt(df.format(notifyEnvInfo.getBatt()));
        circleBarView.setProgress(batteryNum,0,Color.parseColor("#02B5F8"));
        mapName = notifyBaseStatusEx.getCurroute();
        taskName = notifyBaseStatusEx.getCurrpath();
        taskNum=notifyBaseStatusEx.getTaskCount();
        workTimes=notifyBaseStatusEx.getTaskDuration();

        if (mapName!=null && taskName!=null){
            tv_now_task.setText(mapName);
            tv_now_map.setText(taskName);
        }
        tv_now_device.setText(robotID);
        tv_task_num.setText(String.valueOf(taskNum)+"次");
        tv_work_time.setText(String.valueOf(workTimes/3600)+"h");
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
     * 点路径编辑的弹窗
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
        onItemClick();
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

    public void onItemClick(){
        Logger.e("task列表"+groupList.size());
        // Java 8 新特性 Lambda表达式，原来写法即下方注释
        taskCheckAdapter.setOnItemClickListener((adapter,view,position)->{
            tv_now_task.setText(groupList.get(position));
        });

        /*taskCheckAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

            }
        });*/
    }
    @Override
    public void onLeftClick() {
        sendModel(BaseCmd.eCmdActionMode.eAutoDynamic);

    }

    @Override
    public void onCentreClick() {

    }

    @Override
    public void onRightClick() {
        Logger.e("-----退出中");
        exitModel();
    }

    @Override
    public boolean isAutoMode() {
        switch (notifyBaseStatusEx.geteSelfCalibStatus()) {
            case 0:
                //自标定
                return true;
            case 1:
                switch (notifyBaseStatusEx.getMode()) {
                    case 1:
                        //Logger.e("待命模式" + modeView.getText());
                       return false;
                    case 3:
                        //自动模式
                       return true;
                }
                break;
        }
        return true;
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




}
