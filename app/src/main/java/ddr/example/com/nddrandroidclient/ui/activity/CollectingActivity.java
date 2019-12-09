package ddr.example.com.nddrandroidclient.ui.activity;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.jaygoo.widget.OnRangeChangedListener;
import com.jaygoo.widget.RangeSeekBar;
import com.jaygoo.widget.VerticalRangeSeekBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

import DDRCommProto.BaseCmd;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.base.BaseDialog;
import ddr.example.com.nddrandroidclient.common.DDRActivity;
import ddr.example.com.nddrandroidclient.entity.MessageEvent;
import ddr.example.com.nddrandroidclient.entity.info.NotifyBaseStatusEx;
import ddr.example.com.nddrandroidclient.other.Logger;
import ddr.example.com.nddrandroidclient.protocobuf.dispatcher.ClientMessageDispatcher;
import ddr.example.com.nddrandroidclient.socket.TcpClient;
import ddr.example.com.nddrandroidclient.ui.dialog.WaitDialog;
import ddr.example.com.nddrandroidclient.widget.view.CollectingView;
import ddr.example.com.nddrandroidclient.widget.view.RockerView;

import static ddr.example.com.nddrandroidclient.widget.view.RockerView.DirectionMode.DIRECTION_2_HORIZONTAL;
import static ddr.example.com.nddrandroidclient.widget.view.RockerView.DirectionMode.DIRECTION_2_VERTICAL;

/**
 * time:  2019/11/5
 * desc:  采集页面
 */
public class CollectingActivity extends DDRActivity {

    @BindView(R.id.collecting)
    CollectingView collecting;
    @BindView(R.id.process_bar)
    NumberProgressBar processBar;
    @BindView(R.id.tv_speed)
    TextView tvSpeed;
    @BindView(R.id.seek_bar)
    VerticalRangeSeekBar seekBar;
    @BindView(R.id.fixed_speed)
    CheckBox fixedSpeed;
    @BindView(R.id.add_poi)
    ImageView addPoi;
    @BindView(R.id.my_rocker)
    RockerView myRocker;
    @BindView(R.id.my_rocker_zy)
    RockerView myRockerZy;

    private float lineSpeed, palstance;  //线速度 ，角速度
    private double maxSpeed = 0.4;       //设置的最大速度
    private boolean isforward, isGoRight; //左右摇杆当前的方向
    private NotifyBaseStatusEx notifyBaseStatusEx;
    private TcpClient tcpClient;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private boolean iscreatingMap = false;       //是否正在生成地图
    private boolean haveCtrated = false;         //是否地图生成完成
    private String collectName;                  //采集的地图名
    private BaseDialog waitDialog;
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void update(MessageEvent mainUpDate) {
        switch (mainUpDate.getType()) {
            case updateBaseStatus:
                Logger.e("-------:" + notifyBaseStatusEx.getSonMode());
                if (notifyBaseStatusEx.geteSelfCalibStatus() == 0) {
                    setTitle("正在自标定中...");
                } else {
                    if (notifyBaseStatusEx.getMode() == 2) {
                        switch (notifyBaseStatusEx.getSonMode()) {
                            case 2:
                                toast("建图异常,即将退出当前模式，本次地图无效");
                                exitModel();
                                finish();
                                break;
                            case 6:
                                setTitle("采集中...");
                                waitDialog.dismiss();
                                myRockerZy.setVisibility(View.VISIBLE);
                                myRocker.setVisibility(View.VISIBLE);
                                addPoi.setVisibility(View.VISIBLE);
                                break;
                            case 7:
                                setTitle("地图生成中...");
                                if (!iscreatingMap) {
                                    setAnimation(processBar, 70, 4000);
                                    iscreatingMap = true;
                                }
                                break;
                            case 8:
                                setTitle("建图完成");
                                if (!haveCtrated) {
                                    tcpClient.reqRunControlEx(collectName);       //切换地图
                                    setAnimation(processBar, 90, 2000);
                                }
                                break;
                        }
                    }

                }
                break;
            case switchMapSucceed:
                if (!haveCtrated) {
                    setAnimation(processBar, 100, 1000);
                    finish();
                }
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
        processBar.setMax(100);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();
        maxSpeed = sharedPreferences.getFloat("speed", (float) 0.4);
        seekBar.setProgress((float) maxSpeed);
        tvSpeed.setText(String.valueOf(maxSpeed));
        initWaitDialog();
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
    }

    @Override
    public void onLeftClick(View v) {
        quitCollect();
        finish();
    }


    @Override
    public void onRightClick(View v) {
        exitModel();
        processBar.setVisibility(View.VISIBLE);
        collecting.unRegister();

    }

    /**
     * 自标定等待弹窗
     */
    public void initWaitDialog(){
        waitDialog= new WaitDialog.Builder(this)
        .setMessage("自标定中...")
        .show();
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

    @OnClick(R.id.add_poi)
    public void onViewClicked() {
        BaseCmd.reqAddPathPointWhileCollecting reqAddPathPointWhileCollecting=BaseCmd.reqAddPathPointWhileCollecting.newBuilder().build();
        tcpClient.sendData(null,reqAddPathPointWhileCollecting);
        EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.addPoiPoint));
        toast("标记成功");

    }

    private boolean ishaveChecked = false;

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
                toast("锁定");
            } else {
                seekBar.setEnabled(true);
                ishaveChecked = isChecked;
                maxSpeed = sharedPreferences.getFloat("speed", (float) 0.4);
                seekBar.setProgress((float) maxSpeed);
                tvSpeed.setText(String.valueOf(maxSpeed));
                toast("取消锁定");

            }
        }));
    }


    /**
     * 设置进度条的 进度和动画效果
     *
     * @param view
     * @param mProgressBar
     */
    private void setAnimation(final NumberProgressBar view, final int mProgressBar, int time) {
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
                    // mTvShake.setText("当前方向：右");
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
                        sendSpeed(lineSpeed, palstance);
                    }
                } else {
                    a = 0;
                    //Logger.e("线速度，角速度：" + lineSpeed + ";" + palstance);
                    sendSpeed(lineSpeed, palstance);
                }

            }
        };
        timer.schedule(task, 0, 90);
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
        BaseCmd.CommonHeader commonHeader = BaseCmd.CommonHeader.newBuilder()
                .setFromCltType(BaseCmd.eCltType.eLocalAndroidClient)
                .setToCltType(BaseCmd.eCltType.eLSMSlamNavigation)
                .addFlowDirection(BaseCmd.CommonHeader.eFlowDir.Forward)
                .build();
        tcpClient.sendData(commonHeader, reqCmdMove);

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
     * 退出采集模式
     */

    private void quitCollect() {
        BaseCmd.reqCmdEndActionMode reqCmdEndActionMode = BaseCmd.reqCmdEndActionMode.newBuilder()
                .setError("noError")
                .setCancelRec(true)
                .build();
        BaseCmd.CommonHeader commonHeader = BaseCmd.CommonHeader.newBuilder()
                .setFromCltType(BaseCmd.eCltType.eLocalAndroidClient)
                .setToCltType(BaseCmd.eCltType.eLSMSlamNavigation)
                .addFlowDirection(BaseCmd.CommonHeader.eFlowDir.Forward)
                .build();
        tcpClient.sendData(commonHeader, reqCmdEndActionMode);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        editor.putFloat("speed", (float) maxSpeed);
        editor.commit();
        tcpClient.requestFile();
    }

    @Override
    public boolean statusBarDarkFont() {
        return false;
    }



}
