package ddr.example.com.nddrandroidclient.ui.activity;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.protobuf.ByteString;
import com.jaygoo.widget.OnRangeChangedListener;
import com.jaygoo.widget.RangeSeekBar;
import com.jaygoo.widget.VerticalRangeSeekBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import DDRCommProto.BaseCmd;
import butterknife.BindView;
import butterknife.OnClick;
import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.base.BaseDialog;
import ddr.example.com.nddrandroidclient.common.DDRActivity;
import ddr.example.com.nddrandroidclient.entity.MessageEvent;
import ddr.example.com.nddrandroidclient.entity.info.NotifyBaseStatusEx;
import ddr.example.com.nddrandroidclient.entity.info.NotifyLidarPtsEntity;
import ddr.example.com.nddrandroidclient.entity.point.XyEntity;
import ddr.example.com.nddrandroidclient.helper.ActivityStackManager;
import ddr.example.com.nddrandroidclient.other.Logger;
import ddr.example.com.nddrandroidclient.protocobuf.CmdSchedule;
import ddr.example.com.nddrandroidclient.protocobuf.dispatcher.ClientMessageDispatcher;
import ddr.example.com.nddrandroidclient.socket.TcpClient;
import ddr.example.com.nddrandroidclient.ui.dialog.InputDialog;
import ddr.example.com.nddrandroidclient.ui.dialog.WaitDialog;
import ddr.example.com.nddrandroidclient.widget.view.CollectingView3;
import ddr.example.com.nddrandroidclient.widget.view.CollectingView4;
import ddr.example.com.nddrandroidclient.widget.view.RockerView;

import static ddr.example.com.nddrandroidclient.widget.view.RockerView.DirectionMode.DIRECTION_2_HORIZONTAL;
import static ddr.example.com.nddrandroidclient.widget.view.RockerView.DirectionMode.DIRECTION_2_VERTICAL;

/**
 * time:  2019/11/5
 * desc:  采集页面
 * modify time: 2020/3/23
 */
public class CollectingActivity extends DDRActivity {
 /*   @BindView(R.id.collect4)
    CollectingView4 collectingView4;
    @BindView(R.id.collect3)
    CollectingView3 collectingView3;*/
    @BindView(R.id.process_bar)
    ProgressBar processBar;
    @BindView(R.id.layout_progress)
    RelativeLayout layoutProgress;
    @BindView(R.id.tv_progress)
    TextView tvProgress;
    @BindView(R.id.tv_speed)
    TextView tvSpeed;
    @BindView(R.id.seek_bar)
    VerticalRangeSeekBar seekBar;
    @BindView(R.id.fixed_speed)
    CheckBox fixedSpeed;
    @BindView(R.id.add_poi)
    TextView addPoi;
    @BindView(R.id.my_rocker)
    RockerView myRocker;
    @BindView(R.id.my_rocker_zy)
    RockerView myRockerZy;
    @BindView(R.id.tv_detection)
    TextView tvDetection;                   //回环检测
    @BindView(R.id.tv_stop_move)
    TextView tvStopMove;                    //急停
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_save_map)
    TextView tvSaveMap;
    @BindView(R.id.layout_collect)
    LinearLayout layoutCollect;
    @BindView(R.id.tv_cs)
    TextView tvCs;


    private float lineSpeed, palstance;  //线速度 ，角速度
    private double maxSpeed = 0.4;       //设置的最大速度
    private boolean isforward, isGoRight; //左右摇杆当前的方向
    private NotifyBaseStatusEx notifyBaseStatusEx;
    private TcpClient tcpClient;
    private String collectName;                  //采集的地图名
    private BaseDialog waitDialog,waitDialog1;
    private NotifyLidarPtsEntity notifyLidarPtsEntity;
    private NotifyLidarPtsEntity notifyLidarPtsEntity1;
    private List<NotifyLidarPtsEntity> ptsEntityList=new ArrayList<>();  //存储雷达扫到的点云
    private List<XyEntity>poiPoints=new ArrayList<>();                   //兴趣点列表 采集中生成
    private float posX,posY;        //机器人当前位置
    private float radian;           // 机器人当前朝向 单位弧度
    private float angle;            // 机器人当前朝向 弧度转换后的角度
    private float minX=0,minY=0,maxX=0,maxY=0;  //雷达扫到的最大坐标和最小坐标
    private float ratio=1;         //地图比例
    private int measureWidth=1000, measureHeight=1000;
    private boolean isStartDraw=false;        //是否开始绘制
    private int clicks;                       //点击次数
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void update(MessageEvent mainUpDate) {
        switch (mainUpDate.getType()) {
            case updateBaseStatus:
                //Logger.e("-------:" + notifyBaseStatusEx.getSonMode());
                initStatusBar();
                break;
            case notifyMapGenerateProgress:
                float progress= (float) mainUpDate.getData();
                setAnimation(processBar,(int) (progress*100),0);
                tvProgress.setText((int) (progress*100)+"%");
                if (progress==1.0f){
                    tvTitle.setText(R.string.conmon_collect_success);
                    postDelayed(()->{
                        finish();
                    },1000);
                }
                break;
            case updateDetectionLoopStatus:
                int loopStatus= (int) mainUpDate.getData();
                switch (loopStatus){
                    case -2:                    // 检测错误
                        toast(getString(R.string.loop_status_one));
                        break;
                    case -1:                   // 没有检测到回环
                        toast(getString(R.string.loop_status_two));
                        break;
                    case 0:                   // 回环已存在
                        toast(getString(R.string.loop_status_three));
                        break;
                    case 1:                  // 新采集基准构成回环
                        toast(getString(R.string.loop_status_four));
                        break;
                    case 2:                 //  距离太近不需要检测回环
                        toast(getString(R.string.loop_status_five));
                        break;
                    case 3:
                        toast(getString(R.string.loop_status_six));
                        break;
                }
                break;
            case notifyTCPDisconnected:
                netWorkStatusDialog();
                break;
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_collecting;
    }

    @Override
    protected void initView() {
        super.initView();
        tcpClient = TcpClient.getInstance(context, ClientMessageDispatcher.getInstance());
        initSeekBar();
        initRockerView();
        initTimer();
        setFixedSpeed();
    }

    @Override
    protected void initData() {
        super.initData();
        collectName=getIntent().getStringExtra("CollectName");
        Logger.e("-----采集的地图名");
        notifyBaseStatusEx = NotifyBaseStatusEx.getInstance();
        notifyLidarPtsEntity=NotifyLidarPtsEntity.getInstance();
        processBar.setMax(100);
        seekBar.setProgress((float) maxSpeed);
        tvSpeed.setText(String.valueOf(maxSpeed));
        initWaitDialog();

    }

    private void initStatusBar() {
        if (notifyBaseStatusEx.geteSelfCalibStatus() == 0) {
            tvTitle.setText(R.string.collect_title_two);
        } else {
            if (notifyBaseStatusEx.getMode() == 2) {
                switch (notifyBaseStatusEx.getSonMode()) {
                    case 2:
                        toast(R.string.create_map_error);
                        exitModel();
                        finish();
                        break;
                    case 6:
                        waitDialog.dismiss();
                        tvTitle.setText(R.string.collect_title_three);
                        myRockerZy.setVisibility(View.VISIBLE);
                        myRocker.setVisibility(View.VISIBLE);
                        addPoi.setVisibility(View.VISIBLE);
                        break;
                }
            }
        }
        if (notifyBaseStatusEx.getStopStat()==4|notifyBaseStatusEx.getStopStat()==12){
            tvStopMove.setVisibility(View.VISIBLE);
            tvStopMove.setCompoundDrawablesWithIntrinsicBounds(null,getResources().getDrawable(R.mipmap.jt_nodef),null,null);
        }else {
            tvStopMove.setVisibility(View.GONE);
        }
    }


    /**
     * 自标定等待弹窗
     */
    public void initWaitDialog(){
        waitDialog= new WaitDialog.Builder(this)
        .setMessage(R.string.waiting_dialog)
        .show();
    }


    @SuppressLint("NewApi")
    private void initSeekBar() {
        seekBar.setOnRangeChangedListener(new OnRangeChangedListener() {
            @Override
            public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {
                if (!ishaveChecked) {
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

    @OnClick({R.id.add_poi,R.id.tv_detection,R.id.tv_save_map,R.id.iv_back,R.id.tv_hide})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.iv_back:
                new InputDialog.Builder(getActivity())
                        .setTitle(R.string.exit_collecting_dialog)
                        .setEditVisibility(View.GONE)
                        .setListener(new InputDialog.OnListener() {
                            @Override
                            public void onConfirm(BaseDialog dialog, String content) {
                                quitCollect();
                                //stopDraw();
                                finish();
                            }
                            @Override
                            public void onCancel(BaseDialog dialog) {
                            }
                        }).show();

                break;
            case R.id.tv_save_map:
                new InputDialog.Builder(getActivity())
                        .setTitle(R.string.save_map_dialog)
                        .setEditVisibility(View.GONE)
                        .setListener(new InputDialog.OnListener() {
                            @Override
                            public void onConfirm(BaseDialog dialog, String content) {
                                exitModel();
                                layoutProgress.setVisibility(View.VISIBLE);
                                //stopDraw();
                            }
                            @Override
                            public void onCancel(BaseDialog dialog) {
                            }
                        }).show();
                break;
            case R.id.add_poi:
                BaseCmd.reqAddPathPointWhileCollecting reqAddPathPointWhileCollecting=BaseCmd.reqAddPathPointWhileCollecting.newBuilder().build();
                tcpClient.sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eModuleServer),reqAddPathPointWhileCollecting);
                EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.addPoiPoint));
                poiPoints.add(new XyEntity(posX,posY));
                toast(R.string.gauge_succeed);
                break;
            case R.id.tv_detection:
                BaseCmd.reqDetectLoop reqDetectLoop=BaseCmd.reqDetectLoop.newBuilder()
                        .build();
                tcpClient.sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eModuleServer),reqDetectLoop);
                break;
            case R.id.tv_hide:
                if (layoutCollect.getVisibility()==View.GONE){
                    clicks++;
                    if (clicks>=6){
                        layoutCollect.setVisibility(View.VISIBLE);
                        clicks=0;
                    }
                }else {
                    clicks++;
                    if (clicks>=6){
                        layoutCollect.setVisibility(View.GONE);
                        clicks=0;
                    }
                }
                break;
        }


    }

    private boolean ishaveChecked = false;

    /**
     * 固定速度
     */
    public void setFixedSpeed() {
        fixedSpeed.setOnCheckedChangeListener(((buttonView, isChecked) -> {
            if (isChecked) {
                ishaveChecked = isChecked;
                Logger.e("-----" + maxSpeed);
                tvSpeed.setText(String.valueOf(maxSpeed));
                seekBar.setEnabled(false);
                toast(R.string.common_lock);
            } else {
                seekBar.setEnabled(true);
                ishaveChecked = isChecked;
                seekBar.setProgress((float) maxSpeed);
                tvSpeed.setText(String.valueOf(maxSpeed));
                toast(R.string.common_cancel_lock);

            }
        }));
    }


    /**
     * 设置进度条的 进度和动画效果
     * @param view
     *
     * @param mProgressBar
     */
    private void setAnimation(final ProgressBar view, final int mProgressBar, int time) {
        ValueAnimator animator = ValueAnimator.ofInt(0, mProgressBar).setDuration(time);

        animator.addUpdateListener((valueAnimator) -> {
            view.setProgress((int) valueAnimator.getAnimatedValue());
        });
        animator.start();
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
                try {
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
                }catch (NullPointerException e){
                    e.printStackTrace();
                    Logger.e("-----------NullPointerException");
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
                try {
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
                        // mTvShake.setText("当前方向：右");
                        //Logger.e("右");
                        isGoRight = true;
                        myRockerZy.setmAreaBackground(R.mipmap.rocker_go_right);
                    }
                }catch (NullPointerException e){
                    e.printStackTrace();
                    Logger.e("---------NullPointerException");
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
     * 定时器，每50毫秒执行一次
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
                        sendSpeed(lineSpeed, palstance);
                    }
                } else {
                    a = 0;
                    //Logger.e("线速度，角速度：" + lineSpeed + ";" + palstance);
                    sendSpeed(lineSpeed, palstance);
                }

            }
        };
        timer.schedule(task, 0, 50);
    }


    /**
     * 发送线速度，角速度
     *
     * @param lineSpeed
     * @param palstance
     */
    private void sendSpeed(final float lineSpeed, final float palstance) {
        BaseCmd.reqCmdMove reqCmdMove = BaseCmd.reqCmdMove.newBuilder()
                .setLineSpeed(lineSpeed)
                .setAngulauSpeed(palstance)
                .build();
        tcpClient.sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eModuleServer), reqCmdMove);

    }


    /**
     * 退出当前模式
     */
    private void exitModel() {
        BaseCmd.reqCmdEndActionMode reqCmdEndActionMode = BaseCmd.reqCmdEndActionMode.newBuilder()
                .setError("noError")
                .build();
        tcpClient.sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eModuleServer), reqCmdEndActionMode);
    }

    /**
     * 退出采集模式
     */

    private void quitCollect() {
        BaseCmd.reqCmdEndActionMode reqCmdEndActionMode = BaseCmd.reqCmdEndActionMode.newBuilder()
                .setError("noError")
                .setCancelRec(true)
                .build();
        tcpClient.sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eModuleServer), reqCmdEndActionMode);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
        task.cancel();
        tcpClient.requestFile();
        tcpClient.getMapInfo(ByteString.copyFromUtf8(notifyBaseStatusEx.getCurroute()));
       /* if (collectingView3!=null){
            if (collectingView3.isRunning){
                Logger.e("非正常退出采集模式");
                stopDraw();
            }
        }*/

    }
    @Override
    protected void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Logger.e("Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, mLoaderCallback);
        } else {
            Logger.e("OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
        if (tcpClient!=null&&!tcpClient.isConnected()){
            Logger.e("网络无法连接!");
            //netWorkStatusDialog();
        }
    }

    //OpenCV库加载并初始化成功后的回调函数
    private BaseLoaderCallback mLoaderCallback=new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status){
                case LoaderCallbackInterface.SUCCESS:
                    Logger.e("OpenCVLoader加载成功");
                    break;
                default:
                    break;
            }
            super.onManagerConnected(status);
        }
    };

    /**
     * 显示网络连接弹窗
     */
    private void  netWorkStatusDialog(){
        waitDialog1=new WaitDialog.Builder(this).setMessage(R.string.common_network_connecting).show();
        postDelayed(()->{
            if (waitDialog1.isShowing()){
                toast(R.string.network_not_connect);
                ActivityStackManager.getInstance().finishAllActivities();
                startActivity(LoginActivity.class);
            }
        },6000);
    }


    @Override
    public void onBackPressed() {

    }

    @Override
    public boolean statusBarDarkFont() {
        return false;
    }
   /* private int totalSize=0;
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void upDateDrawMap(MessageEvent mainUpDate){
        switch (mainUpDate.getType()){
            case receivePointCloud:
                if (NotifyBaseStatusEx.getInstance().getSonMode()==6){
                    if (measureHeight==0){
                        measureWidth=collectingView4.getWidth();
                        measureHeight=collectingView4.getHeight();
                        Logger.e("------绘图控件大小"+measureWidth+";"+measureHeight);
                    }
                    //long startTime=System.currentTimeMillis();
                    posX=notifyLidarPtsEntity.getPosX();
                    posY=notifyLidarPtsEntity.getPosY();
                    radian=notifyLidarPtsEntity.getPosdirection();
                    totalSize=totalSize+notifyLidarPtsEntity.getPositionList().size();
                    Logger.e("------接收到点数总和："+totalSize);
                    //tvCs.setText("x:"+posX+"y:"+posY+"radian:"+radian);
                    angle=radianToangle(radian);
                    notifyLidarPtsEntity1=new NotifyLidarPtsEntity();
                    notifyLidarPtsEntity1.setPosX(notifyLidarPtsEntity.getPosX());
                    notifyLidarPtsEntity1.setPosY(notifyLidarPtsEntity.getPosY());
                    notifyLidarPtsEntity1.setPositionList(notifyLidarPtsEntity.getPositionList());
                    ptsEntityList.add(notifyLidarPtsEntity1);
                    if (!isStartDraw){
                        collectingView4.startThread();
                        collectingView3.startThread();
                    }
                    collectingView4.setData(ptsEntityList,poiPoints);
                    collectingView3.setData(ptsEntityList,angle);
                    isStartDraw=true;
                }
                break;
        }
    }*/

    /**
     * 弧度转角度
     */
    private float radianToangle(float angle){
        return (float)(180/Math.PI*angle);
    }


   /* *//**
     * 停止绘制
     *//*
    public void stopDraw(){
        collectingView4.onStop();
        collectingView3.onStop();
    }
*/
}
