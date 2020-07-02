package ddr.example.com.nddrandroidclient.ui.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.protobuf.ByteString;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import DDRCommProto.BaseCmd;
import DDRModuleProto.DDRModuleCmd;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;
import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.base.BaseDialog;
import ddr.example.com.nddrandroidclient.common.DDRActivity;
import ddr.example.com.nddrandroidclient.entity.MessageEvent;
import ddr.example.com.nddrandroidclient.entity.info.MapFileStatus;
import ddr.example.com.nddrandroidclient.entity.info.NotifyBaseStatusEx;
import ddr.example.com.nddrandroidclient.entity.point.PathLine;
import ddr.example.com.nddrandroidclient.entity.point.TargetPoint;
import ddr.example.com.nddrandroidclient.entity.point.XyEntity;
import ddr.example.com.nddrandroidclient.other.Logger;
import ddr.example.com.nddrandroidclient.protocobuf.CmdSchedule;
import ddr.example.com.nddrandroidclient.protocobuf.dispatcher.ClientMessageDispatcher;
import ddr.example.com.nddrandroidclient.socket.TcpClient;
import ddr.example.com.nddrandroidclient.ui.adapter.NLinearLayoutManager;
import ddr.example.com.nddrandroidclient.ui.adapter.PathAdapter;
import ddr.example.com.nddrandroidclient.ui.adapter.TargetPointAdapter;
import ddr.example.com.nddrandroidclient.ui.dialog.ControlPopupWindow;
import ddr.example.com.nddrandroidclient.ui.dialog.WaitDialog;
import ddr.example.com.nddrandroidclient.widget.textview.GridTextView;
import ddr.example.com.nddrandroidclient.widget.view.CustomPopuWindow;
import ddr.example.com.nddrandroidclient.widget.zoomview.RobotLocationView;

/**
 * time : 2019/12/25
 * desc : 手动定位
 */
public class RelocationActivity extends DDRActivity {
    @BindView(R.id.robot_location)
    RobotLocationView robotLocationView;         //当前机器人的位置
    @BindView(R.id.map_layout)
    RelativeLayout mapLayout;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_reference)
    TextView tvReference;
    private Bitmap currentBitmap;
    private List<TargetPoint> targetPoints=new ArrayList<>();
    private List<PathLine> pathLines=new ArrayList<>();

    private TcpClient tcpClient;
    private NotifyBaseStatusEx notifyBaseStatusEx;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_relocation;
    }

    @Override
    protected void initView() {
        super.initView();
        notifyBaseStatusEx=NotifyBaseStatusEx.getInstance();
        tcpClient=TcpClient.getInstance(context,ClientMessageDispatcher.getInstance());
    }

    @Override
    protected void initData() {
        super.initData();
        String bitmap=getIntent().getStringExtra("currentBitmap");
        String mapName=getIntent().getStringExtra("currentMapName");
        Logger.e("-------bitmap:"+bitmap);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(bitmap);
            currentBitmap= BitmapFactory.decodeStream(fis);
            robotLocationView.setImageBitmap(currentBitmap);
            tcpClient.getMapInfo(ByteString.copyFromUtf8(mapName));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        realTimeRequest();
        //reqObstacleInfo();
        robotLocationView.startThread();
        targetPoints= MapFileStatus.getInstance().getcTargetPoints();
        pathLines=MapFileStatus.getInstance().getcPathLines();
    }

    /**
     * 请求当前障碍物信息
     */
    private void reqObstacleInfo(){
        DDRModuleCmd.reqObstacleInfo reqObstacleInfo=DDRModuleCmd.reqObstacleInfo.newBuilder().build();
        if (tcpClient!=null){
            tcpClient.sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eModuleServer),reqObstacleInfo);
        }
    }


    @OnClick({R.id.tv_finish,R.id.iv_back,R.id.tv_look,R.id.tv_reference})
    public void onViewClicked(View view){
        switch (view.getId()){
            case R.id.tv_finish:
                XyEntity xyEntity=robotLocationView.getRobotLocationInWindow();
                XyEntity xyEntity1=robotLocationView.toWorld(xyEntity.getX(),xyEntity.getY());
                float rotation=robotLocationView.getRadians();
                reqCmdReloc(xyEntity1.getX(),xyEntity1.getY(),rotation);
                break;
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.tv_look:
                XyEntity original=robotLocationView.getRobotLocationInWindow();
                XyEntity worldXY=robotLocationView.toWorld(original.getX(),original.getY());
                float rotation1=robotLocationView.getRadians();
                toast("X:"+worldXY.getX()+",Y:"+worldXY.getY()+",弧度："+rotation1);
                break;
            case R.id.tv_reference:
                showPopupWindowReference(tvReference);
                break;
        }
    }


    /**
     * 发送重定位
     * @param x
     * @param y
     * @param rotation
     */
    private void reqCmdReloc(float x,float y,float rotation){
        BaseCmd.reqCmdReloc reqCmdReloc=BaseCmd.reqCmdReloc.newBuilder()
                .setTypeValue(2)
                .setPosX0(x)
                .setPosY0(y)
                .setPosTh0(rotation)
                .build();
        tcpClient.sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eModuleServer),reqCmdReloc);
    }

    private boolean isRunning=true;

    /**
     * 实时请求雷达数据
     */
    private void realTimeRequest(){
        new Thread(()->{
            while (isRunning){
                reqObstacleInfo();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isRunning=false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        isRunning=false;
        robotLocationView.onStop();
    }

    private BaseDialog waitDialog;
    private int relocationStatus;      //重定位结果
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void update(MessageEvent messageEvent){
        switch (messageEvent.getType()){
            case updateRelocationStatus:
                relocationStatus= (int) messageEvent.getData();
                switch (relocationStatus){
                    case 0:
                        toast(R.string.relocation_failed);
                        if (waitDialog!=null&&waitDialog.isShowing()){
                            waitDialog.dismiss();
                        }
                        break;
                    case 1:
                        toast(R.string.relocation_succeed);
                        if (waitDialog!=null&&waitDialog.isShowing()){
                            waitDialog.dismiss();
                        }
                        robotLocationView.onStop();
                        finish();
                        break;
                    case 2:
                        waitDialog=new WaitDialog.Builder(this)
                                .setMessage(R.string.the_relocation)
                                .show();
                        break;
                }
                break;
            case touchFloatWindow:
                new ControlPopupWindow(this).showControlPopupWindow(findViewById(R.id.iv_back));
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
    /********************************** start- 显示参考层弹窗******************************/
    private CustomPopuWindow customPopuWindow;
    private TextView tvGrid,tvTargetPointRe,tvAllPoint,tvPathRe,tvAllPath;
    private LinearLayout layoutGrid,layoutTarget,layoutPath;
    private GridTextView tv025m,tv05m,tv1m,tv2m;
    private RecyclerView recyclerPoints,recyclerPaths;
    private TargetPointAdapter targetReferenceAdapter;
    private PathAdapter pathReferenceAdapter;
    private boolean allShowPoint,allShowPath;               // 是否全部显示
    private int gridStatus;                                // 0 默认 1:0.25m ,2: 0.5m, 3: 1m ,4: 2m
    private void showPopupWindowReference(View view){
        View contentView=getLayoutInflater().from(context).inflate(R.layout.popupwindow_reference,null);
        customPopuWindow=new CustomPopuWindow.PopupWindowBuilder(context)
                .setView(contentView)
                .create()
                .showAsDropDown(view,0,5);
        tvGrid=contentView.findViewById(R.id.tv_grid);
        tvTargetPointRe=contentView.findViewById(R.id.tv_target_point);
        tvPathRe=contentView.findViewById(R.id.tv_path);
        layoutGrid=contentView.findViewById(R.id.layout_grid);
        layoutTarget=contentView.findViewById(R.id.layout_target);
        layoutPath=contentView.findViewById(R.id.layout_path);
        tvAllPoint=contentView.findViewById(R.id.tv_all_point);
        tvAllPath=contentView.findViewById(R.id.tv_all_path);
        tv025m=contentView.findViewById(R.id.tv_025m);
        tv05m=contentView.findViewById(R.id.tv_05m);
        tv1m=contentView.findViewById(R.id.tv_1m);
        tv2m=contentView.findViewById(R.id.tv_2m);
        recyclerPoints=contentView.findViewById(R.id.recycler_target_point);
        recyclerPaths=contentView.findViewById(R.id.recycler_paths);
        handleRecycler();
        handleLogic();

    }

    /**
     * 处理弹出显示内容的点击事件
     */
    private void handleLogic(){
        View.OnClickListener listener=v -> {
            switch (v.getId()){
                case R.id.tv_grid:
                    if (layoutGrid.getVisibility()==View.VISIBLE){
                        layoutGrid.setVisibility(View.GONE);
                    }else {
                        layoutGrid.setVisibility(View.VISIBLE);
                    }
                    layoutTarget.setVisibility(View.GONE);
                    layoutPath.setVisibility(View.GONE);
                    break;
                case R.id.tv_025m:
                    if (!tv025m.getSelected()) {
                        robotLocationView.setPrecision((float) 0.25);        //将图片网格化
                        setIconDefault1();
                        tv025m.setTextColor(Color.parseColor("#FFFFFFFF"));
                        tv025m.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.item_show), null);
                        tv025m.setSelected(true);
                        gridStatus=1;
                    } else {
                        robotLocationView.setPrecision(0);        //取消网格
                        setIconDefault1();
                    }
                    break;
                case R.id.tv_05m:
                    if (!tv05m.getSelected()) {
                        robotLocationView.setPrecision((float) 0.5);        //将图片网格化
                        setIconDefault1();
                        tv05m.setSelected(true);
                        tv05m.setTextColor(Color.parseColor("#FFFFFFFF"));
                        tv05m.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.mipmap.item_show),null);
                        gridStatus=2;
                    } else {
                        setIconDefault1();
                        robotLocationView.setPrecision(0);        //取消网格
                    }
                    break;
                case R.id.tv_1m:
                    if (!tv1m.getSelected()) {
                        robotLocationView.setPrecision((float) 1);        //将图片网格化
                        setIconDefault1();
                        tv1m.setSelected(true);
                        tv1m.setTextColor(Color.parseColor("#FFFFFFFF"));
                        tv1m.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.mipmap.item_show),null);
                        gridStatus=3;
                    } else {
                        setIconDefault1();
                        robotLocationView.setPrecision(0);        //取消网格
                    }
                    break;
                case R.id.tv_2m:
                    if (!tv2m.getSelected()) {
                        robotLocationView.setPrecision((float) 2);        //将图片网格化

                        setIconDefault1();
                        tv2m.setSelected(true);
                        tv2m.setTextColor(Color.parseColor("#FFFFFFFF"));
                        tv2m.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.mipmap.item_show),null);
                        gridStatus=4;
                    } else {
                        setIconDefault1();
                        robotLocationView.setPrecision(0);        //取消网格
                    }
                    break;
                case R.id.tv_target_point:
                    if (layoutTarget.getVisibility()==View.VISIBLE){
                        layoutTarget.setVisibility(View.GONE);
                    }else {
                        layoutTarget.setVisibility(View.VISIBLE);
                    }
                    layoutGrid.setVisibility(View.GONE);
                    layoutPath.setVisibility(View.GONE);
                    break;
                case R.id.tv_all_point:
                    if (allShowPoint){
                        allShowPoint=false;
                        tvAllPoint.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.mipmap.item_hide),null);
                        tvAllPoint.setTextColor(getResources().getColor(R.color.text_gray));
                        for (TargetPoint targetPoint:targetPoints){
                            targetPoint.setMultiple(false);
                        }
                    }else {
                        allShowPoint=true;
                        tvAllPoint.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.mipmap.item_show),null);
                        tvAllPoint.setTextColor(getResources().getColor(R.color.white));
                        for (TargetPoint targetPoint:targetPoints){
                            targetPoint.setMultiple(true);
                        }
                    }
                    robotLocationView.setTargetPoints(targetPoints);
                    targetReferenceAdapter.setNewData(targetPoints);
                    break;
                case R.id.tv_path:
                    if (layoutPath.getVisibility()==View.VISIBLE){
                        layoutPath.setVisibility(View.GONE);
                    }else {
                        layoutPath.setVisibility(View.VISIBLE);
                    }
                    layoutGrid.setVisibility(View.GONE);
                    layoutTarget.setVisibility(View.GONE);
                    break;
                case R.id.tv_all_path:
                    if (allShowPath){
                        allShowPath=false;
                        tvAllPath.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.mipmap.item_hide),null);
                        tvAllPath.setTextColor(getResources().getColor(R.color.text_gray));
                        for (PathLine pathLine:pathLines){
                            pathLine.setMultiple(false);
                        }
                    }else {
                        allShowPath=true;
                        tvAllPath.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.mipmap.item_show),null);
                        tvAllPath.setTextColor(getResources().getColor(R.color.white));
                        for (PathLine pathLine:pathLines){
                            pathLine.setMultiple(true);
                        }
                    }
                    robotLocationView.setPathLines(pathLines);
                    pathReferenceAdapter.setNewData(pathLines);
                    break;
            }
        };
        tvGrid.setOnClickListener(listener);
        tv025m.setOnClickListener(listener);
        tv05m.setOnClickListener(listener);
        tv1m.setOnClickListener(listener);
        tv2m.setOnClickListener(listener);
        tvTargetPointRe.setOnClickListener(listener);
        tvPathRe.setOnClickListener(listener);
        tvAllPoint.setOnClickListener(listener);
        tvAllPath.setOnClickListener(listener);
        if (allShowPoint){
            tvAllPoint.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.mipmap.item_show),null);
            tvAllPoint.setTextColor(getResources().getColor(R.color.white));
        }else {
            tvAllPoint.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.mipmap.item_hide),null);
            tvAllPoint.setTextColor(getResources().getColor(R.color.text_gray));
        }
        if (allShowPath){
            tvAllPath.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.mipmap.item_show),null);
            tvAllPath.setTextColor(getResources().getColor(R.color.white));
        }else {
            tvAllPath.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.mipmap.item_hide),null);
            tvAllPath.setTextColor(getResources().getColor(R.color.text_gray));
        }
        switch (gridStatus){
            case 0:
                setIconDefault1();
                break;
            case 1:
                tv025m.setTextColor(Color.parseColor("#FFFFFFFF"));
                tv025m.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.item_show), null);
                tv025m.setSelected(true);
                break;
            case 2:
                tv05m.setSelected(true);
                tv05m.setTextColor(Color.parseColor("#FFFFFFFF"));
                tv05m.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.mipmap.item_show),null);
                break;
            case 3:
                tv1m.setSelected(true);
                tv1m.setTextColor(Color.parseColor("#FFFFFFFF"));
                tv1m.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.mipmap.item_show),null);
                break;
            case 4:
                tv2m.setSelected(true);
                tv2m.setTextColor(Color.parseColor("#FFFFFFFF"));
                tv2m.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.mipmap.item_show),null);
                break;
        }
    }

    /**
     * 处理RecyclerView和点击事件
     */
    private void handleRecycler(){
        targetReferenceAdapter=new TargetPointAdapter(R.layout.item_show_recycler);
        NLinearLayoutManager layoutManager = new NLinearLayoutManager(context);
        recyclerPoints.setLayoutManager(layoutManager);
        recyclerPoints.setAdapter(targetReferenceAdapter);
        pathReferenceAdapter=new PathAdapter(R.layout.item_show_recycler);
        NLinearLayoutManager layoutManager1 = new NLinearLayoutManager(context);
        recyclerPaths.setLayoutManager(layoutManager1);
        recyclerPaths.setAdapter(pathReferenceAdapter);
        targetReferenceAdapter.setNewData(targetPoints);
        pathReferenceAdapter.setNewData(pathLines);
        targetReferenceAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (targetPoints.get(position).isMultiple()){
                targetPoints.get(position).setMultiple(false);
                robotLocationView.setTargetPoints(targetPoints);
                targetReferenceAdapter.setNewData(targetPoints);
            }else {
                targetPoints.get(position).setMultiple(true);
                robotLocationView.setTargetPoints(targetPoints);
                targetReferenceAdapter.setNewData(targetPoints);
            }
        });

        pathReferenceAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (pathLines.get(position).isMultiple()){
                pathLines.get(position).setMultiple(false);
                robotLocationView.setPathLines(pathLines);
                pathReferenceAdapter.setNewData(pathLines);
            }else {
                pathLines.get(position).setMultiple(true);
                robotLocationView.setPathLines(pathLines);
                pathReferenceAdapter.setNewData(pathLines);
            }
        });
    }
    /**
     * 设置网格图标默认状态
     */
    private void setIconDefault1(){
        tv025m.setSelected(false);
        tv05m.setSelected(false);
        tv1m.setSelected(false);
        tv2m.setSelected(false);
        tv025m.setTextColor(Color.parseColor("#66ffffff"));
        tv05m.setTextColor(Color.parseColor("#66ffffff"));
        tv1m.setTextColor(Color.parseColor("#66ffffff"));
        tv2m.setTextColor(Color.parseColor("#66ffffff"));
        tv025m.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.item_hide), null);
        tv1m.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.item_hide), null);
        tv05m.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.item_hide), null);
        tv2m.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.item_hide), null);
        gridStatus=0;
    }

    /**********************************end -********************************************/


}

