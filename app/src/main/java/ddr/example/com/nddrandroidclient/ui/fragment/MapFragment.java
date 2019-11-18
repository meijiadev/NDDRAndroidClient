package ddr.example.com.nddrandroidclient.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.protobuf.ByteString;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import DDRCommProto.BaseCmd;
import DDRVLNMapProto.DDRVLNMap;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
import ddr.example.com.nddrandroidclient.other.Logger;
import ddr.example.com.nddrandroidclient.protocobuf.CmdSchedule;
import ddr.example.com.nddrandroidclient.protocobuf.dispatcher.ClientMessageDispatcher;
import ddr.example.com.nddrandroidclient.socket.TcpClient;
import ddr.example.com.nddrandroidclient.ui.activity.CollectingActivity;
import ddr.example.com.nddrandroidclient.ui.activity.HomeActivity;
import ddr.example.com.nddrandroidclient.ui.activity.MapEditActivity;
import ddr.example.com.nddrandroidclient.ui.adapter.ActionAdapter;
import ddr.example.com.nddrandroidclient.ui.adapter.BaseModeAdapter;
import ddr.example.com.nddrandroidclient.ui.adapter.MapAdapter;
import ddr.example.com.nddrandroidclient.ui.adapter.PathAdapter;
import ddr.example.com.nddrandroidclient.ui.adapter.StringAdapter;
import ddr.example.com.nddrandroidclient.ui.adapter.TargetPointAdapter;
import ddr.example.com.nddrandroidclient.ui.adapter.TaskAdapter;
import ddr.example.com.nddrandroidclient.ui.dialog.InputDialog;
import ddr.example.com.nddrandroidclient.ui.dialog.WaitDialog;
import ddr.example.com.nddrandroidclient.widget.edit.DDREditText;
import ddr.example.com.nddrandroidclient.widget.edit.RegexEditText;
import ddr.example.com.nddrandroidclient.widget.textview.DDRTextView;
import ddr.example.com.nddrandroidclient.widget.view.CustomPopuWindow;
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
    @BindView(R.id.tv_25m)
    TextView tv25m;
    @BindView(R.id.tv_5m)
    TextView tv5m;
    @BindView(R.id.tv_1m)
    TextView tv1m;
    @BindView(R.id.tv_2m)
    TextView tv2m;
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
    /**path路径的子项查看和再编辑*/
    @BindView(R.id.path_detail_layout)
    RelativeLayout pathDetailLayout;
    @BindView(R.id.et_path_name)
    RegexEditText etPathName;
    @BindView(R.id.spinner_mode)
    DDRTextView tv_Spinner;        // 模式的下拉框触发
    @BindView(R.id.et_parameter)
    DDREditText etParameter;
    @BindView(R.id.et_speed)
    DDREditText etSpeed;
    @BindView(R.id.action_recycler)
    RecyclerView actionRecycler;
    @BindView(R.id.tv_config)
    TextView tvConfig;
    /*** task的子项查看和再编辑布局*/
    @BindView(R.id.task_detail_layout)
    RelativeLayout taskDetailLayout;
    @BindView(R.id.et_task_name)
    EditText etTaskName;
    @BindView(R.id.bt_select)
    Button btSelect;
    @BindView(R.id.bt_sort)
    TextView btSort;
    @BindView(R.id.select_point_Recycler)
    RecyclerView selectPointRecycler;
    @BindView(R.id.select_path_Recycler)
    RecyclerView selectPathRecycler;
    @BindView(R.id.select_layout)
    RelativeLayout selectLayout;
    @BindView(R.id.sort_Recycler)
    RecyclerView sortRecycler;


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
        mapAdapter.setNewData(mapInfos);
        onItemClick();
        onTargetItemClick();
        onPathItemClick();
        onActionItemClick();
        onTaskItemClick();
        onSelectedItemClick();
        /*************************路径模式***************************/
        modeList=new ArrayList<>();
        map=tv_Spinner.getMap();
        modeList.add(map.get(64));
        modeList.add(map.get(65));
        modeList.add(map.get(66));
        stringAdapter=new StringAdapter(R.layout.item_path_mode,modeList);
        onModeItemClick();
        /*************************************************************/

    }

    @OnClick({R.id.bt_create_map, R.id.iv_back, R.id.tv_target_point, R.id.tv_add_new, R.id.tv_delete,R.id.bt_batch_delete, R.id.save_point, R.id.tv_path,
            R.id.spinner_mode, R.id.save_path, R.id.tv_task,R.id.bt_select,R.id.bt_sort,R.id.save_task,R.id.tv_edit_map})
    public void onViewClicked(View view)  {
        switch (view.getId()) {
            case R.id.bt_create_map:
                new InputDialog.Builder(getAttachActivity())
                        .setTitle("采集地图")
                        .setHint("输入地图名称")
                        .setListener(new InputDialog.OnListener() {
                            @Override
                            public void onConfirm(BaseDialog dialog, String content) {
                                if (!content.isEmpty()){
                                    String name="OneRoute_"+content;
                                    BaseCmd.reqCmdStartActionMode reqCmdStartActionMode=BaseCmd.reqCmdStartActionMode.newBuilder()
                                            .setMode(BaseCmd.eCmdActionMode.eRec)
                                            .setRouteName(ByteString.copyFromUtf8(name))
                                            .build();
                                    tcpClient.sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eLSMSlamNavigation),reqCmdStartActionMode);
                                    startActivity(CollectingActivity.class);
                                }else {
                                    toast("请输入地图名字");
                                }
                            }
                            @Override
                            public void onCancel(BaseDialog dialog) {

                            }
                        }).show();
                break;
            case R.id.bt_batch_delete:
                if (isShowSelected) {
                    isShowSelected = false;
                    mapAdapter.showSelected(false);
                } else {
                    isShowSelected = true;
                    mapAdapter.showSelected(true);
                }
                break;
            case R.id.iv_back:
                mapDetailLayout.setVisibility(View.GONE);
                mapLayout.setVisibility(View.VISIBLE);
                mPosition=0;
                try {
                    tcpClient.saveDataToServer(mapFileStatus.getReqDDRVLNMapEx(),targetPoints,pathLines,taskModes);
                    Logger.e("----------:"+taskModes.size());
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
                break;
            case R.id.tv_target_point:
                mPosition=0;
                if (pointDetailLayout.getVisibility() == View.GONE) {
                    leftDetailLayout.setVisibility(View.VISIBLE);
                    pointDetailLayout.setVisibility(View.VISIBLE);
                    pathDetailLayout.setVisibility(View.GONE);
                    taskDetailLayout.setVisibility(View.GONE);
                    recyclerDetail.setAdapter(targetPointAdapter);
                    targetPointAdapter.setNewData(targetPoints);
                    if (targetPoints.size() > 0) {
                        etPointName.setText(targetPoints.get(mPosition).getName());
                        etX.setText(targetPoints.get(mPosition).getX());
                        etY.setText(targetPoints.get(mPosition).getY());
                        etToward.setText(targetPoints.get(mPosition).getTheta());
                    }
                } else {
                    leftDetailLayout.setVisibility(View.GONE);
                    pointDetailLayout.setVisibility(View.GONE);
                }
                break;
            case R.id.save_point:          // 保存已修改的目标点，并发送到服务端
                targetPoints.get(mPosition).setName(etPointName.getText().toString());
                targetPoints.get(mPosition).setX(etX.getFloatText());
                targetPoints.get(mPosition).setY(etY.getFloatText());
                targetPoints.get(mPosition).setTheta(etToward.getFloatText());
                targetPointAdapter.setNewData(targetPoints);
                break;
            case R.id.tv_path:
                mPosition=0;
                if (pathDetailLayout.getVisibility() == View.GONE) {        //如果路径编辑部分不可见
                    leftDetailLayout.setVisibility(View.VISIBLE);
                    pointDetailLayout.setVisibility(View.GONE);
                    taskDetailLayout.setVisibility(View.GONE);
                    pathDetailLayout.setVisibility(View.VISIBLE);
                    recyclerDetail.setAdapter(pathAdapter);
                    pathAdapter.setNewData(pathLines);
                    if (pathLines.size() > 0) {
                        etPathName.setText(pathLines.get(0).getName());
                        tv_Spinner.setValueText(pathLines.get(0).getPathModel());
                        etSpeed.setText(pathLines.get(0).getVelocity());
                        tvConfig.setText(pathLines.get(0).getConfig());
                        actionAdapter.setNewData(pathLines.get(0).getPathPoints());
                    }
                } else {
                    leftDetailLayout.setVisibility(View.GONE);
                    pathDetailLayout.setVisibility(View.GONE);
                }
                break;
            case R.id.spinner_mode:
                showPathModePopupWindow(tv_Spinner);
                break;
            case R.id.save_path:
                pathLines.get(mPosition).setName(etPathName.getText().toString());
                pathLines.get(mPosition).setVelocity(etSpeed.getFloatText());
                pathLines.get(mPosition).setPathModel(tv_Spinner.getTextVaule());
                pathAdapter.setNewData(pathLines);
                break;
            case R.id.tv_task:
                mPosition=0;
                if (taskDetailLayout.getVisibility()==View.GONE){
                    leftDetailLayout.setVisibility(View.VISIBLE);
                    pointDetailLayout.setVisibility(View.GONE);
                    taskDetailLayout.setVisibility(View.VISIBLE);
                    pathDetailLayout.setVisibility(View.GONE);
                    //选择按键
                    btSelect.setBackgroundResource(R.drawable.button_shape_blue);
                    //排序按键
                    btSort.setBackgroundResource(R.drawable.bt_bg__map);
                    selectLayout.setVisibility(View.VISIBLE);
                    sortRecycler.setVisibility(View.INVISIBLE);
                    recyclerDetail.setAdapter(taskAdapter);
                    taskAdapter.setNewData(taskModes);
                    if (taskModes.size()>0){
                        etTaskName.setText(taskModes.get(0).getName());
                        try {
                            initSelectRecycler(0);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }else {
                    leftDetailLayout.setVisibility(View.GONE);
                    taskDetailLayout.setVisibility(View.GONE);
                }
               break;
            case R.id.bt_select:
                btSelect.setBackgroundResource(R.drawable.button_shape_blue);
                btSort.setBackgroundResource(R.drawable.bt_bg__map);
                selectLayout.setVisibility(View.VISIBLE);
                sortRecycler.setVisibility(View.INVISIBLE);
                break;
            case R.id.bt_sort:
                btSelect.setBackgroundResource(R.drawable.bt_bg__map);
                btSort.setBackgroundResource(R.drawable.button_shape_blue);
                selectLayout.setVisibility(View.GONE);
                sortRecycler.setVisibility(View.VISIBLE);
                if (taskModes.size()>0){
                    sortAdapter.setNewData(taskModes.get(mPosition).getBaseModes());
                }
                break;
            case R.id.save_task:
                taskModes.get(mPosition).setName(etTaskName.getText().toString());
                taskAdapter.setNewData(taskModes);
                break;
            case R.id.tv_add_new:
                if (pointDetailLayout.getVisibility()==View.VISIBLE){
                    startActivity(MapEditActivity.class);
                    EventBus.getDefault().postSticky(new MessageEvent(MessageEvent.Type.addNewPoint,lookBitmap));
                }else if (pathDetailLayout.getVisibility()==View.VISIBLE){
                    startActivity(MapEditActivity.class);
                    EventBus.getDefault().postSticky(new MessageEvent(MessageEvent.Type.addNewPath,lookBitmap));
                }else if (taskDetailLayout.getVisibility()==View.VISIBLE){
                    new InputDialog.Builder(getAttachActivity())
                            .setTitle("任务名")
                            .setHint("请输入名字")
                            .setListener(new InputDialog.OnListener() {
                                @Override
                                public void onConfirm(BaseDialog dialog, String content) {
                                    if (!content.isEmpty()){
                                        //选择按键
                                        String name="DDRTask_"+content+".task";
                                        btSelect.setBackgroundResource(R.drawable.button_shape_blue);
                                        //排序按键
                                        btSort.setBackgroundResource(R.drawable.bt_bg__map);
                                        selectLayout.setVisibility(View.VISIBLE);
                                        sortRecycler.setVisibility(View.INVISIBLE);
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
                                        taskAdapter.setNewData(taskModes);
                                        mPosition=0;
                                        if (taskModes.size()>0){
                                            etTaskName.setText(taskModes.get(0).getName());
                                            try {
                                                initSelectRecycler(0);
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            } catch (ClassNotFoundException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                }
                                @Override
                                public void onCancel(BaseDialog dialog) {

                                }
                            }).show();
                }
                break;
            case R.id.tv_delete:
                if (pointDetailLayout.getVisibility()==View.VISIBLE){
                    targetPoints.remove(mPosition);
                    targetPointAdapter.setNewData(targetPoints);
                }else if (pathDetailLayout.getVisibility()==View.VISIBLE){
                    pathLines.remove(mPosition);
                    pathAdapter.setNewData(pathLines);
                }else if (taskDetailLayout.getVisibility()==View.VISIBLE){
                    taskModes.remove(mPosition);
                    taskAdapter.setNewData(taskModes);
                }
                break;
            case R.id.tv_edit_map:
                startActivity(MapEditActivity.class);
                EventBus.getDefault().postSticky(new MessageEvent(MessageEvent.Type.editMap,lookBitmap));
                break;
        }
    }

    private BaseDialog dialog;
    private Bitmap lookBitmap;

    /**
     * 地图Recycler的点击事件
     */
    public void onItemClick() {
        mapAdapter.setOnItemClickListener(((adapter, view, position) -> {
            Logger.e("--------:" + mapInfos.get(position).getMapName());
            if (isShowSelected) {
                MapInfo mapInfo = mapInfos.get(position);
                if (mapInfo.isSelected()) {
                    mapInfo.setSelected(false);
                } else {
                    mapInfo.setSelected(true);
                }
                mapAdapter.setData(position, mapInfo);
            } else {
                mapName=mapInfos.get(position).getMapName();
                tcpClient.getMapInfo(ByteString.copyFromUtf8(mapName));
                dialog = new WaitDialog.Builder(getAttachActivity())
                        .setMessage("加载地图信息中")
                        .show();
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(mapInfos.get(position).getBitmap());
                    lookBitmap= BitmapFactory.decodeStream(fis);
                    zoomMap.setImageBitmap(lookBitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
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
                    }

                }, 5000);
            }
        }));
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
            etPathName.setText(pathLines.get(position).getName());
            tv_Spinner.setValueText(pathLines.get(position).getPathModel());
            etSpeed.setText(pathLines.get(position).getVelocity());
            tvConfig.setText(pathLines.get(position).getConfig());
            actionAdapter.setNewData(pathLines.get(position).getPathPoints());
            //LineView.getInstance(getAttachActivity()).setLineViews();
        });
    }

    /**
     * 动作Recycler的点击事件
     */
    public void onActionItemClick() {
        actionAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            switch (view.getId()) {
                case R.id.tv_delete:
                    Logger.e("------点击删除该点动作");
                    break;
                case R.id.tv_action_type:
                    Logger.e("-----点击修改动作");
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
            etTaskName.setText(taskModes.get(position).getName());
            try {
                initSelectRecycler(position);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
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
        });
    }

    /**
     * 排序Recycler的点击事件
     */
    private void onSortItemClick(){
        sortAdapter.setOnItemChildClickListener((adapter, view, position) -> {

        });
    }

    /************************路径选择路径模式的弹窗************************/
    private CustomPopuWindow customPopuWindow;
    private RecyclerView pathModeRecycler;
    private StringAdapter stringAdapter;
    private List<String> modeList;
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
        }
        for (int i = 0; i < infoList.size(); i++) {
            Logger.e("------" + infoList.get(i).getTime());
        }
        mapInfos = infoList;
    }




    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void update(MessageEvent messageEvent)  {
        switch (messageEvent.getType()) {
            case updateDDRVLNMap:
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
                }
                tvTargetPoint.setText("目标点"+"("+targetPoints.size()+")");
                tvPath.setText("路径"+"("+pathLines.size()+")");
                tvTask.setText("任务"+"("+taskModes.size()+")");
                if (dialog.isShowing()) {
                    getAttachActivity().postDelayed(() -> {
                        dialog.dismiss();
                        mapLayout.setVisibility(View.GONE);
                        mapDetailLayout.setVisibility(View.VISIBLE);
                        leftDetailLayout.setVisibility(View.VISIBLE);
                        pointDetailLayout.setVisibility(View.VISIBLE);
                        pathDetailLayout.setVisibility(View.GONE);
                        taskDetailLayout.setVisibility(View.GONE);
                        targetPointAdapter.setNewData(targetPoints);
                        recyclerDetail.setAdapter(targetPointAdapter);
                    }, 800);
                }
                break;
            case updatePoints:
                List<TargetPoint> targetPoints1= (List<TargetPoint>) messageEvent.getData();
                Logger.e("-------接收新建点数据"+targetPoints1.size());
                targetPoints.addAll(targetPoints1);
                Logger.e("-------接收新建点后数据"+targetPoints.size());
                targetPointAdapter.setNewData(targetPoints);
                tvTargetPoint.setText("目标点"+"("+targetPoints.size()+")");
                break;
            case updatePaths:
                List<PathLine> pathLines1= (List<PathLine>) messageEvent.getData();
                pathLines.addAll(pathLines1);
                pathAdapter.setNewData(pathLines);
                tvPath.setText("路径"+"("+pathLines.size()+")");
                break;
            case updateRevamp:
                Logger.e("更新数据");
                tcpClient.getMapInfo(ByteString.copyFromUtf8(mapName));
                break;
        }
    }





}
