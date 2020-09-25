package ddr.example.com.nddrandroidclient.ui.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hjq.toast.ToastUtils;
import com.jaygoo.widget.OnRangeChangedListener;
import com.jaygoo.widget.RangeSeekBar;
import com.jaygoo.widget.VerticalRangeSeekBar;
import com.yhao.floatwindow.FloatWindow;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.entity.MessageEvent;
import ddr.example.com.nddrandroidclient.entity.info.NotifyBaseStatusEx;
import ddr.example.com.nddrandroidclient.entity.other.ComputerEditions;
import ddr.example.com.nddrandroidclient.other.Logger;
import ddr.example.com.nddrandroidclient.protocobuf.dispatcher.ClientMessageDispatcher;
import ddr.example.com.nddrandroidclient.socket.TcpClient;
import ddr.example.com.nddrandroidclient.widget.view.CustomPopuWindow;
import ddr.example.com.nddrandroidclient.widget.view.RockerView;

import static ddr.example.com.nddrandroidclient.widget.view.RockerView.DirectionMode.DIRECTION_2_HORIZONTAL;
import static ddr.example.com.nddrandroidclient.widget.view.RockerView.DirectionMode.DIRECTION_2_VERTICAL;

/**
 * desc：控制遥控弹窗
 */
public class ControlPopupWindow {
    private float lineSpeed, palstance;  //线速度 ，角速度
    private double maxSpeed = 0.4;       //设置的最大速度
    private boolean isforward, isGoRight; //左右摇杆当前的方向
    private VerticalRangeSeekBar seekBar;
    private CheckBox fixedSpeed;
    private  RockerView myRocker;
    private RockerView myRockerZy;
    private TextView tvSpeed;
    private TextView tv_xsu;//线速度
    private TextView tv_jsu;//角速度
    private LinearLayout speedLayout;
    private String xsu;
    private String jsu;
    private ImageView iv_quit_yk;
    private CustomPopuWindow customPopuWindow;
    private TcpClient tcpClient;
    private boolean isShowToast;
    private NotifyBaseStatusEx notifyBaseStatusEx;

    private Context context;
    public ControlPopupWindow(Context context) {
        super();
        this.context=context;
        tcpClient=TcpClient.getInstance(context,ClientMessageDispatcher.getInstance());
        if (customPopuWindow!=null){
            customPopuWindow.dissmiss();
        }
        notifyBaseStatusEx=NotifyBaseStatusEx.getInstance();
        EventBus.getDefault().register(this);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void update(MessageEvent messageEvent) {
        switch (messageEvent.getType()) {
            case updateBaseStatus:
                DecimalFormat format = new DecimalFormat("0.00");
                xsu=String.valueOf(format.format(notifyBaseStatusEx.getPosLinespeed()));
                jsu=String.valueOf(format.format(notifyBaseStatusEx.getPosAngulauspeed()));
                if(tv_xsu!=null && tv_jsu!=null){
                    tv_xsu.setText(context.getString(R.string.line_speed)+xsu+" m/s");
                    tv_jsu.setText(context.getString(R.string.angulau_speed)+jsu+context.getString(R.string.angulau_speed_1));
                }
                break;
        }
    }

    /**
     * 遥控弹窗
     */
    public void showControlPopupWindow(View view) {
        View contentView = null;
        contentView = LayoutInflater.from(context).inflate(R.layout.dialog_yk_check, null);
        customPopuWindow = new CustomPopuWindow.PopupWindowBuilder(context)
                .setView(contentView)
                .size(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                .enableOutsideTouchableDissmiss(true)// 设置点击PopupWindow之外的地方，popWindow不关闭，如果不设置这个属性或者为true，则关闭
                .setOutsideTouchable(false)//是否PopupWindow 以外触摸dissmiss
                .create()
                .showAsDropDown(view,0,-10);
        seekBar=contentView.findViewById(R.id.seek_bar_control);
        fixedSpeed=contentView.findViewById(R.id.fixed_speed);
        myRocker=contentView.findViewById(R.id.my_rocker_control);
        myRockerZy=contentView.findViewById(R.id.my_rocker_zy_control);
        tvSpeed=contentView.findViewById(R.id.tv_speed_control);
        iv_quit_yk=contentView.findViewById(R.id.iv_quit_yk);
        tv_xsu=contentView.findViewById(R.id.robot_xsu);//线速度
        tv_jsu=contentView.findViewById(R.id.robot_jsu);//角速度
        speedLayout=contentView.findViewById(R.id.speed_ku);      //调速模块的布局
        initSeekBar();
        initRockerView();
        initTimer();
        setFixedSpeed();
        int robotType= ComputerEditions.getInstance().getRobotType();
        if (robotType==4|robotType==5){
            maxSpeed=0.3;
            speedLayout.setVisibility(View.GONE);
        }else {
            maxSpeed=0.4;
            speedLayout.setVisibility(View.VISIBLE);
        }
        seekBar.setProgress((float) maxSpeed);
        tvSpeed.setText(showSpeed(maxSpeed));
        iv_quit_yk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customPopuWindow.dissmiss();
                timer.cancel();
                task.cancel();
                onDestroy();
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
                Logger.e("------" + seekBar.getLeftSeekBar().getProgress());
                maxSpeed = seekBar.getLeftSeekBar().getProgress();
                if (maxSpeed<=0){
                    isShowToast=true;
                }else {
                    isShowToast=false;

                }
                tvSpeed.setText(showSpeed(maxSpeed));

            }

            @Override
            public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {

            }

            @Override
            public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft) {

            }
        });
    }

    private String showSpeed(double speed){
        DecimalFormat df=new DecimalFormat("0.00");//设置保留位数
        return df.format(speed);
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
                        tcpClient.sendSpeed(lineSpeed, palstance);
                    }
                    if (isShowToast){
                        ToastUtils.show("当前速度为0，无法移动");
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
                Logger.e("-----" + maxSpeed);
                tvSpeed.setText(showSpeed(maxSpeed));
                seekBar.setEnabled(false);

            } else {
                seekBar.setEnabled(true);
                seekBar.setProgress((float) maxSpeed);
                tvSpeed.setText(showSpeed(maxSpeed));
            }
        }));
    }

    public void onDestroy(){
        EventBus.getDefault().unregister(this);
    }
}
