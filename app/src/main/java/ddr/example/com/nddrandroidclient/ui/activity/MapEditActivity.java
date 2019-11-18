package ddr.example.com.nddrandroidclient.ui.activity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hjq.bar.TitleBar;
import com.jaygoo.widget.OnRangeChangedListener;
import com.jaygoo.widget.RangeSeekBar;
import com.jaygoo.widget.VerticalRangeSeekBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.base.BaseDialog;
import ddr.example.com.nddrandroidclient.common.DDRActivity;
import ddr.example.com.nddrandroidclient.entity.MessageEvent;
import ddr.example.com.nddrandroidclient.entity.info.MapFileStatus;
import ddr.example.com.nddrandroidclient.entity.info.NotifyBaseStatusEx;
import ddr.example.com.nddrandroidclient.entity.point.PathLine;
import ddr.example.com.nddrandroidclient.entity.point.TargetPoint;
import ddr.example.com.nddrandroidclient.helper.ListTool;
import ddr.example.com.nddrandroidclient.other.Logger;
import ddr.example.com.nddrandroidclient.protocobuf.dispatcher.ClientMessageDispatcher;
import ddr.example.com.nddrandroidclient.socket.TcpClient;
import ddr.example.com.nddrandroidclient.ui.adapter.PathAdapter;
import ddr.example.com.nddrandroidclient.ui.adapter.TargetPointAdapter;
import ddr.example.com.nddrandroidclient.ui.dialog.InputDialog;
import ddr.example.com.nddrandroidclient.widget.view.CustomPopuWindow;
import ddr.example.com.nddrandroidclient.widget.view.RockerView;
import ddr.example.com.nddrandroidclient.widget.view.ZoomImageView;

import static ddr.example.com.nddrandroidclient.widget.view.RockerView.DirectionMode.DIRECTION_2_HORIZONTAL;
import static ddr.example.com.nddrandroidclient.widget.view.RockerView.DirectionMode.DIRECTION_2_VERTICAL;

/**
 * time  : 2019/10/29
 * desc  : 地图编辑页面
 * remark：包括 编辑虚拟墙 、添加目标点、添加路径、添加任务、编辑任务等功能
 */
public class MapEditActivity extends DDRActivity {
    @BindView(R.id.title_layout)
    TitleBar titleLayout;
    @BindView(R.id.tv_target_point)
    TextView tvTargetPoint;
    @BindView(R.id.tv_path)
    TextView tvPath;
    @BindView(R.id.tv_025m)
    TextView tv025m;
    @BindView(R.id.tv_05m)
    TextView tv05m;
    @BindView(R.id.tv_1m)
    TextView tv1m;
    @BindView(R.id.tv_2m)
    TextView tv2m;
    @BindView(R.id.speed_layout)
    LinearLayout speedLayout;         //速度调节布局
    @BindView(R.id.zmap)
    ZoomImageView zmap;
    @BindView(R.id.iv_center)
    ImageView ivCenter;
    @BindView(R.id.tv_mark_current)
    TextView tvMarkCurrent;
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
    @BindView(R.id.iv_add_path)
    TextView tvAddPath;
    @BindView(R.id.delete_point)
    TextView tvDeletePoint;
    @BindView(R.id.save_path)
    TextView tvSavePath;

    private Bitmap bitmap;
    private boolean ishaveChecked = false;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private float lineSpeed, palstance;  //线速度 ，角速度
    private double maxSpeed = 0.4;       //设置的最大速度
    private boolean isforward, isGoRight; //左右摇杆当前的方向
    private TcpClient tcpClient;
    private NotifyBaseStatusEx notifyBaseStatusEx;
    private MapFileStatus mapFileStatus;
    private List<TargetPoint> newPoints = new ArrayList<>();
    private List<PathLine> newPaths = new ArrayList<>();
    private List<PathLine.PathPoint> pathPoints=new ArrayList<>();
    private List<TargetPoint> targetPoints;
    private List<PathLine> pathLines;
    private TargetPointAdapter targetPointAdapter;
    private PathAdapter pathAdapter;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_map_edit;
    }


    @Override
    protected void initView() {
        super.initView();
        tcpClient = TcpClient.getInstance(getApplicationContext(), ClientMessageDispatcher.getInstance());
        initSeekBar();
        initRockerView();
        initTimer();
        setFixedSpeed();
        targetPointAdapter = new TargetPointAdapter(R.layout.item_show_recycler);
        pathAdapter = new PathAdapter(R.layout.item_show_recycler);
    }

    @Override
    protected void initData() {
        super.initData();
        notifyBaseStatusEx = NotifyBaseStatusEx.getInstance();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mapFileStatus = MapFileStatus.getInstance();
        editor = sharedPreferences.edit();
        maxSpeed = sharedPreferences.getFloat("speed", (float) 0.4);
        seekBar.setProgress((float) maxSpeed);
        tvSpeed.setText(String.valueOf(maxSpeed));
        try {
            targetPoints = ListTool.deepCopy(mapFileStatus.getTargetPoints());
            pathLines = ListTool.deepCopy(mapFileStatus.getPathLines());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        tvTargetPoint.setText("目标点" + "(" + targetPoints.size() + ")");
        tvPath.setText("路径" + "(" + pathLines.size() + ")");
    }


    @Override
    public boolean statusBarDarkFont() {
        return false;
    }


    @OnClick({R.id.tv_target_point, R.id.tv_path, R.id.tv_025m, R.id.tv_05m, R.id.tv_1m, R.id.tv_2m, R.id.tv_mark_current, R.id.fixed_speed, R.id.add_poi,R.id.iv_add_path
    ,R.id.delete_point,R.id.save_path})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_target_point:
                showPopupWindow(tvTargetPoint, 0);
                break;
            case R.id.tv_path:
                showPopupWindow(tvPath, 1);
                break;
            case R.id.tv_025m:
                break;
            case R.id.tv_05m:
                break;
            case R.id.tv_1m:
                break;
            case R.id.tv_2m:
                break;
            case R.id.tv_mark_current:
                if (speedLayout.getVisibility() == View.VISIBLE) {
                    speedLayout.setVisibility(View.GONE);
                    myRocker.setVisibility(View.GONE);
                    myRockerZy.setVisibility(View.INVISIBLE);
                    tvMarkCurrent.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.nocheckedwg), null);
                } else {
                    speedLayout.setVisibility(View.VISIBLE);
                    myRocker.setVisibility(View.VISIBLE);
                    myRockerZy.setVisibility(View.VISIBLE);
                    tvMarkCurrent.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.checkedwg), null);
                }
                break;
            case R.id.fixed_speed:
                break;
            case R.id.add_poi:
                if (titleLayout.getLeftTitle().toString().equals("新建目标点")) {
                    Logger.e("--------?");
                    new InputDialog.Builder(this).setTitle("添加目标名")
                            .setHint("请输入")
                            .setListener(new InputDialog.OnListener() {
                                @Override
                                public void onConfirm(BaseDialog dialog, String content) {
                                    TargetPoint targetPoint = new TargetPoint(2);
                                    targetPoint.setName(content);
                                    targetPoint.setX(notifyBaseStatusEx.getPosX());
                                    targetPoint.setY(notifyBaseStatusEx.getPosY());
                                    targetPoint.setTheta(1);
                                    newPoints.add(targetPoint);
                                    targetPoints.add(targetPoint);
                                }
                                @Override
                                public void onCancel(BaseDialog dialog) {
                                    toast("取消添加");
                                }
                            })
                            .show();
                }
                break;
            case R.id.iv_add_path:
                PathLine.PathPoint pathPoint=new PathLine().new PathPoint();
                pathPoint.setName("point"+(pathPoints.size()+1));
                pathPoint.setY(zmap.getTargetPoint().getY());
                pathPoint.setX(zmap.getTargetPoint().getX());
                pathPoints.add(pathPoint);
                break;
            case R.id.delete_point:
                if (pathPoints.size()>0){
                    pathPoints.remove(pathPoints.size()-1);
                }else {
                    toast("请先添加点");
                }
                break;
            case R.id.save_path:
                new InputDialog.Builder(this).setTitle("添加路径名")
                        .setHint("请输入")
                        .setListener(new InputDialog.OnListener() {
                            @Override
                            public void onConfirm(BaseDialog dialog, String content) {
                                PathLine pathLine=new PathLine();
                                pathLine.setName(content);
                                List<PathLine.PathPoint> pathPoints1=new ArrayList<>();
                                try {
                                     pathPoints1=ListTool.deepCopy(pathPoints);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (ClassNotFoundException e) {
                                    e.printStackTrace();
                                }
                                pathLine.setPathPoints(pathPoints1);
                                pathLine.setVelocity(0.4f);
                                newPaths.add(pathLine);
                                pathLines.add(pathLine);
                                pathPoints.clear();
                            }
                            @Override
                            public void onCancel(BaseDialog dialog) {
                                pathPoints.clear();
                                toast("取消添加");
                            }
                        })
                        .show();
                break;

        }
    }


    /*************************************显示选择目标点和路径的弹窗*********************************************/
    private CustomPopuWindow customPopuWindow;
    private RecyclerView showRecycler;

    private void showPopupWindow(View view, int type) {
        View contentView = LayoutInflater.from(this).inflate(R.layout.window_point, null);
        customPopuWindow = new CustomPopuWindow.PopupWindowBuilder(this)
                .setView(contentView)
                .create()
                .showAsDropDown(view, 0, 0);
        showRecycler = contentView.findViewById(R.id.show_Recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        showRecycler.setLayoutManager(layoutManager);
        if (type == 0) {
            showRecycler.setAdapter(targetPointAdapter);
            targetPointAdapter.setNewData(targetPoints);
        } else if (type == 1) {
            showRecycler.setAdapter(pathAdapter);
            pathAdapter.setNewData(pathLines);
        }

    }

    /**
     * 弹窗事件点击事件
     */
    private void onShowItemClick() {
        targetPointAdapter.setOnItemClickListener((adapter, view, position) -> {

        });

        pathAdapter.setOnItemClickListener((adapter, view, position) -> {

        });
    }

    /***************************************************end*************************************************************/

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
        timer.schedule(task, 0, 90);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        editor.putFloat("speed", (float) maxSpeed);
        editor.commit();
    }


    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void update(MessageEvent messageEvent) {
        switch (messageEvent.getType()) {
            case addNewPoint:
                Logger.e("新建点");
                titleLayout.setLeftTitle("新建目标点");
                tvMarkCurrent.setVisibility(View.VISIBLE);
                addPoi.setVisibility(View.VISIBLE);
                bitmap = (Bitmap) messageEvent.getData();
                zmap.setImageBitmap(bitmap);
                break;
            case addNewPath:
                Logger.e("新建路径");
                titleLayout.setLeftTitle("新建路径");
                ivCenter.setVisibility(View.VISIBLE);
                tvAddPath.setVisibility(View.VISIBLE);
                tvDeletePoint.setVisibility(View.VISIBLE);
                tvSavePath.setVisibility(View.VISIBLE);
                bitmap = (Bitmap) messageEvent.getData();
                zmap.setImageBitmap(bitmap);
                break;
            case editMap:
                titleLayout.setLeftTitle("编辑地图");
                tvTargetPoint.setText("编辑类型");
                tvTargetPoint.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.virtual_wall_blue),null,null,null);
                ivCenter.setVisibility(View.VISIBLE);
                tvAddPath.setVisibility(View.VISIBLE);
                tvDeletePoint.setVisibility(View.VISIBLE);
                tvSavePath.setVisibility(View.VISIBLE);
                bitmap = (Bitmap) messageEvent.getData();
                zmap.setImageBitmap(bitmap);
                break;
        }
    }


    /**
     * 左上角退出按键
     *
     * @param v
     */
    @Override
    public void onLeftClick(View v) {
        super.onLeftClick(v);
    }

    /**
     * 系统退出按键
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Logger.e("-------退出");
        toPostData();
    }

    /**
     * 给原始页面传递数据
     */
    private void toPostData() {
        if (titleLayout.getLeftTitle().equals("新建目标点")){
            EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.updatePoints, newPoints));
        }else if (titleLayout.getLeftTitle().equals("新建路径")){
            EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.updatePaths,newPaths));
        }
    }

}
