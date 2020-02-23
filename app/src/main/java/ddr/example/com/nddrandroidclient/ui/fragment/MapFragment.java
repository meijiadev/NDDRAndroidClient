package ddr.example.com.nddrandroidclient.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.protobuf.ByteString;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import DDRCommProto.BaseCmd;
import DDRVLNMapProto.DDRVLNMap;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.OnClick;
import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.base.BaseDialog;
import ddr.example.com.nddrandroidclient.common.DDRLazyFragment;
import ddr.example.com.nddrandroidclient.download.FileUtil;
import ddr.example.com.nddrandroidclient.entity.MessageEvent;
import ddr.example.com.nddrandroidclient.entity.info.MapFileStatus;
import ddr.example.com.nddrandroidclient.entity.info.MapInfo;
import ddr.example.com.nddrandroidclient.entity.info.NotifyBaseStatusEx;
import ddr.example.com.nddrandroidclient.entity.point.BaseMode;
import ddr.example.com.nddrandroidclient.entity.point.PathLine;
import ddr.example.com.nddrandroidclient.entity.point.TargetPoint;
import ddr.example.com.nddrandroidclient.entity.point.TaskMode;
import ddr.example.com.nddrandroidclient.helper.ListTool;
import ddr.example.com.nddrandroidclient.other.DpOrPxUtils;
import ddr.example.com.nddrandroidclient.other.Logger;
import ddr.example.com.nddrandroidclient.protocobuf.CmdSchedule;
import ddr.example.com.nddrandroidclient.protocobuf.dispatcher.ClientMessageDispatcher;
import ddr.example.com.nddrandroidclient.socket.TcpClient;
import ddr.example.com.nddrandroidclient.ui.activity.CollectingActivity;
import ddr.example.com.nddrandroidclient.ui.activity.HomeActivity;
import ddr.example.com.nddrandroidclient.ui.activity.MapEditActivity;
import ddr.example.com.nddrandroidclient.ui.activity.MapSettingActivity;
import ddr.example.com.nddrandroidclient.ui.activity.RelocationActivity;
import ddr.example.com.nddrandroidclient.ui.adapter.ActionAdapter;
import ddr.example.com.nddrandroidclient.ui.adapter.BaseModeAdapter;
import ddr.example.com.nddrandroidclient.ui.adapter.MapAdapter;
import ddr.example.com.nddrandroidclient.ui.adapter.PathAdapter;
import ddr.example.com.nddrandroidclient.ui.adapter.StringAdapter;
import ddr.example.com.nddrandroidclient.ui.adapter.TargetPointAdapter;
import ddr.example.com.nddrandroidclient.ui.adapter.TaskAdapter;
import ddr.example.com.nddrandroidclient.ui.dialog.InputDialog;
import ddr.example.com.nddrandroidclient.ui.dialog.MenuDialog;
import ddr.example.com.nddrandroidclient.ui.dialog.SelectDialog;
import ddr.example.com.nddrandroidclient.ui.dialog.WaitDialog;
import ddr.example.com.nddrandroidclient.widget.edit.DDREditText;
import ddr.example.com.nddrandroidclient.widget.edit.MyEditTextChangeListener;
import ddr.example.com.nddrandroidclient.widget.edit.RegexEditText;
import ddr.example.com.nddrandroidclient.widget.textview.DDRTextView;
import ddr.example.com.nddrandroidclient.widget.textview.GridTextView;
import ddr.example.com.nddrandroidclient.widget.view.CustomPopuWindow;
import ddr.example.com.nddrandroidclient.widget.view.GridLayerView;
import ddr.example.com.nddrandroidclient.widget.view.LineView;
import ddr.example.com.nddrandroidclient.widget.view.PointView;
import ddr.example.com.nddrandroidclient.widget.view.ZoomImageView;

/**
 * time: 2019/10/26
 * desc: 地图管理界面
 */
public class MapFragment extends DDRLazyFragment<HomeActivity> {
    @BindView(R.id.bt_create_map)
    TextView btCreatMap;
    @BindView(R.id.bt_batch_delete)
    TextView btBatch;   //批量管理
    @BindView(R.id.tv_delete_all)
    TextView tvDeleteAll; //批量删除
    @BindView(R.id.recycler_map)
    RecyclerView mapRecycler;
    @BindView(R.id.map_layout)
    RelativeLayout mapLayout;
    @BindView(R.id.map_detail_layout)
    RelativeLayout mapDetailLayout;       //展示单个地图详情页面
    @BindView(R.id.left_detail_layout)
    RelativeLayout leftDetailLayout;       // 左侧布局 包括列表和列表展开项
    @BindView(R.id.detail_layout)
    RelativeLayout detailLayout;
    @BindView(R.id.iv_back)
    ImageView iv_back;
    @BindView(R.id.tv_target_point)
    TextView tvTargetPoint;
    @BindView(R.id.tv_path)
    TextView tvPath;
    @BindView(R.id.tv_task)
    TextView tvTask;
    @BindView(R.id.tv_edit_map)
    TextView tvEditMap;
    @BindView(R.id.recycler_detail)
    RecyclerView recyclerDetail;
    @BindView(R.id.tv_add_new)
    TextView tvAddNew;
    @BindView(R.id.tv_delete)
    TextView tvDelete;
    @BindView(R.id.zoom_map)
    ZoomImageView zoomMap;
    @BindView(R.id.tv_map_name)
    TextView tvMapName;
    @BindView(R.id.tv_map_size)
    TextView tvMapSize;
    @BindView(R.id.tv_create_time)
    TextView tvCreateTime;
    @BindView(R.id.tv_025m)
    GridTextView tv025m;
    @BindView(R.id.tv_05m)
    GridTextView tv05m;
    @BindView(R.id.tv_1m)
    GridTextView tv1m;
    @BindView(R.id.tv_2m)
    GridTextView tv2m;
    /**目标点的子项查看和再编辑布局*/
    @BindView(R.id.point_detail_layout)
    RelativeLayout pointDetailLayout;
    @BindView(R.id.et_point_name)
    RegexEditText etPointName;
    @BindView(R.id.et_x)
    DDREditText etX;
    @BindView(R.id.et_y)
    DDREditText etY;
    @BindView(R.id.et_toward)
    DDREditText etToward;
    @BindView(R.id.layout_edit)
    RelativeLayout layoutEdit;
    @BindView(R.id.revamp_point)
    TextView revampPoint;             //修改充点电
    /**path路径的子项查看和再编辑*/
    @BindView(R.id.path_detail_layout)
    RelativeLayout pathDetailLayout;
    @BindView(R.id.et_path_name)
    RegexEditText etPathName;
    @BindView(R.id.spinner_mode)
    DDRTextView tv_Spinner;        // 模式的下拉框触发
    @BindView(R.id.tv_cs)
    TextView tv_cs;
    @BindView(R.id.et_parameter)
    DDREditText etParameter;
    @BindView(R.id.tv_cm)
    TextView tv_cm;
    @BindView(R.id.et_speed)
    DDREditText etSpeed;
    @BindView(R.id.action_recycler)
    RecyclerView actionRecycler;
    @BindView(R.id.tv_config)
    TextView tvConfig;
    /*** task的子项查看和再编辑布局*/
    @BindView(R.id.task_detail_layout)
    RelativeLayout taskDetailLayout;
    @BindView(R.id.layout_select)
    RelativeLayout layoutSelect;             //选择布局
    @BindView(R.id.et_task_name)
    EditText etTaskName;
    @BindView(R.id.bt_next)
    Button btNext;
    @BindView(R.id.tv_target_spread)
    TextView tvTargetSpread;       // 目标点 点击展开
    @BindView(R.id.tv_path_spread)
    TextView tvPathSpread;        //  路径点击展开
    @BindView(R.id.select_point_Recycler)
    RecyclerView selectPointRecycler;
    @BindView(R.id.select_path_Recycler)
    RecyclerView selectPathRecycler;

    @BindView(R.id.sort_layout)         //排序布局
    RelativeLayout layoutSort;
    @BindView(R.id.sort_Recycler)
    RecyclerView sortRecycler;
    @BindView(R.id.bt_back)
    Button btBack;
    @BindView(R.id.bt_save)
    Button btSave;

    @BindView(R.id.right_map_layout)
    RelativeLayout rightMapLayout;
    @BindView(R.id.save_point)
    TextView savePoint;
    @BindView(R.id.save_path)
    TextView savePath;



    private List<TargetPoint> targetPoints = new ArrayList<>();                    // 解析后的目标点列表
    private List<PathLine> pathLines = new ArrayList<>();                         //解析后的路径列表
    private List<TaskMode> taskModes=new ArrayList<>();                           //解析后的任务列表
    private MapFileStatus mapFileStatus;
    private MapAdapter mapAdapter;        //地图列表适配器
    private List<MapInfo> mapInfos = new ArrayList<>(); //地图列表
    private List<String> downloadMapNames = new ArrayList<>();
    private TcpClient tcpClient;
    private boolean isShowSelected;   //显示批量管理的按钮

    private TargetPointAdapter targetPointAdapter,selectPointAdapter;            //目标点列表适配器 ;用于选择的目标点列表
    private PathAdapter pathAdapter,selectPathAdapter;                         //路径列表适配器 ;用于选择的路径列表
    private ActionAdapter actionAdapter;                     // 动作点列表
    private TaskAdapter taskAdapter;                         //任务Recycler的适配器
    private BaseModeAdapter sortAdapter;                     //排序的列表适配器
    private String mapName;                                  //点击查看的地图名




    private int mPosition = 0;                                   //当前显示的是哪个子项数据 （目标点列表、路径列表、任务列表）


    public static MapFragment newInstance() {
        return new MapFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_map;
    }

    @Override
    protected void initView() {
        mapLayout.setVisibility(View.VISIBLE);
        mapDetailLayout.setVisibility(View.GONE);
        mapAdapter = new MapAdapter(R.layout.item_map_recycler, getAttachActivity());
        @SuppressLint("WrongConstant")
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getAttachActivity(), 4, LinearLayoutManager.VERTICAL, false);
        mapRecycler.setLayoutManager(gridLayoutManager);
        mapRecycler.setAdapter(mapAdapter);
        //目标点(路径或任务)的列表初始化
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getAttachActivity());
        recyclerDetail.setLayoutManager(linearLayoutManager);
        targetPointAdapter = new TargetPointAdapter(R.layout.item_target_point);
        pathAdapter = new PathAdapter(R.layout.item_target_point);
        actionAdapter = new ActionAdapter(R.layout.item_path_action);
        taskAdapter = new TaskAdapter(R.layout.item_target_point);
        recyclerDetail.setAdapter(targetPointAdapter);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getAttachActivity());
        actionRecycler.setLayoutManager(linearLayoutManager1);
        actionRecycler.setAdapter(actionAdapter);     //给动作Recycler设置适配器
        selectPointAdapter=new TargetPointAdapter(R.layout.item_task_select);
        selectPathAdapter=new PathAdapter(R.layout.item_task_select);
        LinearLayoutManager linearLayoutManager2=new LinearLayoutManager(getAttachActivity());
        selectPointRecycler.setLayoutManager(linearLayoutManager2);
        selectPointRecycler.setAdapter(selectPointAdapter);
        LinearLayoutManager linearLayoutManager3=new LinearLayoutManager(getAttachActivity());
        selectPathRecycler.setLayoutManager(linearLayoutManager3);
        selectPathRecycler.setAdapter(selectPathAdapter);
        LinearLayoutManager linearLayoutManager4=new LinearLayoutManager(getAttachActivity());
        sortRecycler.setLayoutManager(linearLayoutManager4);
        sortAdapter=new BaseModeAdapter(R.layout.item_task_sort);
        sortRecycler.setAdapter(sortAdapter);       //给排序的列表设置适配器
    }

    @Override
    protected void initData() {
        tcpClient = TcpClient.getInstance(getAttachActivity(), ClientMessageDispatcher.getInstance());
        mapFileStatus = MapFileStatus.getInstance();
        downloadMapNames = mapFileStatus.getMapNames();
        checkFilesAllName(downloadMapNames);
        transformMapInfo(mapFileStatus.getMapInfos());
        mapAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_LEFT);
        mapAdapter.setNewData(mapInfos);
        onItemClick();
        onTargetItemClick();
        onPathItemClick();
        onActionItemClick();
        onTaskItemClick();
        onSelectedItemClick();
        onSortItemClick();
        /*************************路径设置***************************/
        map=tv_Spinner.getMap();
        actionList=new ArrayList<>();
        for (int i=1;i<9;i++){
            actionList.add(map.get(i));
        }
        modeList=new ArrayList<>();
        modeList.add(map.get(64));
        modeList.add(map.get(65));
        /*modeList.add(map.get(66));*/
        stringAdapter=new StringAdapter(R.layout.item_path_mode,modeList);
        onModeItemClick();
        /*************************************************************/
        etToward.setViewType(1);
        etSpeed.setEt_content(3);

    }

    @SuppressLint("ResourceAsColor")
    @OnClick({R.id.bt_create_map, R.id.iv_back, R.id.tv_target_point, R.id.tv_add_new, R.id.tv_delete,R.id.bt_batch_delete,R.id.tv_delete_all, R.id.save_point,R.id.revamp_point, R.id.tv_path,
            R.id.spinner_mode,R.id.bt_add_action, R.id.save_path, R.id.tv_task,R.id.tv_target_spread,R.id.tv_path_spread,R.id.bt_next,R.id.bt_back,R.id.bt_save,R.id.tv_edit_map,R.id.tv_025m,R.id.tv_05m,R.id.tv_1m,R.id.tv_2m})
    public void onViewClicked(View view)  {
        switch (view.getId()) {
            case R.id.bt_create_map:
                if (NotifyBaseStatusEx.getInstance().getMode()==2){
                    Intent intent=new Intent(getAttachActivity(),CollectingActivity.class);
                    startActivity(intent);
                }else {
                    new InputDialog.Builder(getAttachActivity())
                            .setTitle("采集地图")
                            .setHint("输入地图名称")
                            .setListener(new InputDialog.OnListener() {
                                @Override
                                public void onConfirm(BaseDialog dialog, String content) {
                                    if (!content.isEmpty()){
                                        content=content.replaceAll(" ","");
                                        String name="OneRoute_"+content;
                                        if (!mapFileStatus.getMapNames().contains(name)){
                                            BaseCmd.reqCmdStartActionMode reqCmdStartActionMode=BaseCmd.reqCmdStartActionMode.newBuilder()
                                                    .setMode(BaseCmd.eCmdActionMode.eRec)
                                                    .setRouteName(ByteString.copyFromUtf8(name))
                                                    .build();
                                            tcpClient.sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eLSMSlamNavigation),reqCmdStartActionMode);
                                            Intent intent=new Intent(getAttachActivity(),CollectingActivity.class);
                                            intent.putExtra("CollectName",name);
                                            startActivity(intent);
                                        }else {
                                            toast("名字重复，请重新输入");
                                        }
                                    }else {
                                        toast("请输入地图名字");
                                    }
                                }
                                @Override
                                public void onCancel(BaseDialog dialog) {

                                }
                            }).show();
                }
                break;
            case R.id.bt_batch_delete:
                if (isShowSelected) {              //是否显示批量选择
                    btBatch.setBackgroundResource(R.drawable.bt_bg__map);
                    tvDeleteAll.setVisibility(View.GONE);
                    isShowSelected = false;
                    mapAdapter.showSelected(false);
                } else {
                    btBatch.setBackgroundResource(R.drawable.button_shape_blue);
                    isShowSelected = true;
                    mapAdapter.showSelected(true);
                    tvDeleteAll.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.tv_delete_all:
                new InputDialog.Builder(getActivity())
                        .setTitle("是否删除所选")
                        .setEditVisibility(View.GONE)
                        .setListener(new InputDialog.OnListener() {
                            @Override
                            public void onConfirm(BaseDialog dialog, String content) {
                                List<DDRVLNMap.reqMapOperational.OptItem> optItems=null;
                                for (int i=0;i<mapInfos.size();i++){
                                    if (mapInfos.get(i).isSelected()){
                                        optItems=new ArrayList<>();
                                        DDRVLNMap.reqMapOperational.OptItem optItem=DDRVLNMap.reqMapOperational.OptItem.newBuilder()
                                                .setTypeValue(1)
                                                .setSourceName(ByteString.copyFromUtf8(mapInfos.get(i).getMapName()))
                                                .build();
                                        optItems.add(optItem);
                                    }
                                }
                                if (optItems!=null){
                                    for (MapInfo mapInfo:mapInfos){
                                        mapInfo.setSelected(false);
                                    }
                                    btBatch.setBackgroundResource(R.drawable.bt_bg__map);
                                    tvDeleteAll.setVisibility(View.GONE);
                                    isShowSelected = false;
                                    mapAdapter.showSelected(false);
                                    tcpClient.reqMapOperational(optItems);
                                    waitDialog=new WaitDialog.Builder(getAttachActivity())
                                            .setMessage("正在删除...")
                                            .show();
                                }else {
                                    toast("请先选择要删除的图片");
                                }
                            }
                            @Override
                            public void onCancel(BaseDialog dialog) {
                            }
                        }).show();
                break;
            case R.id.iv_back:
                mapDetailLayout.setVisibility(View.GONE);
                mapLayout.setVisibility(View.VISIBLE);
                mPosition=0;
              /*  try {
                    tcpClient.saveDataToServer(mapFileStatus.getReqDDRVLNMapEx(),targetPoints,pathLines,taskModes);
                    Logger.e("----------:"+taskModes.size());
                }catch (NullPointerException e){
                    e.printStackTrace();
                }*/
                PointView.getInstance(getAttachActivity()).clearDraw();
                LineView.getInstance(getAttachActivity()).clearDraw();
                GridLayerView.getInstance(zoomMap).onDestroy();
                break;
            case R.id.tv_target_point:
                mPosition=0;
                if (pointDetailLayout.getVisibility() == View.GONE) {
                    leftDetailLayout.setVisibility(View.VISIBLE);
                    pointDetailLayout.setVisibility(View.VISIBLE);
                    pathDetailLayout.setVisibility(View.GONE);
                    taskDetailLayout.setVisibility(View.GONE);
                    recyclerDetail.setAdapter(targetPointAdapter);
                    for (TargetPoint targetPoint:targetPoints){
                        targetPoint.setSelected(false);
                    }
                    PointView.getInstance(getAttachActivity()).clearDraw();
                    LineView.getInstance(getAttachActivity()).clearDraw();
                    setIconDefault();
                    tvTargetPoint.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.iv_target_blue),null,null,null);
                    tvTargetPoint.setTextColor(Color.parseColor("#0399ff"));
                    if (targetPoints.size() > 0) {
                        targetPoints.get(mPosition).setSelected(true);
                        targetPointAdapter.setNewData(targetPoints);
                        etPointName.setText(targetPoints.get(mPosition).getName());
                        etX.setText(targetPoints.get(mPosition).getX());
                        etY.setText(targetPoints.get(mPosition).getY());
                        etToward.setText(targetPoints.get(mPosition).getTheta());
                        if (targetPoints.get(mPosition).getName().equals("初始点")){
                            layoutEdit.setVisibility(View.GONE);
                        }else {
                            layoutEdit.setVisibility(View.VISIBLE);
                        }
                        etX.et_content.addTextChangedListener(new MyEditTextChangeListener(0,PointView.getInstance(getAttachActivity()),targetPoints.get(mPosition),zoomMap));
                        etY.et_content.addTextChangedListener(new MyEditTextChangeListener(1,PointView.getInstance(getAttachActivity()),targetPoints.get(mPosition),zoomMap));
                        etToward.et_content.addTextChangedListener(new MyEditTextChangeListener(2,PointView.getInstance(getAttachActivity()),targetPoints.get(mPosition),zoomMap));
                        PointView.getInstance(getAttachActivity()).setPoint(targetPoints.get(mPosition));
                        zoomMap.invalidate();
                    }
                } else {
                    setIconDefault();
                    leftDetailLayout.setVisibility(View.GONE);
                    pointDetailLayout.setVisibility(View.GONE);
                }
                break;
            case R.id.save_point:          // 保存已修改的目标点，并发送到服务端
                if (mPosition<targetPoints.size()){
                    targetPoints.get(mPosition).setName(etPointName.getText().toString());
                    targetPoints.get(mPosition).setX(etX.getFloatText());
                    targetPoints.get(mPosition).setY(etY.getFloatText());
                    targetPoints.get(mPosition).setTheta(etToward.getIntegerText());
                    targetPointAdapter.setNewData(targetPoints);
                    etX.setText(targetPoints.get(mPosition).getX());
                    etY.setText(targetPoints.get(mPosition).getY());
                    etToward.setText(targetPoints.get(mPosition).getTheta());
                    BaseDialog waitDialog1=new WaitDialog.Builder(getAttachActivity()).setMessage("保存中...").show();
                    getAttachActivity().postDelayed(()->{
                        try {
                            tcpClient.saveDataToServer(mapFileStatus.getReqDDRVLNMapEx(),targetPoints,pathLines,taskModes);
                            Logger.e("----------:"+targetPoints.size());
                        }catch (NullPointerException e){
                            e.printStackTrace();
                        }
                        waitDialog1.dismiss();
                    },500);
                }else {
                    toast("该目标点不存在");
                }
                break;
            case R.id.tv_path:
                mPosition=0;
                if (pathDetailLayout.getVisibility() == View.GONE) {        //如果路径编辑部分不可见
                    setIconDefault();
                    tvPath.setTextColor(Color.parseColor("#0399ff"));
                    tvPath.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.iv_path_blue),null,null,null);
                    leftDetailLayout.setVisibility(View.VISIBLE);
                    pointDetailLayout.setVisibility(View.GONE);
                    taskDetailLayout.setVisibility(View.GONE);
                    pathDetailLayout.setVisibility(View.VISIBLE);
                    recyclerDetail.setAdapter(pathAdapter);
                    for (PathLine pathLine:pathLines){
                        pathLine.setSelected(false);
                    }
                    PointView.getInstance(getAttachActivity()).clearDraw();
                    LineView.getInstance(getAttachActivity()).clearDraw();
                    if (pathLines.size() > 0) {
                        pathLines.get(mPosition).setSelected(true);
                        pathAdapter.setNewData(pathLines);
                        etPathName.setText(pathLines.get(0).getName());
                        tv_Spinner.setValueText(pathLines.get(0).getPathModel());
                        initCS(tv_Spinner.getTextVaule());
                        etSpeed.setText(pathLines.get(0).getVelocity());
                        tvConfig.setText(pathLines.get(0).getConfig());
                        LineView.getInstance(getAttachActivity()).setPoints(pathLines.get(mPosition).getPathPoints());
                        zoomMap.invalidate();
                        actionAdapter.setNewData(selectActionList(pathLines.get(0).getPathPoints()));
                    }
                } else {
                    setIconDefault();
                    leftDetailLayout.setVisibility(View.GONE);
                    pathDetailLayout.setVisibility(View.GONE);
                }
                break;
            case R.id.spinner_mode:
                showPathModePopupWindow(tv_Spinner);
                break;
            case R.id.bt_add_action:  //添加动作
                if (pathLines.size()>0&&mPosition<pathLines.size()){
                    new SelectDialog.Builder(getAttachActivity())
                            .setPointList(defaultActionList(pathLines.get(mPosition).getPathPoints()))
                            .setActionList(actionList)
                            .setGravity(Gravity.CENTER)
                            .setListener(new SelectDialog.OnListener() {
                                @Override
                                public void onSelected(int position, Object o) {

                                }

                                @Override
                                public void onSelectedAction(int position, Object o) {

                                }

                                @Override
                                public void onConfirm() {
                                    actionAdapter.setNewData(selectActionList(pathLines.get(mPosition).getPathPoints()));
                                }
                            }).show();

                }else {
                    toast("该路径不存在!");
                }
                break;
            case R.id.save_path:
                if (pathLines.size()>0&&mPosition<pathLines.size()){
                    pathLines.get(mPosition).setName(etPathName.getText().toString());
                    pathLines.get(mPosition).setVelocity(etSpeed.getFloatText());
                    pathLines.get(mPosition).setPathModel(tv_Spinner.getTextVaule());
                    pathAdapter.setNewData(pathLines);
                    BaseDialog waitDialog2=new WaitDialog.Builder(getAttachActivity()).setMessage("保存中...").show();
                    getAttachActivity().postDelayed(()->{
                        try {
                            tcpClient.saveDataToServer(mapFileStatus.getReqDDRVLNMapEx(),targetPoints,pathLines,taskModes);
                            Logger.e("----------:"+pathLines.size());
                        }catch (NullPointerException e){
                            e.printStackTrace();
                        }
                        waitDialog2.dismiss();
                    },500);
                }else {
                    toast("该路径不存在");
                }
                break;
            case R.id.tv_task:
                mPosition=0;
                if (taskDetailLayout.getVisibility()==View.GONE){
                    setIconDefault();
                    tvTask.setTextColor(Color.parseColor("#0399ff"));
                    tvTask.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.iv_task_blue),null,null,null);
                    leftDetailLayout.setVisibility(View.VISIBLE);
                    pointDetailLayout.setVisibility(View.GONE);
                    taskDetailLayout.setVisibility(View.VISIBLE);
                    pathDetailLayout.setVisibility(View.GONE);
                    layoutSelect.setVisibility(View.VISIBLE);
                    selectPointRecycler.setVisibility(View.GONE);
                    selectPathRecycler.setVisibility(View.GONE);
                    layoutSort.setVisibility(View.GONE);
                    recyclerDetail.setAdapter(taskAdapter);
                    for (TaskMode taskMode:taskModes){
                        taskMode.setSelected(false);
                    }
                    PointView.getInstance(getAttachActivity()).clearDraw();
                    LineView.getInstance(getAttachActivity()).clearDraw();
                    if (taskModes.size()>0){
                        String taskName=taskModes.get(0).getName();
                        taskName=taskName.replaceAll("DDRTask_","");
                        taskName=taskName.replaceAll(".task","");
                        etTaskName.setText(taskName);
                        taskModes.get(mPosition).setSelected(true);
                        try {
                            initSelectRecycler(0);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                        zoomMap.invalidate();
                    }
                    taskAdapter.setNewData(taskModes);
                }else {
                    setIconDefault();
                    leftDetailLayout.setVisibility(View.GONE);
                    taskDetailLayout.setVisibility(View.GONE);
                }
               break;
            case R.id.tv_target_spread:
                if (selectPointRecycler.getVisibility()==View.GONE){
                    selectPointRecycler.setVisibility(View.VISIBLE);
                    selectPathRecycler.setVisibility(View.GONE);
                }else {
                    selectPointRecycler.setVisibility(View.GONE
                    );
                    selectPathRecycler.setVisibility(View.GONE);
                }
                break;
            case R.id.tv_path_spread:
                if (selectPathRecycler.getVisibility()==View.GONE){
                    selectPointRecycler.setVisibility(View.GONE);
                    selectPathRecycler.setVisibility(View.VISIBLE);
                }else {
                    selectPointRecycler.setVisibility(View.GONE);
                    selectPathRecycler.setVisibility(View.GONE);
                }
                break;
            case R.id.bt_next:
                layoutSelect.setVisibility(View.GONE);
                layoutSort.setVisibility(View.VISIBLE);
                sortAdapter.setNewData(taskModes.get(mPosition).getBaseModes());
                break;
            case R.id.bt_back:
                layoutSelect.setVisibility(View.VISIBLE);
                layoutSort.setVisibility(View.GONE);
                break;
            case R.id.bt_save:
                if (mPosition<taskModes.size()){
                    if (taskModes.get(mPosition).getBaseModes().size()>0){
                        String taskName=etTaskName.getText().toString().trim();
                        taskName="DDRTask_"+taskName+".task";
                        taskModes.get(mPosition).setName(taskName);
                        taskAdapter.setNewData(taskModes);
                        BaseDialog waitDialog3=new WaitDialog.Builder(getAttachActivity()).setMessage("保存中...").show();
                        getAttachActivity().postDelayed(()->{
                            try {
                                tcpClient.saveDataToServer(mapFileStatus.getReqDDRVLNMapEx(),targetPoints,pathLines,taskModes);
                                Logger.e("----------:"+taskModes.size());
                            }catch (NullPointerException e){
                                e.printStackTrace();
                            }
                            waitDialog3.dismiss();
                            layoutSelect.setVisibility(View.VISIBLE);
                            layoutSort.setVisibility(View.GONE);
                        },500);
                    }else {
                        toast("请给任务添加路径");
                    }
                }else {
                    toast("该任务不存在");
                }
                break;
            case R.id.tv_add_new:
                if (pointDetailLayout.getVisibility()==View.VISIBLE){
                    PointView.getInstance(getAttachActivity()).clearDraw();
                    Intent intent=new Intent(getAttachActivity(),MapEditActivity.class);
                    intent.putExtra("type",1);
                    intent.putExtra("bitmapPath",bitmapPath);
                    intent.putExtra("targetList",(Serializable) targetPoints);
                    intent.putExtra("pathList",(Serializable) pathLines);
                    startActivity(intent);
                }else if (pathDetailLayout.getVisibility()==View.VISIBLE){
                    LineView.getInstance(getAttachActivity()).clearDraw();
                    Intent intent=new Intent(getAttachActivity(),MapEditActivity.class);
                    intent.putExtra("type",2);
                    intent.putExtra("bitmapPath",bitmapPath);
                    intent.putExtra("targetList",(Serializable) targetPoints);
                    intent.putExtra("pathList",(Serializable) pathLines);
                    startActivity(intent);
                }else if (taskDetailLayout.getVisibility()==View.VISIBLE){
                    new InputDialog.Builder(getAttachActivity())
                            .setTitle("任务名")
                            .setHint("请输入名字")
                            .setListener(new InputDialog.OnListener() {
                                @Override
                                public void onConfirm(BaseDialog dialog, String content) {
                                    if (!content.isEmpty()){
                                        //选择按键
                                        content=content.replaceAll(" ","");
                                        String name="DDRTask_"+content+".task";
                                        layoutSelect.setVisibility(View.VISIBLE);
                                        layoutSort.setVisibility(View.GONE);
                                        recyclerDetail.setAdapter(taskAdapter);
                                        TaskMode taskMode=new TaskMode();
                                        taskMode.setName(name);
                                        taskMode.setType(0);
                                        taskMode.setRunCounts(999);
                                        taskMode.setStartHour(0);
                                        taskMode.setStartMin(0);
                                        taskMode.setEndHour(24);
                                        taskMode.setEndMin(0);
                                        taskModes.add(0,taskMode);
                                        mPosition=0;
                                        if (taskModes.size()>0){
                                            etTaskName.setText(content);
                                            try {
                                                initSelectRecycler(0);
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            } catch (ClassNotFoundException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        for (TaskMode taskMode1:taskModes){
                                            taskMode1.setSelected(false);
                                        }
                                        taskModes.get(mPosition).setSelected(true);
                                        taskAdapter.setNewData(taskModes);
                                    }
                                }
                                @Override
                                public void onCancel(BaseDialog dialog) {

                                }
                            }).show();
                }
                break;
            case R.id.tv_delete:
                new InputDialog.Builder(getActivity())
                        .setTitle("是否删除")
                        .setEditVisibility(View.GONE)
                        .setListener(new InputDialog.OnListener() {
                            @Override
                            public void onConfirm(BaseDialog dialog, String content) {
                                if (pointDetailLayout.getVisibility()==View.VISIBLE){                   //删除目标点
                                    if (targetPoints.size()>0){
                                        if (mPosition<targetPoints.size()){
                                            targetPoints.remove(mPosition);
                                            targetPointAdapter.setNewData(targetPoints);
                                            if (targetPoints.size()==0){
                                                etPointName.setText("无目标点");
                                                etX.setText(0);
                                                etY.setText(0);
                                                etToward.setText(0);
                                            }else {
                                                etPointName.setText(targetPoints.get(0).getName());
                                                etX.setText(targetPoints.get(0).getX());
                                                etY.setText(targetPoints.get(0).getY());
                                                etToward.setText(targetPoints.get(0).getTheta());
                                            }
                                            PointView.getInstance(getAttachActivity()).setPoint(null);
                                            zoomMap.invalidate();
                                        }else {
                                            toast("请先选择一个目标点再删除");
                                        }
                                    }else {
                                        toast("无目标点");
                                        etPointName.setText("无目标点");
                                        etX.setText(0);
                                        etY.setText(0);
                                        etToward.setText(0);
                                    }
                                }else if (pathDetailLayout.getVisibility()==View.VISIBLE){           //删除路径
                                    if (pathLines.size()>0){
                                        if (mPosition<pathLines.size()){
                                            pathLines.remove(mPosition);
                                            pathAdapter.setNewData(pathLines);
                                            if (pathLines.size()==0){
                                                etPathName.setText("无路径");
                                                tv_Spinner.setValueText(0);
                                                etSpeed.setText(0);
                                                tvConfig.setText("");
                                            }else {
                                                etPathName.setText(pathLines.get(0).getName());
                                                tv_Spinner.setValueText(pathLines.get(0).getPathModel());
                                                etSpeed.setText(pathLines.get(0).getVelocity());
                                                tvConfig.setText(pathLines.get(0).getConfig());
                                                actionAdapter.setNewData(selectActionList(pathLines.get(0).getPathPoints()));
                                            }
                                            LineView.getInstance(getAttachActivity()).setPoints(null);
                                            zoomMap.invalidate();
                                        }else {
                                            toast("请先选择路径再删除");
                                        }
                                    }else {
                                        toast("无路径");
                                        etPathName.setText("无路径");
                                        tv_Spinner.setValueText(0);
                                        etSpeed.setText(0);
                                        tvConfig.setText("");
                                    }
                                }else if (taskDetailLayout.getVisibility()==View.VISIBLE){         //删除任务
                                    if (taskModes.size()>0){
                                        if (mPosition<taskModes.size()){
                                            taskModes.remove(mPosition);
                                            taskAdapter.setNewData(taskModes);
                                            if (taskModes.size()==0){
                                                etTaskName.setText("无任务");
                                                selectPointAdapter.setNewData(null);
                                                selectPathAdapter.setNewData(null);
                                            }else {
                                                String taskName=taskModes.get(0).getName();
                                                taskName=taskName.replaceAll("DDRTask_","");
                                                taskName=taskName.replaceAll(".task","");
                                                etTaskName.setText(taskName);
                                                try {
                                                    initSelectRecycler(0);
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                } catch (ClassNotFoundException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }else {
                                            toast("请先选择任务再删除");
                                        }
                                    }else {
                                        etTaskName.setText("无任务");
                                        selectPointAdapter.setNewData(null);
                                        selectPathAdapter.setNewData(null);
                                    }
                                }
                                tcpClient.saveDataToServer(mapFileStatus.getReqDDRVLNMapEx(),targetPoints,pathLines,taskModes);
                            }
                            @Override
                            public void onCancel(BaseDialog dialog) {
                            }
                        }).show();
                break;
            case R.id.tv_edit_map:
                Intent intent=new Intent(getAttachActivity(),MapEditActivity.class);
                intent.putExtra("type",3);
                intent.putExtra("bitmapPath",bitmapPath);
                startActivity(intent);
                break;
            case R.id.tv_025m:
                if (!tv025m.getSelected()) {
                    GridLayerView.getInstance(zoomMap).setPrecision((float) 0.25);        //将图片网格化
                    zoomMap.invalidate();
                    setIconDefault1();
                    tv025m.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.checkedwg), null, null, null);
                    tv025m.setSelected(true);
                } else {
                    GridLayerView.getInstance(zoomMap).setPrecision(0);        //取消网格
                    zoomMap.invalidate();
                    setIconDefault1();

                }
                break;
            case R.id.tv_05m:
                if (!tv05m.getSelected()) {
                    GridLayerView.getInstance(zoomMap).setPrecision((float) 0.5);        //将图片网格化
                    zoomMap.invalidate();
                    setIconDefault1();
                    tv05m.setSelected(true);
                    tv05m.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.checkedwg),null,null,null);
                } else {
                    setIconDefault1();
                    GridLayerView.getInstance(zoomMap).setPrecision(0);        //取消网格
                    zoomMap.invalidate();

                }
                break;
            case R.id.tv_1m:
                if (!tv1m.getSelected()) {
                    GridLayerView.getInstance(zoomMap).setPrecision((float) 1);        //将图片网格化
                    zoomMap.invalidate();
                    setIconDefault1();
                    tv1m.setSelected(true);
                    tv1m.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.checkedwg),null,null,null);
                } else {
                    setIconDefault1();
                    GridLayerView.getInstance(zoomMap).setPrecision(0);        //取消网格
                    zoomMap.invalidate();
                }
                break;
            case R.id.tv_2m:
                if (!tv2m.getSelected()) {
                    GridLayerView.getInstance(zoomMap).setPrecision((float) 2);        //将图片网格化
                    zoomMap.invalidate();
                    setIconDefault1();
                    tv2m.setSelected(true);
                    tv2m.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.checkedwg),null,null,null);
                } else {
                    setIconDefault1();
                    GridLayerView.getInstance(zoomMap).setPrecision(0);        //取消网格
                    zoomMap.invalidate();
                }
                break;
        }
    }

    /**
     * 把图标状态设置回默认
     */
    @SuppressLint("ResourceAsColor")
    private void setIconDefault(){
        tvTargetPoint.setTextColor(Color.parseColor("#ccffffff"));
        tvTargetPoint.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.iv_target_gray),null,null,null);
        tvPath.setTextColor(Color.parseColor("#ccffffff"));
        tvPath.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.iv_path_gray),null,null,null);
        tvTask.setTextColor(Color.parseColor("#ccffffff"));
        tvTask.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.iv_task_gray),null,null,null);

    }

    /**
     * 设置网格图标默认状态
     */
    private void setIconDefault1(){
        tv025m.setSelected(false);
        tv05m.setSelected(false);
        tv1m.setSelected(false);
        tv2m.setSelected(false);
        tv025m.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.nocheckedwg), null, null, null);
        tv05m.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.nocheckedwg), null, null, null);
        tv1m.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.nocheckedwg), null, null, null);
        tv2m.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.nocheckedwg), null, null, null);
    }

    /**
     * 筛选出非默认点的动作列表
     * @param pathPoints
     * @return
     */
    private List<PathLine.PathPoint> selectActionList(List<PathLine.PathPoint> pathPoints){
        List<PathLine.PathPoint> pathPoints1=new ArrayList<>();
        for (int i=0;i<pathPoints.size();i++){
            if (pathPoints.get(i).getPointType()!=8){
                pathPoints1.add(pathPoints.get(i));
            }
        }
        return pathPoints1;
    }

    /**
     * 筛选出默认点列表
     * @param pathPoints
     * @return
     */
    private List<PathLine.PathPoint> defaultActionList(List<PathLine.PathPoint> pathPoints){
        List<PathLine.PathPoint> pathPoints1=new ArrayList<>();
        for (int i=0;i<pathPoints.size();i++){
            if (pathPoints.get(i).getPointType()==8){
                pathPoints1.add(pathPoints.get(i));
            }
        }
        return pathPoints1;
    }




    private BaseDialog dialog,waitDialog;
    private Bitmap lookBitmap;
    private String bitmapPath;          // 点击的图片存储地址

    /**
     * 地图Recycler的点击事件
     */
    public void onItemClick() {
        mapAdapter.setOnItemClickListener(((adapter, view, position) -> {
            Logger.e("--------:" + mapInfos.get(position).getMapName());
            if (isShowSelected) {
                if (mapInfos.get(position).isUsing()){
                    toast("当前使用地图无法删除！");
                }else {
                    MapInfo mapInfo = mapInfos.get(position);
                    if (mapInfo.isSelected()) {
                        mapInfo.setSelected(false);
                    } else {
                        mapInfo.setSelected(true);
                    }
                    mapAdapter.setData(position, mapInfo);
                }
            } else {
                if (mapInfos.get(position).isUsing()){               //判断当前点击的地图是否在使用中，是则跳转详情
                   intoMapDetail(position);
                }else {        //如果不再使用中 则弹出弹窗
                    showMapSettingWindow(view.findViewById(R.id.iv_more),position);

                }
            }
        }));

        //长按地图列表子项
        mapAdapter.setOnItemLongClickListener(((adapter, view, position) -> {
            toast("触发长按效果");
            if (!isShowSelected){
                btBatch.setBackgroundResource(R.drawable.button_shape_blue);
                isShowSelected = true;
                mapAdapter.showSelected(true);
                tvDeleteAll.setVisibility(View.VISIBLE);
            }else {
                btBatch.setBackgroundResource(R.drawable.bt_bg__map);
                tvDeleteAll.setVisibility(View.GONE);
                isShowSelected = false;
                mapAdapter.showSelected(false);
            }
            return true;
        }));



    }

    /**
     * 进入地图详情页面
     */
    private void intoMapDetail(int position){
        bitmapPath=mapInfos.get(position).getBitmap();
        mapName=mapInfos.get(position).getMapName();
        tcpClient.getMapInfo(ByteString.copyFromUtf8(mapName));
        dialog = new WaitDialog.Builder(getAttachActivity())
                .setMessage("加载"+mapName+"地图信息中")
                .show();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(mapInfos.get(position).getBitmap());
            lookBitmap= BitmapFactory.decodeStream(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        String name=mapInfos.get(position).getMapName();
        name=name.replaceAll("OneRoute_","");
        tvMapName.setText("地图名称："+name);
        tvMapSize.setText("地图面积："+(int)mapInfos.get(position).getWidth()+"x"+(int)mapInfos.get(position).getHeight()+"m²");
        tvCreateTime.setText("建立日期："+mapInfos.get(position).getTime());
        getAttachActivity().postDelayed(() -> {
            if (dialog.isShowing()) {
                dialog.dismiss();
                toast("加载失败！");
                mapLayout.setVisibility(View.GONE);
                mapDetailLayout.setVisibility(View.VISIBLE);
                leftDetailLayout.setVisibility(View.VISIBLE);
                pointDetailLayout.setVisibility(View.VISIBLE);
                pathDetailLayout.setVisibility(View.GONE);
                taskDetailLayout.setVisibility(View.GONE);
                zoomMap.setImageBitmap(lookBitmap);

            }

        }, 5000);
    }

    private String switchMapName,switchBitmapPath;

    /**
     * 点击地图右上角设置弹出弹窗
     */
    private void showMapSettingWindow(View view,int position){
        View contentView=getAttachActivity().getLayoutInflater().from(getAttachActivity()).inflate(R.layout.map_management_window,null);
        CustomPopuWindow customPopuWindow=new CustomPopuWindow.PopupWindowBuilder(getAttachActivity())
                .setView(contentView)
                .create()
                .showAsDropDown(view,-DpOrPxUtils.dip2px(getAttachActivity(),100),-10);
        View.OnClickListener listener=v -> {
            customPopuWindow.dissmiss();
          switch (v.getId()){
             /* case R.id.tv_rename:
                  String mapName=mapInfos.get(position).getMapName();
                  if (!NotifyBaseStatusEx.getInstance().getCurroute().equals(mapName)){
                      new InputDialog.Builder(getAttachActivity())
                              .setTitle("修改名称")
                              .setHint("输入地图名")
                              .setListener(new InputDialog.OnListener() {
                                  @Override
                                  public void onConfirm(BaseDialog dialog, String content) {
                                      if (!content.isEmpty()){
                                          content="OneRoute_"+content;
                                          List<DDRVLNMap.reqMapOperational.OptItem> optItems=new ArrayList<>();
                                          DDRVLNMap.reqMapOperational.OptItem optItem=DDRVLNMap.reqMapOperational.OptItem.newBuilder()
                                                  .setTypeValue(3)
                                                  .setSourceName(ByteString.copyFromUtf8(mapName))
                                                  .setTargetName(ByteString.copyFromUtf8(content))
                                                  .build();
                                          optItems.add(optItem);
                                          tcpClient.reqMapOperational(optItems);
                                          waitDialog=new WaitDialog.Builder(getAttachActivity())
                                                  .setMessage("正在修改中")
                                                  .show();
                                          getAttachActivity().postDelayed(() -> {
                                              if (waitDialog.isShowing()) {
                                                  waitDialog.dismiss();
                                                  toast("修改失败！");
                                              }
                                          }, 10000);
                                      }
                                  }
                                  @Override
                                  public void onCancel(BaseDialog dialog) {
                                      toast("取消修改地图名");
                                  }
                              }).show();
                  }else {
                      toast("当前地图正在使用中，无法修改");
                  }
                  break;*/
              case R.id.tv_switch:
                  Logger.e("------------"+NotifyBaseStatusEx.getInstance().getMode());
                  new InputDialog.Builder(getActivity())
                          .setTitle("是否切换地图")
                          .setEditVisibility(View.GONE)
                          .setListener(new InputDialog.OnListener() {
                              @Override
                              public void onConfirm(BaseDialog dialog, String content) {
                                  if (NotifyBaseStatusEx.getInstance().getMode()==1){
                                      switchMapName=mapInfos.get(position).getMapName();
                                      switchBitmapPath=mapInfos.get(position).getBitmap();
                                      Logger.e("-----把地图切换到："+switchMapName);
                                      tcpClient.reqRunControlEx(switchMapName);
                                      waitDialog=new WaitDialog.Builder(getAttachActivity())
                                              .setMessage("正在切换中")
                                              .show();
                                      getAttachActivity().postDelayed(() -> {
                                          if (waitDialog.isShowing()) {
                                              waitDialog.dismiss();
                                              toast("切换失败！");
                                          }
                                      }, 5000);

                                  }else {
                                      toast("非待命模式无法切换地图");
                                  }
                              }
                              @Override
                              public void onCancel(BaseDialog dialog) {
                              }
                          }).show();
                  break;
             /* case R.id.tv_recover:
                  new InputDialog.Builder(getActivity())
                          .setTitle("是否恢复")
                          .setEditVisibility(View.GONE)
                          .setListener(new InputDialog.OnListener() {
                              @Override
                              public void onConfirm(BaseDialog dialog, String content) {
                                  if (NotifyBaseStatusEx.getInstance().getCurroute().equals(mapInfos.get(position).getMapName())){
                                      toast("当前地图正在使用中，无法修改");
                                  }else {
                                      List<DDRVLNMap.reqMapOperational.OptItem> optItems1=new ArrayList<>();
                                      DDRVLNMap.reqMapOperational.OptItem optItem1=DDRVLNMap.reqMapOperational.OptItem.newBuilder()
                                              .setTypeValue(2)
                                              .setSourceName(ByteString.copyFromUtf8(mapInfos.get(position).getMapName()))
                                              .build();
                                      optItems1.add(optItem1);
                                      tcpClient.reqMapOperational(optItems1);
                                      waitDialog=new WaitDialog.Builder(getAttachActivity())
                                              .setMessage("正在修改中")
                                              .show();
                                      getAttachActivity().postDelayed(() -> {
                                          if (waitDialog.isShowing()) {
                                              waitDialog.dismiss();
                                              toast("修改失败！");
                                          }
                                      }, 4000);
                                  }
                              }
                              @Override
                              public void onCancel(BaseDialog dialog) {
                              }
                          }).show();
                  break;*/
             /* case R.id.tv_delete:
                  new InputDialog.Builder(getActivity())
                          .setTitle("是否删除")
                          .setEditVisibility(View.GONE)
                          .setListener(new InputDialog.OnListener() {
                              @Override
                              public void onConfirm(BaseDialog dialog, String content) {
                                  if (NotifyBaseStatusEx.getInstance().getCurroute().equals(mapInfos.get(position).getMapName())){
                                      toast("当前地图正在使用中，无法删除");
                                  }else {
                                      List<DDRVLNMap.reqMapOperational.OptItem> optItems2=new ArrayList<>();
                                      DDRVLNMap.reqMapOperational.OptItem optItem2=DDRVLNMap.reqMapOperational.OptItem.newBuilder()
                                              .setTypeValue(1)
                                              .setSourceName(ByteString.copyFromUtf8(mapInfos.get(position).getMapName()))
                                              .build();
                                      optItems2.add(optItem2);
                                      tcpClient.reqMapOperational(optItems2);
                                      waitDialog=new WaitDialog.Builder(getAttachActivity())
                                              .setMessage("正在删除...")
                                              .show();
                                  }
                              }

                              @Override
                              public void onCancel(BaseDialog dialog) {

                              }
                          }).show();
                  break;*/
              case R.id.tv_detail:
                  intoMapDetail(position);
                  break;
              case R.id.tv_setting:                     //进入地图管理界面
                  mapName=mapInfos.get(position).getMapName();
                  tcpClient.getMapInfo(ByteString.copyFromUtf8(mapName));
                  startActivity(MapSettingActivity.class);
                  break;
          }
        };
        //contentView.findViewById(R.id.tv_rename).setOnClickListener(listener);
        contentView.findViewById(R.id.tv_switch).setOnClickListener(listener);
        //contentView.findViewById(R.id.tv_recover).setOnClickListener(listener);
       // contentView.findViewById(R.id.tv_delete).setOnClickListener(listener);
        contentView.findViewById(R.id.tv_detail).setOnClickListener(listener);
        contentView.findViewById(R.id.tv_setting).setOnClickListener(listener);

    }

    /**
     * 判断是否显示贴边距离
     * @param mode
     */
    private void initCS(int mode){
        if (mode==66){
            tv_cs.setVisibility(View.VISIBLE);
            tv_cm.setVisibility(View.VISIBLE);
            etParameter.setVisibility(View.VISIBLE);
        }else {
            tv_cs.setVisibility(View.GONE);
            tv_cm.setVisibility(View.GONE);
            etParameter.setVisibility(View.GONE);
        }
    }

    /**
     * 目标点Recycler的点击事件
     */
    public void onTargetItemClick() {
        targetPointAdapter.setOnItemClickListener((adapter, view, position) -> {
            etPointName.setText(targetPoints.get(position).getName());
            etX.setText(targetPoints.get(position).getX());
            etY.setText(targetPoints.get(position).getY());
            etToward.setText(targetPoints.get(position).getTheta());
            mPosition = position;
            if (targetPoints.get(mPosition).getName().equals("初始点")){
                layoutEdit.setVisibility(View.GONE);
            }else {
                layoutEdit.setVisibility(View.VISIBLE);
            }
            etX.et_content.addTextChangedListener(new MyEditTextChangeListener(0,PointView.getInstance(getAttachActivity()),targetPoints.get(mPosition),zoomMap));
            etY.et_content.addTextChangedListener(new MyEditTextChangeListener(1,PointView.getInstance(getAttachActivity()),targetPoints.get(mPosition),zoomMap));
            etToward.et_content.addTextChangedListener(new MyEditTextChangeListener(2,PointView.getInstance(getAttachActivity()),targetPoints.get(mPosition),zoomMap));
            for (TargetPoint targetPoint:targetPoints){
                targetPoint.setSelected(false);
            }
            targetPoints.get(position).setSelected(true);
            targetPointAdapter.setNewData(targetPoints);
            PointView.getInstance(getAttachActivity()).clearDraw();
            LineView.getInstance(getAttachActivity()).clearDraw();
            PointView.getInstance(getAttachActivity()).setPoint(targetPoints.get(position));
            zoomMap.invalidate();
        });
    }

    /**
     * 路径Recycler的点击事件
     */
    public void onPathItemClick() {
        pathAdapter.setOnItemClickListener((adapter, view, position) -> {
            mPosition = position;
            for (PathLine pathLine:pathLines){
                pathLine.setSelected(false);
            }
            pathLines.get(position).setSelected(true);
            etPathName.setText(pathLines.get(position).getName());
            int mode=pathLines.get(position).getPathModel();
            tv_Spinner.setValueText(mode);
            initCS(mode);
            etSpeed.setText(pathLines.get(position).getVelocity());
            tvConfig.setText(pathLines.get(position).getConfig());
            actionAdapter.setNewData(selectActionList(pathLines.get(position).getPathPoints()));
            pathAdapter.setNewData(pathLines);
            LineView.getInstance(getAttachActivity()).clearDraw();
            PointView.getInstance(getAttachActivity()).clearDraw();
            LineView.getInstance(getAttachActivity()).setPoints(pathLines.get(position).getPathPoints());
            zoomMap.invalidate();
        });
    }

    /**
     * 动作Recycler的点击事件
     */
    public void onActionItemClick() {
        actionAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            Logger.e("------点击："+position);
            int position1=position;
            switch (view.getId()) {
                case R.id.tv_delete:
                    Logger.e("------点击删除该点动作");
                    selectActionList(pathLines.get(mPosition).getPathPoints()).get(position).setPointType(8);
                    actionAdapter.setNewData(selectActionList(pathLines.get(mPosition).getPathPoints()));
                    break;
                case R.id.tv_action_type:
                    PointView.getInstance(getAttachActivity()).setPathPoint(selectActionList(pathLines.get(mPosition).getPathPoints()).get(position));
                    zoomMap.invalidate();
                    break;
            }
        });
    }

    /**
     * 任务Recycler的点击事件
     */
    public void onTaskItemClick(){
        taskAdapter.setOnItemClickListener((adapter, view, position) -> {
            mPosition=position;
            for (TaskMode taskMode:taskModes){
                taskMode.setSelected(false);
            }
            taskModes.get(position).setSelected(true);
            taskAdapter.setNewData(taskModes);
            String taskName=taskModes.get(position).getName();
            taskName=taskName.replaceAll("DDRTask_","");
            taskName=taskName.replaceAll(".task","");
            etTaskName.setText(taskName);
            try {
                initSelectRecycler(position);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            zoomMap.invalidate();
        });
    }

    private List<TargetPoint> selectPoints;
    private List<PathLine> selectPaths;
    /**
     * 设置每个task子项中被选择的目标点和路径列表
     */
    private void initSelectRecycler(int position) throws IOException, ClassNotFoundException {
        selectPoints=ListTool.deepCopy(targetPoints);      //对列表深拷贝，后续的操作不影响数据源
        selectPaths=ListTool.deepCopy(pathLines);
        if (taskModes.size()>0){
            for (int i=0;i<selectPoints.size();i++){
                if (taskModes.get(position).getTargetPoints().contains(selectPoints.get(i).getName())){
                    selectPoints.get(i).setInTask(true);
                }else {
                    selectPoints.get(i).setInTask(false);
                }
            }
            selectPointAdapter.setNewData(selectPoints);
            for (int i=0;i<selectPaths.size();i++){
                if (taskModes.get(position).getPathLines().contains(selectPaths.get(i).getName())){
                    selectPaths.get(i).setInTask(true);
                }else {
                    selectPaths.get(i).setInTask(false);
                }
            }
            selectPathAdapter.setNewData(selectPaths);
            PointView.getInstance(getAttachActivity()).setPoints(selectPoints);
            LineView.getInstance(getAttachActivity()).setLineViews(selectPaths);
        }
    }

    /**
     * 选择Recycler点击事件（目标点、路径）
     */
    public void onSelectedItemClick(){
        //目标点选择
        selectPointAdapter.setOnItemClickListener((adapter, view, position) -> {
            Logger.e("------当前位置："+mPosition);
            TargetPoint targetPoint=selectPoints.get(position);
            targetPoint.setType(2);
            List<BaseMode> baseModes=taskModes.get(mPosition).getBaseModes();
            if (targetPoint.isInTask()){               //如果目标点本来就被选中，再次点击将取消
               targetPoint.setInTask(false);
               for (int i=0;i<baseModes.size();i++){
                   if (baseModes.get(i).getType()==2){
                       TargetPoint targetPoint1= (TargetPoint) baseModes.get(i);
                       if (targetPoint1.getName().equals(targetPoint.getName())){
                           baseModes.remove(i);
                           taskModes.get(mPosition).setBaseModes(baseModes);
                       }
                   }
               }
            }else {        //如果未被选中，将直接添加到列表中
                targetPoint.setInTask(true);
                baseModes.add(targetPoint);
                taskModes.get(mPosition).setBaseModes(baseModes);
            }
            selectPointAdapter.setData(position,targetPoint);
            PointView.getInstance(getAttachActivity()).setPoints(selectPoints);
            zoomMap.invalidate();

        });
        //路径选择
        selectPathAdapter.setOnItemClickListener((adapter, view, position) -> {
            PathLine pathLine=selectPaths.get(position);
            pathLine.setType(1);
            List<BaseMode> baseModes=taskModes.get(mPosition).getBaseModes();
            if (pathLine.isInTask()){
                pathLine.setInTask(false);
                for (int i=0;i<baseModes.size();i++){
                    if (baseModes.get(i).getType()==1){
                        PathLine pathLine1= (PathLine) baseModes.get(i);
                        if (pathLine.getName().equals(pathLine1.getName())){
                            baseModes.remove(i);
                            taskModes.get(mPosition).setBaseModes(baseModes);
                        }
                    }
                }
            }else {
                pathLine.setInTask(true);
                baseModes.add(pathLine);
                taskModes.get(mPosition).setBaseModes(baseModes);
            }
            selectPathAdapter.setData(position,pathLine);
            LineView.getInstance(getAttachActivity()).setLineViews(selectPaths);
            zoomMap.invalidate();
        });
    }

    /**
     * 排序Recycler的点击事件
     */
    private void onSortItemClick(){
       sortAdapter.setOnItemChildClickListener((adapter, view, position) -> {
        switch (view.getId()){
            case R.id.iv_up:
                if (position==0){
                    toast("当前位置无法改变");
                }else {
                    Collections.swap(taskModes.get(mPosition).getBaseModes(),position,position-1);
                    sortAdapter.setNewData(taskModes.get(mPosition).getBaseModes());
                }
                break;
            case R.id.iv_down:
                if (position==taskModes.get(mPosition).getBaseModes().size()-1){
                    toast("当前位置无法改变");
                }else {
                    Collections.swap(taskModes.get(mPosition).getBaseModes(),position,position+1);
                    sortAdapter.setNewData(taskModes.get(mPosition).getBaseModes());
                }
                break;
        }
       });

    }

    /************************路径选择路径模式的弹窗************************/
    private CustomPopuWindow customPopuWindow;
    private RecyclerView pathModeRecycler;
    private StringAdapter stringAdapter;
    private List<String> modeList;
    private List<String> actionList;
    private Map<Integer,String> map;
    private void showPathModePopupWindow(View view){
        View contentView=getAttachActivity().getLayoutInflater().from(getAttachActivity()).inflate(R.layout.window_path_mode,null);
        customPopuWindow=new CustomPopuWindow.PopupWindowBuilder(getAttachActivity())
                .setView(contentView)
                .create()
        .showAsDropDown(view,0,5);
        pathModeRecycler=contentView.findViewById(R.id.path_mode_recycler);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getAttachActivity());
        pathModeRecycler.setLayoutManager(layoutManager);
        pathModeRecycler.setAdapter(stringAdapter);
    }

    /**
     * 路径模式的Recycler的点击事件
     */
    private void onModeItemClick(){
        stringAdapter.setOnItemClickListener((adapter, view, position) -> {
            tv_Spinner.setText(modeList.get(position));
            initCS(tv_Spinner.getTextVaule());
            customPopuWindow.dissmiss();
        });
    }

    /**
     * 对比本地文件和服务的文件是否有区别,将服务端不存在的本地文件删除，保持一致性
     *
     * @return
     */
    public void checkFilesAllName(List<String> downloadMapNames) {
        if (downloadMapNames != null) {
            File file = new File(Environment.getExternalStorageDirectory().getPath() + "/" + "机器人");
            if (file.exists()) {
                File[] files = file.listFiles();
                if (files == null) {
                    Logger.e("空目录");
                }
                for (int i = 0; i < files.length; i++) {
                    if (downloadMapNames.size() > 0) {
                        if (downloadMapNames.contains(files[i].getName())) {
                        } else {
                            FileUtil.deleteFile(files[i]);
                        }
                    }
                }
            }
        }
    }

    /**
     * 设置图片的路径
     *
     * @param infoList
     */
    public void transformMapInfo(List<MapInfo> infoList) {
        for (int i = 0; i < infoList.size(); i++) {
            String dirName = infoList.get(i).getMapName();
            String pngPath = Environment.getExternalStorageDirectory().getPath() + "/" + "机器人" + "/" + dirName + "/" + "bkPic.png";
            if (pngPath != null) {
                infoList.get(i).setBitmap(pngPath);
            } else {
                infoList.remove(i);
            }
            if (dirName.equals(NotifyBaseStatusEx.getInstance().getCurroute())){
                infoList.get(i).setUsing(true);
            }else {
                infoList.get(i).setUsing(false);
            }
        }
        mapInfos = infoList;
    }

    private BaseDialog waitDialog2;
    private NotifyBaseStatusEx notifyBaseStatusEx=NotifyBaseStatusEx.getInstance();
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void update(MessageEvent messageEvent)  {
        switch (messageEvent.getType()) {
            case updateDDRVLNMap:
                if (dialog!=null){
                    if (dialog.isShowing()) {
                        getAttachActivity().postDelayed(() -> {
                            try {
                                targetPoints=ListTool.deepCopy(mapFileStatus.getTargetPoints());
                                pathLines=ListTool.deepCopy(mapFileStatus.getPathLines());
                                taskModes=ListTool.deepCopy(mapFileStatus.getTaskModes());
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                            Logger.e("----------:" + taskModes.size());
                            if (targetPoints.size() > 0) {
                                etPointName.setText(targetPoints.get(0).getName());
                                etX.setText(targetPoints.get(0).getX());
                                etY.setText(targetPoints.get(0).getY());
                                etToward.setText(targetPoints.get(0).getTheta());
                                targetPoints.get(0).setSelected(true);
                                PointView.getInstance(getAttachActivity()).setPoint(targetPoints.get(0));
                                if (targetPoints.get(0).getName().equals("初始点")){
                                    layoutEdit.setVisibility(View.GONE);
                                }else {
                                    layoutEdit.setVisibility(View.VISIBLE);
                                }
                            }else {
                                etPointName.setText("无目标点");
                                etX.setText(0);
                                etY.setText(0);
                                etToward.setText(0);
                            }
                            tvTargetPoint.setText("目标点"+"("+targetPoints.size()+")");
                            tvPath.setText("路径"+"("+pathLines.size()+")");
                            tvTask.setText("任务"+"("+taskModes.size()+")");
                            dialog.dismiss();
                            mapLayout.setVisibility(View.GONE);
                            mapDetailLayout.setVisibility(View.VISIBLE);
                            leftDetailLayout.setVisibility(View.VISIBLE);
                            pointDetailLayout.setVisibility(View.VISIBLE);
                            pathDetailLayout.setVisibility(View.GONE);
                            taskDetailLayout.setVisibility(View.GONE);
                            targetPointAdapter.setNewData(targetPoints);
                            recyclerDetail.setAdapter(targetPointAdapter);
                            setIconDefault();
                            setIconDefault1();
                            tvTargetPoint.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.iv_target_blue),null,null,null);
                            tvTargetPoint.setTextColor(Color.parseColor("#0399ff"));
                            zoomMap.setImageBitmap(lookBitmap);
                        }, 800);
                    }

                }
                break;
            case updatePoints:
                List<TargetPoint> targetPoints1= (List<TargetPoint>) messageEvent.getData();
                targetPoints.addAll(targetPoints1);
                targetPointAdapter.setNewData(targetPoints);
                tvTargetPoint.setText("目标点"+"("+targetPoints.size()+")");
                tcpClient.saveDataToServer(mapFileStatus.getReqDDRVLNMapEx(),targetPoints,pathLines,taskModes);
                break;
            case updatePaths:
                List<PathLine> pathLines1= (List<PathLine>) messageEvent.getData();
                pathLines.addAll(pathLines1);
                pathAdapter.setNewData(pathLines);
                tvPath.setText("路径"+"("+pathLines.size()+")");
                tcpClient.saveDataToServer(mapFileStatus.getReqDDRVLNMapEx(),targetPoints,pathLines,taskModes);
                break;
            case updateVirtualWall:
                tcpClient.saveDataToServer(mapFileStatus.getReqDDRVLNMapEx(),targetPoints,pathLines,taskModes);
                break;
            case updateRevamp:
                Logger.e("更新数据");
                break;
            case updateMapList:
                if (waitDialog!=null){
                    if (waitDialog.isShowing()){
                        waitDialog.dismiss();
                    }
                }
                getAttachActivity().postDelayed(()->{
                    transformMapInfo(mapFileStatus.getMapInfos());
                    mapAdapter.setNewData(mapInfos);
                    },500);
                break;
            case mapOperationalSucceed:
                if (waitDialog!=null){
                    if (waitDialog.isShowing()){
                        tcpClient.requestFile();
                        waitDialog.dismiss();
                    }
                }
                break;
            case switchMapSucceed:
                Logger.e("切换地图");
                tcpClient.requestFile();
                tcpClient.getMapInfo(ByteString.copyFromUtf8(switchMapName));
                if (waitDialog!=null) {
                    getAttachActivity().postDelayed(() -> {
                        if (waitDialog.isShowing()) {
                            waitDialog.dismiss();
                        }
                    }, 200);
                }
                getAttachActivity().postDelayed(()->{
                    waitDialog2=new WaitDialog.Builder(getAttachActivity())
                            .setMessage("正在自动定位中可能需要1~3分钟，请稍后...")

                            .show();
                },600);

                break;
            case updateBaseStatus:
                //Logger.e("-------------当前状态:"+notifyBaseStatusEx.getExceptionValue());
                if (waitDialog2!=null&&waitDialog2.isShowing()){
                if (!notifyBaseStatusEx.isHaveLocation()){
                    Logger.e("自动定位失败");
                    new InputDialog.Builder(getAttachActivity())
                            .setEditVisibility(View.GONE)
                            .setTitle("自动定位失败，是否进入手动定位")
                            .setConfirm("是")
                            .setCancel("否")
                            .setCanceledOnTouchOutside(false)    // 是否可以点击外部取消弹窗
                            .setListener(new InputDialog.OnListener() {
                                @Override
                                public void onConfirm(BaseDialog dialog, String content) {
                                    Intent intent=new Intent(getAttachActivity(),RelocationActivity.class);
                                    intent.putExtra("currentBitmap",switchBitmapPath);
                                    intent.putExtra("currentMapName",switchMapName);
                                    startActivity(intent);
                                }
                                @Override
                                public void onCancel(BaseDialog dialog) {

                                }
                            }).show();
                    waitDialog2.dismiss();
                    notifyBaseStatusEx.setHaveLocation(true);
                }else if (notifyBaseStatusEx.isLocationed()){
                    toast("定位成功！");
                    waitDialog2.dismiss();
                }else {
                    notifyBaseStatusEx.setHaveLocation(true);
                }
                }
                break;



        }
    }
}
