package ddr.example.com.nddrandroidclient.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.text.InputFilter;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.protobuf.ByteString;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.LitePal;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import DDRCommProto.BaseCmd;
import DDRVLNMapProto.DDRVLNMap;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;
import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.base.BaseDialog;
import ddr.example.com.nddrandroidclient.common.DDRLazyFragment;
import ddr.example.com.nddrandroidclient.common.GlobalParameter;
import ddr.example.com.nddrandroidclient.download.FileUtil;
import ddr.example.com.nddrandroidclient.entity.MessageEvent;
import ddr.example.com.nddrandroidclient.entity.PointType;
import ddr.example.com.nddrandroidclient.entity.info.MapFileStatus;
import ddr.example.com.nddrandroidclient.entity.info.MapInfo;
import ddr.example.com.nddrandroidclient.entity.info.NotifyBaseStatusEx;
import ddr.example.com.nddrandroidclient.entity.point.PathLine;
import ddr.example.com.nddrandroidclient.entity.point.TargetPoint;
import ddr.example.com.nddrandroidclient.entity.point.TaskMode;
import ddr.example.com.nddrandroidclient.helper.ListTool;
import ddr.example.com.nddrandroidclient.other.DpOrPxUtils;
import ddr.example.com.nddrandroidclient.other.InputFilterMinMax;
import ddr.example.com.nddrandroidclient.other.Logger;
import ddr.example.com.nddrandroidclient.protocobuf.CmdSchedule;
import ddr.example.com.nddrandroidclient.protocobuf.dispatcher.ClientMessageDispatcher;
import ddr.example.com.nddrandroidclient.socket.TcpClient;
import ddr.example.com.nddrandroidclient.ui.activity.CollectingActivity;
import ddr.example.com.nddrandroidclient.ui.activity.HomeActivity;
import ddr.example.com.nddrandroidclient.ui.activity.MapEditActivity;
import ddr.example.com.nddrandroidclient.ui.activity.MapSettingActivity;
import ddr.example.com.nddrandroidclient.ui.adapter.ActionAdapter;
import ddr.example.com.nddrandroidclient.ui.adapter.MapAdapter;
import ddr.example.com.nddrandroidclient.ui.adapter.NGridLayoutManager;
import ddr.example.com.nddrandroidclient.ui.adapter.NLinearLayoutManager;
import ddr.example.com.nddrandroidclient.ui.adapter.PathAdapter;
import ddr.example.com.nddrandroidclient.ui.adapter.StringAdapter;
import ddr.example.com.nddrandroidclient.ui.adapter.TargetPointAdapter;
import ddr.example.com.nddrandroidclient.ui.dialog.InputDialog;
import ddr.example.com.nddrandroidclient.ui.dialog.SelectDialog;
import ddr.example.com.nddrandroidclient.ui.dialog.WaitDialog;
import ddr.example.com.nddrandroidclient.widget.edit.DDREditText;
import ddr.example.com.nddrandroidclient.widget.edit.LimitInputTextWatcher;
import ddr.example.com.nddrandroidclient.widget.edit.MyEditTextChangeListener;
import ddr.example.com.nddrandroidclient.widget.edit.RegexEditText;
import ddr.example.com.nddrandroidclient.widget.textview.DDRTextView;
import ddr.example.com.nddrandroidclient.widget.textview.GridTextView;
import ddr.example.com.nddrandroidclient.widget.view.CustomPopuWindow;
import ddr.example.com.nddrandroidclient.widget.view.GridLayerView;
import ddr.example.com.nddrandroidclient.widget.view.LineView;
import ddr.example.com.nddrandroidclient.widget.view.PointView;
import ddr.example.com.nddrandroidclient.widget.zoomview.ZoomImageView;

/**
 * time: 2019/10/26
 * desc: 地图管理界面
 */
public class MapFragment extends DDRLazyFragment<HomeActivity> {
    @BindView(R.id.bt_create_map)
    TextView btCreateMap;
    @BindView(R.id.bt_batch_delete)
    TextView btBatch;   //批量管理
    @BindView(R.id.tv_delete_all)
    TextView tvDeleteAll; //批量删除
    @BindView(R.id.tv_back_batch)
    TextView tvBackBatch;              //退出批量管理状态
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
    @BindView(R.id.tv_reference)
    TextView tvReference;
    @BindView(R.id.tv_set_charge)
    TextView tvSetCharge;            //设置充电点

    /**
     * 目标点的子项查看和再编辑布局
     */
    @BindView(R.id.point_detail_layout)
    RelativeLayout pointDetailLayout;
    @BindView(R.id.et_point_name)
    RegexEditText etPointName;
    @BindView(R.id.tv_revamp_charge)
    TextView tvRevampCharge;           //修改充电点
    @BindView(R.id.et_x)
    DDREditText etX;
    @BindView(R.id.et_y)
    DDREditText etY;
    @BindView(R.id.et_toward)
    DDREditText etToward;
    @BindView(R.id.layout_edit)
    RelativeLayout layoutEdit;
    @BindView(R.id.revamp_point)
    TextView revampPoint;             //修改点
    /**
     * path路径的子项查看和再编辑
     */
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
    @BindView(R.id.tv_action_recycler)
    TextView tvActionRecycler;
    @BindView(R.id.bt_add_action)
    TextView tvAddAction;
    @BindView(R.id.action_recycler)
    RecyclerView actionRecycler;
    @BindView(R.id.tv_config)
    TextView tvConfig;


    @BindView(R.id.right_map_layout)
    RelativeLayout rightMapLayout;
    @BindView(R.id.save_point)
    TextView savePoint;
    @BindView(R.id.save_path)
    TextView savePath;


    private List<TargetPoint> targetPoints = new ArrayList<>();                    // 解析后的目标点列表
    private List<PathLine> pathLines = new ArrayList<>();                         //解析后的路径列表
    private List<TaskMode> taskModes = new ArrayList<>();                           //解析后的任务列表
    private MapFileStatus mapFileStatus;
    private MapAdapter mapAdapter;        //地图列表适配器
    private List<MapInfo> mapInfos = new ArrayList<>(); //地图列表
    private TcpClient tcpClient;
    private boolean isShowSelected;   //是否进入批量管理的状态

    private TargetPointAdapter targetPointAdapter;            //目标点列表适配器 ;用于选择的目标点列表
    private PathAdapter pathAdapter;                         //路径列表适配器 ;用于选择的路径列表
    private ActionAdapter actionAdapter;                     // 动作点列表
    private String mapName;                                  //点击查看的地图名
    private int mPosition = 0;                                   //当前显示的是哪个子项数据 （目标点列表、路径列表、任务列表）
    private String taskName;                                     //任务编辑框中的任务名
    private BaseDialog inputDialog;                              //地图命名窗口

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
        NGridLayoutManager gridLayoutManager = new NGridLayoutManager(getAttachActivity(), 4);
        mapRecycler.setLayoutManager(gridLayoutManager);
        mapRecycler.setAdapter(mapAdapter);
        //目标点(路径或任务)的列表初始化
        NLinearLayoutManager linearLayoutManager = new NLinearLayoutManager(getAttachActivity());
        recyclerDetail.setLayoutManager(linearLayoutManager);
        targetPointAdapter = new TargetPointAdapter(R.layout.item_target_point);
        pathAdapter = new PathAdapter(R.layout.item_target_point);
        actionAdapter = new ActionAdapter(R.layout.item_path_action);
        recyclerDetail.setAdapter(targetPointAdapter);
        NLinearLayoutManager linearLayoutManager1 = new NLinearLayoutManager(getAttachActivity());
        actionRecycler.setLayoutManager(linearLayoutManager1);
        actionRecycler.setAdapter(actionAdapter);     //给动作Recycler设置适配器


    }

    @Override
    protected void initData() {
        tcpClient = TcpClient.getInstance(getContext(), ClientMessageDispatcher.getInstance());
        mapFileStatus = MapFileStatus.getInstance();
        List<String> downloadMapNames = mapFileStatus.getMapNames();
        checkFilesAllName(downloadMapNames);
        mapInfos=mapFileStatus.getMapInfos();
        mapAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_LEFT);
        mapAdapter.setNewData(mapInfos);
        onItemClick();
        onTargetItemClick();
        onPathItemClick();
        onActionItemClick();

        /************************路径设置***************************/
        map = tv_Spinner.getMap();
        actionList = new ArrayList<>();
        actionList.add(map.get(7));
        modeList = new ArrayList<>();
        modeList.add(map.get(64));
        modeList.add(map.get(65));
        /*modeList.add(map.get(66));*/
        stringAdapter = new StringAdapter(R.layout.item_path_mode, modeList);
        onModeItemClick();
        /*************************************************************/
        etToward.setViewType(1);
        etSpeed.setEt_content(3);

        etX.getEt_content().setFilters(new InputFilter[]{new InputFilterMinMax("-999.99", "999.99")});
        etY.getEt_content().setFilters(new InputFilter[]{new InputFilterMinMax("-999.99", "999.99")});
        etSpeed.getEt_content().setFilters(new InputFilter[]{new InputFilterMinMax("0", "1.0")});
        etToward.getEt_content().setFilters(new InputFilter[]{new InputFilterMinMax("-180", "180")});

    }

    @SuppressLint("ResourceAsColor")
    @OnClick({R.id.bt_create_map, R.id.iv_back, R.id.tv_target_point, R.id.tv_add_new, R.id.tv_delete, R.id.bt_batch_delete, R.id.tv_delete_all,R.id.tv_back_batch, R.id.save_point,R.id.tv_revamp_charge, R.id.revamp_point, R.id.tv_path,
            R.id.spinner_mode, R.id.bt_add_action, R.id.save_path, R.id.tv_edit_map,R.id.tv_reference,R.id.tv_set_charge})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_create_map:
                if (!isShowSelected){
                    if (notifyBaseStatusEx.getMode()==1){
                        inputDialog=new InputDialog.Builder(getAttachActivity())
                                .setTitle(R.string.collect_map)
                                .setAutoDismiss(false)
                                .setHint(R.string.enter_map_name)
                                .addTextChangedListener(LimitInputTextWatcher.REGEX_NAME)
                                .setListener(new InputDialog.OnListener() {
                                    @Override
                                    public void onConfirm(BaseDialog dialog, String content) {
                                        if (!content.isEmpty()) {
                                            content = content.replaceAll(" ", "");
                                            String name = "OneRoute_" + content;
                                            if (!mapFileStatus.getMapNames().contains(name)) {
                                                LitePal.deleteDatabase("DDRDataBase");
                                                BaseCmd.reqCmdStartActionMode reqCmdStartActionMode = BaseCmd.reqCmdStartActionMode.newBuilder()
                                                        .setMode(BaseCmd.eCmdActionMode.eRec)
                                                        .setRouteName(ByteString.copyFromUtf8(name))
                                                        .build();
                                                tcpClient.sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eModuleServer), reqCmdStartActionMode);
                                                Intent intent = new Intent(getAttachActivity(), CollectingActivity.class);
                                                intent.putExtra("CollectName", name);
                                                startActivity(intent);
                                                inputDialog.dismiss();
                                            } else {
                                                toast(R.string.name_repetition);
                                            }
                                        } else {
                                            toast(R.string.please_anter_name);
                                        }
                                    }
                                    @Override
                                    public void onCancel(BaseDialog dialog) {
                                        inputDialog.dismiss();
                                    }
                                }).show();

                    }else if (NotifyBaseStatusEx.getInstance().getMode() == 2) {
                        tcpClient.getAllLidarMap();
                        Intent intent = new Intent(getAttachActivity(), CollectingActivity.class);
                        startActivity(intent);
                    }else {
                        toast(R.string.not_wait_collect);
                    }
                }else {
                    toast(R.string.batch_collect);
                }
                break;
            case R.id.bt_batch_delete:
                if (isShowSelected) {              //是否显示批量选择
                    btBatch.setBackgroundResource(R.drawable.bt_bg__map);
                    tvDeleteAll.setVisibility(View.GONE);
                    tvBackBatch.setVisibility(View.GONE);
                    isShowSelected = false;
                    for (MapInfo mapInfo:mapInfos){
                        mapInfo.setSelected(false);
                    }
                    mapAdapter.showSelected(false);
                } else {
                    btBatch.setBackgroundResource(R.drawable.button_shape_blue);
                    isShowSelected = true;
                    mapAdapter.showSelected(true);
                    tvDeleteAll.setVisibility(View.VISIBLE);
                    tvBackBatch.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.tv_delete_all:
                new InputDialog.Builder(getActivity())
                        .setTitle(R.string.please_delete)
                        .setEditVisibility(View.GONE)
                        .setListener(new InputDialog.OnListener() {
                            @Override
                            public void onConfirm(BaseDialog dialog, String content) {
                                List<DDRVLNMap.reqMapOperational.OptItem> optItems = new ArrayList<>();;
                                for (int i = 0; i < mapInfos.size(); i++) {
                                    if (mapInfos.get(i).isSelected()) {
                                        DDRVLNMap.reqMapOperational.OptItem optItem = DDRVLNMap.reqMapOperational.OptItem.newBuilder()
                                                .setTypeValue(1)
                                                .setSourceName(ByteString.copyFromUtf8(mapInfos.get(i).getMapName()))
                                                .build();
                                        optItems.add(optItem);
                                        Logger.e("-------要删除的地图名：" + mapInfos.get(i).getMapName());
                                    }
                                }
                                Logger.e("-----要删除的文件数：" + optItems.size());
                                for (MapInfo mapInfo : mapInfos) {
                                    mapInfo.setSelected(false);
                                }
                                btBatch.setBackgroundResource(R.drawable.bt_bg__map);
                                tvDeleteAll.setVisibility(View.GONE);
                                tvBackBatch.setVisibility(View.GONE);
                                isShowSelected = false;
                                mapAdapter.showSelected(false);
                                Logger.e("-----要删除的文件数：" + optItems.size());
                                tcpClient.reqMapOperational(optItems);
                                waitDialog = new WaitDialog.Builder(getAttachActivity())
                                        .setMessage(R.string.common_deleting)
                                        .show();
                            }
                            @Override
                            public void onCancel(BaseDialog dialog) {
                            }
                        }).show();
                break;
            case R.id.tv_back_batch:
                showBatchSelected();
                break;
            case R.id.iv_back:
                backToMapList();
                break;
            case R.id.tv_target_point:
                mPosition = 0;
                if (pointDetailLayout.getVisibility() == View.GONE) {
                    leftDetailLayout.setVisibility(View.VISIBLE);
                    pointDetailLayout.setVisibility(View.VISIBLE);
                    pathDetailLayout.setVisibility(View.GONE);
                    recyclerDetail.setAdapter(targetPointAdapter);
                    for (TargetPoint targetPoint : targetPoints) {
                        targetPoint.setSelected(false);
                    }
                    PointView.getInstance(getAttachActivity()).clearDraw();
                    LineView.getInstance(getAttachActivity()).clearDraw();
                    setIconDefault();
                    tvTargetPoint.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.iv_target_blue), null, null, null);
                    tvTargetPoint.setTextColor(Color.parseColor("#0399ff"));
                    if (targetPoints.size() > 0) {
                        targetPoints.get(mPosition).setSelected(true);
                        targetPointAdapter.setNewData(targetPoints);
                        etPointName.setText(targetPoints.get(mPosition).getName());
                        etX.setText(targetPoints.get(mPosition).getX());
                        etY.setText(targetPoints.get(mPosition).getY());
                        etToward.setText(targetPoints.get(mPosition).getTheta());
                        if (targetPoints.get(mPosition).getName().equals(getString(R.string.initial_point))) {
                            layoutEdit.setVisibility(View.GONE);
                            tvRevampCharge.setVisibility(View.GONE);
                        } else if (targetPoints.get(mPosition).getPointType().equals(PointType.eMarkingTypeCharging)) {
                            layoutEdit.setVisibility(View.GONE);
                            tvRevampCharge.setVisibility(View.VISIBLE);
                        }else {
                            layoutEdit.setVisibility(View.VISIBLE);
                            tvRevampCharge.setVisibility(View.GONE);
                        }
                        etX.et_content.addTextChangedListener(new MyEditTextChangeListener(0, PointView.getInstance(getAttachActivity()), targetPoints.get(mPosition), zoomMap,etX,etY,etToward));
                        etY.et_content.addTextChangedListener(new MyEditTextChangeListener(1, PointView.getInstance(getAttachActivity()), targetPoints.get(mPosition), zoomMap,etX,etY,etToward));
                        etToward.et_content.addTextChangedListener(new MyEditTextChangeListener(2, PointView.getInstance(getAttachActivity()), targetPoints.get(mPosition), zoomMap,etX,etY,etToward));
                        PointView.getInstance(getAttachActivity()).setPoint(targetPoints.get(mPosition));
                        zoomMap.invalidate();
                    }
                } else {
                    setIconDefault();
                    leftDetailLayout.setVisibility(View.GONE);
                    pointDetailLayout.setVisibility(View.GONE);
                }
                break;
            case R.id.tv_set_charge:
            case R.id.tv_revamp_charge:
                Logger.e("点击跳转到MapEditActivity");
                Intent intent1 = new Intent(getAttachActivity(), MapEditActivity.class);
                intent1.putExtra("type", 4);
                intent1.putExtra("bitmapPath", bitmapPath);
                intent1.putExtra("targetList", (Serializable) targetPoints);
                intent1.putExtra("pathList", (Serializable) pathLines);
                startActivity(intent1);
                break;
            case R.id.save_point:          // 保存已修改的目标点，并发送到服务端
                if (mPosition < targetPoints.size()) {
                    targetPoints.get(mPosition).setName(etPointName.getText().toString());
                    targetPoints.get(mPosition).setX(etX.getFloatText());
                    targetPoints.get(mPosition).setY(etY.getFloatText());
                    targetPoints.get(mPosition).setTheta(etToward.getFloatText());
                    targetPointAdapter.setNewData(targetPoints);
                    etX.setText(targetPoints.get(mPosition).getX());
                    etY.setText(targetPoints.get(mPosition).getY());
                    etToward.setText(targetPoints.get(mPosition).getTheta());
                    BaseDialog waitDialog1 = new WaitDialog.Builder(getAttachActivity()).setMessage(R.string.in_storage).show();
                    getAttachActivity().postDelayed(() -> {
                        try {
                            tcpClient.saveDataToServer(mapFileStatus.getReqDDRVLNMapEx(), targetPoints, pathLines, taskModes);
                            Logger.e("----------:" + targetPoints.size());
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                        PointView.getInstance(getContext()).setPoint(targetPoints.get(mPosition));
                        zoomMap.invalidate();
                        waitDialog1.dismiss();
                    }, 500);
                } else {
                    toast(R.string.the_point_inexistence);
                }
                break;
            case R.id.tv_path:
                mPosition = 0;
                if (pathDetailLayout.getVisibility() == View.GONE) {        //如果路径编辑部分不可见
                    setIconDefault();
                    tvPath.setTextColor(Color.parseColor("#0399ff"));
                    tvPath.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.iv_path_blue), null, null, null);
                    leftDetailLayout.setVisibility(View.VISIBLE);
                    pointDetailLayout.setVisibility(View.GONE);
                    pathDetailLayout.setVisibility(View.VISIBLE);
                    recyclerDetail.setAdapter(pathAdapter);
                    for (PathLine pathLine : pathLines) {
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
                        hideActionRecycler(0);
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
                if (pathLines.size() > 0 && mPosition < pathLines.size()) {
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

                } else {
                    toast(R.string.the_path_inexistence);
                }
                break;
            case R.id.save_path:
                if (pathLines.size() > 0 && mPosition < pathLines.size()) {
                    pathLines.get(mPosition).setName(etPathName.getText().toString());
                    pathLines.get(mPosition).setVelocity(etSpeed.getFloatText());
                    pathLines.get(mPosition).setPathModel(tv_Spinner.getTextVaule());
                    pathAdapter.setNewData(pathLines);
                    BaseDialog waitDialog2 = new WaitDialog.Builder(getAttachActivity()).setMessage(R.string.in_storage).show();
                    getAttachActivity().postDelayed(() -> {
                        try {
                            tcpClient.saveDataToServer(mapFileStatus.getReqDDRVLNMapEx(), targetPoints, pathLines, taskModes);
                            Logger.e("----------:" + pathLines.size());
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                        waitDialog2.dismiss();
                    }, 500);
                } else {
                    toast(R.string.the_path_inexistence);
                }
                break;
            case R.id.tv_add_new:
                if (pointDetailLayout.getVisibility() == View.VISIBLE) {
                    PointView.getInstance(getAttachActivity()).clearDraw();
                    Intent intent = new Intent(getAttachActivity(), MapEditActivity.class);
                    intent.putExtra("type", 1);
                    intent.putExtra("bitmapPath", bitmapPath);
                    intent.putExtra("targetList", (Serializable) targetPoints);
                    intent.putExtra("pathList", (Serializable) pathLines);
                    startActivity(intent);
                } else if (pathDetailLayout.getVisibility() == View.VISIBLE) {
                    LineView.getInstance(getAttachActivity()).clearDraw();
                    Intent intent = new Intent(getAttachActivity(), MapEditActivity.class);
                    intent.putExtra("type", 2);
                    intent.putExtra("bitmapPath", bitmapPath);
                    intent.putExtra("targetList", (Serializable) targetPoints);
                    intent.putExtra("pathList", (Serializable) pathLines);
                    startActivity(intent);
                }
                break;
            case R.id.tv_delete:
                new InputDialog.Builder(getActivity())
                        .setTitle(R.string.is_delete)
                        .setEditVisibility(View.GONE)
                        .setListener(new InputDialog.OnListener() {
                            @Override
                            public void onConfirm(BaseDialog dialog, String content) {
                                if (pointDetailLayout.getVisibility() == View.VISIBLE) {                   //删除目标点
                                    if (targetPoints.size() > 0) {
                                        if (mPosition < targetPoints.size()) {
                                            targetPoints.remove(mPosition);
                                            targetPointAdapter.setNewData(targetPoints);
                                            if (targetPoints.size() == 0) {
                                                etPointName.setText(R.string.no_point);
                                                etX.setText(0);
                                                etY.setText(0);
                                                etToward.setText(0);
                                            } else {
                                                etPointName.setText(targetPoints.get(0).getName());
                                                etX.setText(targetPoints.get(0).getX());
                                                etY.setText(targetPoints.get(0).getY());
                                                etToward.setText(targetPoints.get(0).getTheta());
                                            }
                                            tvTargetPoint.setText(getString(R.string.target_point_label) + "(" + targetPoints.size() + ")");
                                            PointView.getInstance(getAttachActivity()).setPoint(null);
                                            zoomMap.invalidate();
                                        } else {
                                            toast(R.string.please_select_point);
                                        }
                                    } else {
                                        toast(R.string.no_point);
                                        etPointName.setText(R.string.no_point);
                                        etX.setText(0);
                                        etY.setText(0);
                                        etToward.setText(0);
                                    }
                                } else if (pathDetailLayout.getVisibility() == View.VISIBLE) {           //删除路径
                                    if (pathLines.size() > 0) {
                                        if (mPosition < pathLines.size()) {
                                            pathLines.remove(mPosition);
                                            pathAdapter.setNewData(pathLines);
                                            if (pathLines.size() == 0) {
                                                etPathName.setText(R.string.no_path);
                                                tv_Spinner.setValueText(0);
                                                etSpeed.setText(0);
                                                tvConfig.setText("");
                                            } else {
                                                etPathName.setText(pathLines.get(0).getName());
                                                tv_Spinner.setValueText(pathLines.get(0).getPathModel());
                                                etSpeed.setText(pathLines.get(0).getVelocity());
                                                tvConfig.setText(pathLines.get(0).getConfig());
                                                actionAdapter.setNewData(selectActionList(pathLines.get(0).getPathPoints()));
                                            }
                                            tvPath.setText(getString(R.string.path_label)+ "(" + pathLines.size() + ")");
                                            LineView.getInstance(getAttachActivity()).setPoints(null);
                                            zoomMap.invalidate();
                                        } else {
                                            toast(R.string.please_select_path);
                                        }
                                    } else {
                                        toast(R.string.no_path);
                                        etPathName.setText(R.string.no_path);
                                        tv_Spinner.setValueText(0);
                                        etSpeed.setText(0);
                                        tvConfig.setText("");
                                    }
                                }
                                tcpClient.saveDataToServer(mapFileStatus.getReqDDRVLNMapEx(), targetPoints, pathLines, taskModes);
                                zoomMap.invalidate();
                            }
                            @Override
                            public void onCancel(BaseDialog dialog) {
                            }
                        }).show();
                break;
            case R.id.tv_edit_map:
                Intent intent = new Intent(getAttachActivity(), MapEditActivity.class);
                intent.putExtra("type", 3);
                intent.putExtra("bitmapPath", bitmapPath);
                startActivity(intent);
                break;
            case R.id.tv_reference:
                showPopupWindowReference(tvReference);
                break;

        }
    }

    /**
     * 把图标状态设置回默认
     */
    @SuppressLint("ResourceAsColor")
    private void setIconDefault() {
        tvTargetPoint.setTextColor(Color.parseColor("#ccffffff"));
        tvTargetPoint.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.iv_target_gray), null, null, null);
        tvPath.setTextColor(Color.parseColor("#ccffffff"));
        tvPath.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.iv_path_gray), null, null, null);
    }


    /**
     * 进入批量删除状态
     */
    private void showBatchSelected(){
        if (isShowSelected) {              //是否显示批量选择
            btBatch.setBackgroundResource(R.drawable.bt_bg__map);
            tvDeleteAll.setVisibility(View.GONE);
            tvBackBatch.setVisibility(View.GONE);
            isShowSelected = false;
            for (MapInfo mapInfo:mapInfos){
                mapInfo.setSelected(false);
            }
            mapAdapter.showSelected(false);
        } else {
            btBatch.setBackgroundResource(R.drawable.button_shape_blue);
            isShowSelected = true;
            for (MapInfo mapInfo:mapInfos){
                mapInfo.setSelected(false);
            }
            mapAdapter.showSelected(true);
            tvDeleteAll.setVisibility(View.VISIBLE);
            tvBackBatch.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 筛选出非默认点的动作列表
     *
     * @param pathPoints
     * @return
     */
    private List<PathLine.PathPoint> selectActionList(List<PathLine.PathPoint> pathPoints) {
        List<PathLine.PathPoint> pathPoints1 = new ArrayList<>();
        for (int i = 0; i < pathPoints.size(); i++) {
            if (pathPoints.get(i).getPointType() != 8) {
                pathPoints1.add(pathPoints.get(i));
            }
        }
        return pathPoints1;
    }

    /**
     * 筛选出默认点列表
     *
     * @param pathPoints
     * @return
     */
    private List<PathLine.PathPoint> defaultActionList(List<PathLine.PathPoint> pathPoints) {
        List<PathLine.PathPoint> pathPoints1 = new ArrayList<>();
        for (int i = 0; i < pathPoints.size(); i++) {
            if (pathPoints.get(i).getPointType() == 8) {
                pathPoints1.add(pathPoints.get(i));
            }
        }
        return pathPoints1;
    }


    private BaseDialog dialog, waitDialog;
    //private Bitmap lookBitmap;
    private String bitmapPath;          // 点击的图片存储地址
    private boolean mapIsUsing = false;

    /**
     * 地图Recycler的点击事件
     */
    public void onItemClick() {
        mapAdapter.setOnItemClickListener(((adapter, view, position) -> {
            Logger.e("--------:" + mapInfos.get(position).getMapName());
            mapIsUsing = mapInfos.get(position).isUsing();
            if (isShowSelected) {
                if (mapInfos.get(position).isUsing()) {
                    toast(R.string.current_map_unable_delete);
                } else {
                    MapInfo mapInfo = mapInfos.get(position);
                    if (mapInfo.isSelected()) {
                        mapInfo.setSelected(false);
                    } else {
                        mapInfo.setSelected(true);
                    }
                    mapAdapter.setData(position, mapInfo);
                }
            } else {
                if (mapInfos.get(position).isUsing()) {               //判断当前点击的地图是否在使用中，是则跳转详情
                    intoMapDetail(position);
                } else {        //如果不再使用中 则弹出弹窗
                    showMapSettingWindow(view.findViewById(R.id.iv_more), position);

                }
            }
        }));
        mapAdapter.setOnItemChildClickListener(((adapter, view, position) -> {
            if (!isShowSelected){
                showMapSettingWindow(view.findViewById(R.id.iv_more), position);
            }
        }));


    }

    private Runnable waitRunnable;           //延迟执行

    /**
     * 进入地图详情页面
     */
    @SuppressLint("SetTextI18n")
    private void intoMapDetail(int position) {
        bitmapPath = mapInfos.get(position).getBitmap();
        mapName = mapInfos.get(position).getMapName();
        tcpClient.getMapInfo(ByteString.copyFromUtf8(mapName));
        dialog = new WaitDialog.Builder(getAttachActivity())
                .setMessage(getString(R.string.common_load) + mapName +getString(R.string.common_loading))
                .show();
        String name = mapInfos.get(position).getMapName();
        name = name.replaceAll("OneRoute_", "");
        tvMapName.setText(getString(R.string.map_name) + name);
        tvMapSize.setText(getString(R.string.map_size) + (int) mapInfos.get(position).getWidth() + "x" + (int) mapInfos.get(position).getHeight() + "m²");
        tvCreateTime.setText(getString(R.string.create_time) + mapInfos.get(position).getTime());
        waitRunnable=new Runnable() {
            @Override
            public void run() {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                    toast(R.string.load_failed);
                    mapLayout.setVisibility(View.GONE);
                    mapDetailLayout.setVisibility(View.VISIBLE);
                    leftDetailLayout.setVisibility(View.VISIBLE);
                    pointDetailLayout.setVisibility(View.VISIBLE);
                    pathDetailLayout.setVisibility(View.GONE);
                    zoomMap.setImageBitmap(bitmapPath);
                }
            }
        };
        getAttachActivity().postDelayed(waitRunnable,7000);

    }



    private String switchMapName, switchBitmapPath;

    /**
     * 点击地图右上角设置弹出弹窗
     */
    private void showMapSettingWindow(View view, int position) {
        mapIsUsing = mapInfos.get(position).isUsing();
        View contentView = LayoutInflater.from(getAttachActivity()).inflate(R.layout.map_management_window, null);
        CustomPopuWindow customPopuWindow = new CustomPopuWindow.PopupWindowBuilder(getAttachActivity())
                .setView(contentView)
                .create()
                .showAsDropDown(view, -DpOrPxUtils.dip2px(getAttachActivity(), 100), -10);
        View.OnClickListener listener = v -> {
            customPopuWindow.dissmiss();
            switch (v.getId()) {
                case R.id.tv_switch:
                    //地图是否使用
                   String message=(mapIsUsing?getString(R.string.is_relocation):getString(R.string.is_switch_map));
                    new InputDialog.Builder(getActivity())
                            .setTitle(message)
                            .setEditVisibility(View.GONE)
                            .setListener(new InputDialog.OnListener() {
                                @Override
                                public void onConfirm(BaseDialog dialog, String content) {
                                    switchMapName = mapInfos.get(position).getMapName();
                                    switchBitmapPath = mapInfos.get(position).getBitmap();
                                    Logger.e("-----把地图切换到：" + switchMapName);
                                    tcpClient.exitModel();
                                    if (!mapIsUsing){
                                        tcpClient.reqRunControlEx(switchMapName);
                                        waitDialog = new WaitDialog.Builder(getAttachActivity())
                                                .setMessage(R.string.in_switch)
                                                .show();
                                        getAttachActivity().postDelayed(() -> {
                                            if (waitDialog.isShowing()) {
                                                waitDialog.dismiss();
                                                toast(R.string.switch_failed);
                                            }
                                        }, 5000);
                                    }else {
                                        reqCmdRelocation();     //发送重定位命令
                                    }
                                }
                                @Override
                                public void onCancel(BaseDialog dialog) {
                                }
                            }).show();
                    break;
                case R.id.tv_detail:
                    intoMapDetail(position);
                    break;
                case R.id.tv_setting:                     //进入地图管理界面
                    mapName = mapInfos.get(position).getMapName();
                    Intent intent=new Intent(getAttachActivity(),MapSettingActivity.class);
                    intent.putExtra("mapName",mapName);
                    startActivity(intent);
                    break;
            }
        };

        TextView tvSwitch = contentView.findViewById(R.id.tv_switch);
        tvSwitch.setOnClickListener(listener);
        contentView.findViewById(R.id.tv_detail).setOnClickListener(listener);
        contentView.findViewById(R.id.tv_setting).setOnClickListener(listener);
        if (mapIsUsing) {
            tvSwitch.setText(R.string.common_relocation);
            tvSwitch.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.iv_relocation), null, null, null);
            switchMapName = mapInfos.get(position).getMapName();
            switchBitmapPath = mapInfos.get(position).getBitmap();
        } else {
            tvSwitch.setText(R.string.common_switch_map);
            tvSwitch.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.iv_switch), null, null, null);
        }
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
        BaseCmd.reqCmdReloc reqCmdReloc=BaseCmd.reqCmdReloc.newBuilder()
                .setTypeValue(0)
                .build();
        tcpClient.sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eModuleServer),reqCmdReloc);
    }

    /**
     * 判断是否显示贴边距离
     *
     * @param mode
     */
    private void initCS(int mode) {
        if (mode == 66) {
            tv_cs.setVisibility(View.VISIBLE);
            tv_cm.setVisibility(View.VISIBLE);
            etParameter.setVisibility(View.VISIBLE);
        } else {
            tv_cs.setVisibility(View.GONE);
            tv_cm.setVisibility(View.GONE);
            etParameter.setVisibility(View.GONE);
        }
    }

    /**
     * 目标点Recycler的点击事件
     */
    private void onTargetItemClick() {
        targetPointAdapter.setOnItemClickListener((adapter, view, position) -> {
            etPointName.setText(targetPoints.get(position).getName());
            etX.setText(targetPoints.get(position).getX());
            etY.setText(targetPoints.get(position).getY());
            etToward.setText(targetPoints.get(position).getTheta());
            mPosition = position;
            if (targetPoints.get(mPosition).getName().equals(getString(R.string.initial_point))) {
                layoutEdit.setVisibility(View.GONE);
                tvRevampCharge.setVisibility(View.GONE);
            } else if (targetPoints.get(mPosition).getPointType().equals(PointType.eMarkingTypeCharging)) {
                layoutEdit.setVisibility(View.GONE);
                tvRevampCharge.setVisibility(View.VISIBLE);
            }else {
                layoutEdit.setVisibility(View.VISIBLE);
                tvRevampCharge.setVisibility(View.GONE);
            }
            etX.et_content.addTextChangedListener(new MyEditTextChangeListener(0, PointView.getInstance(getAttachActivity()), targetPoints.get(mPosition), zoomMap,etX,etY,etToward));
            etY.et_content.addTextChangedListener(new MyEditTextChangeListener(1, PointView.getInstance(getAttachActivity()), targetPoints.get(mPosition), zoomMap,etX,etY,etToward));
            etToward.et_content.addTextChangedListener(new MyEditTextChangeListener(2, PointView.getInstance(getAttachActivity()), targetPoints.get(mPosition), zoomMap,etX,etY,etToward));
            for (TargetPoint targetPoint : targetPoints) {
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
    private void onPathItemClick() {
        pathAdapter.setOnItemClickListener((adapter, view, position) -> {
            mPosition = position;
            for (PathLine pathLine : pathLines) {
                pathLine.setSelected(false);
            }
            pathLines.get(position).setSelected(true);
            etPathName.setText(pathLines.get(position).getName());
            int mode = pathLines.get(position).getPathModel();
            tv_Spinner.setValueText(mode);
            initCS(mode);
            etSpeed.setText(pathLines.get(position).getVelocity());
            tvConfig.setText(pathLines.get(position).getConfig());
            actionAdapter.setNewData(selectActionList(pathLines.get(position).getPathPoints()));
            pathAdapter.setNewData(pathLines);
            hideActionRecycler(position);
            LineView.getInstance(getAttachActivity()).clearDraw();
            PointView.getInstance(getAttachActivity()).clearDraw();
            LineView.getInstance(getAttachActivity()).setPoints(pathLines.get(position).getPathPoints());
            zoomMap.invalidate();
        });
    }

    /**
     *隐藏建图路径时的动作点列表
     */
    private void hideActionRecycler(int position){
        if (pathLines.get(position).getName().equals("建图路径")){
            tvActionRecycler.setVisibility(View.INVISIBLE);
            tvAddAction.setVisibility(View.INVISIBLE);
            actionRecycler.setVisibility(View.INVISIBLE);
        }else {
            tvActionRecycler.setVisibility(View.VISIBLE);
            tvAddAction.setVisibility(View.VISIBLE);
            actionRecycler.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 动作Recycler的点击事件
     */
    private void onActionItemClick() {
        actionAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            Logger.e("------点击：" + position);
            int position1 = position;
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


    /************************路径选择路径模式的弹窗************************/
    private CustomPopuWindow customPopuWindow;
    private RecyclerView pathModeRecycler;
    private StringAdapter stringAdapter;
    private List<String> modeList;
    private List<String> actionList;
    private Map<Integer, String> map;

    private void showPathModePopupWindow(View view) {
        View contentView = LayoutInflater.from(getAttachActivity()).inflate(R.layout.window_path_mode, null);
        customPopuWindow = new CustomPopuWindow.PopupWindowBuilder(getAttachActivity())
                .setView(contentView)
                .create()
                .showAsDropDown(view, 0, 5);
        pathModeRecycler = contentView.findViewById(R.id.path_mode_recycler);
        NLinearLayoutManager layoutManager = new NLinearLayoutManager(getAttachActivity());
        pathModeRecycler.setLayoutManager(layoutManager);
        pathModeRecycler.setAdapter(stringAdapter);
    }

    /**
     * 路径模式的Recycler的点击事件
     */
    private void onModeItemClick() {
        stringAdapter.setOnItemClickListener((adapter, view, position) -> {
            tv_Spinner.setText(modeList.get(position));
            initCS(tv_Spinner.getTextVaule());
            customPopuWindow.dissmiss();
        });
    }

    /********************************** start- 显示参考层弹窗******************************/
    private TextView tvGrid,tvTargetPointRe,tvAllPoint,tvPathRe,tvAllPath;
    private LinearLayout layoutGrid,layoutTarget,layoutPath;
    private GridTextView tv025m,tv05m,tv1m,tv2m;
    private RecyclerView recyclerPoints,recyclerPaths;
    private TargetPointAdapter targetReferenceAdapter;
    private PathAdapter pathReferenceAdapter;
    private boolean allShowPoint,allShowPath;               // 是否全部显示
    private int gridStatus;                                // 0 默认 1:0.25m ,2: 0.5m, 3: 1m ,4: 2m
    private void showPopupWindowReference(View view){
        View contentView= LayoutInflater.from(getAttachActivity()).inflate(R.layout.popupwindow_reference,null);
        customPopuWindow=new CustomPopuWindow.PopupWindowBuilder(getAttachActivity())
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
                        GridLayerView.getInstance(zoomMap).setPrecision((float) 0.25);        //将图片网格化
                        zoomMap.invalidate();
                        setIconDefault1();
                        tv025m.setTextColor(Color.parseColor("#FFFFFFFF"));
                        tv025m.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.item_show), null);
                        tv025m.setSelected(true);
                        gridStatus=1;
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
                        tv05m.setTextColor(Color.parseColor("#FFFFFFFF"));
                        tv05m.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.mipmap.item_show),null);
                        gridStatus=2;
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
                        tv1m.setTextColor(Color.parseColor("#FFFFFFFF"));
                        tv1m.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.mipmap.item_show),null);
                        gridStatus=3;
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
                        tv2m.setTextColor(Color.parseColor("#FFFFFFFF"));
                        tv2m.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.mipmap.item_show),null);
                        gridStatus=4;
                    } else {
                        setIconDefault1();
                        GridLayerView.getInstance(zoomMap).setPrecision(0);        //取消网格
                        zoomMap.invalidate();
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
                    PointView.getInstance(getAttachActivity()).setTargetPoints(targetPoints);
                    zoomMap.invalidate();
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
                    LineView.getInstance(getAttachActivity()).setPathLines(pathLines);
                    zoomMap.invalidate();
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
        NLinearLayoutManager layoutManager = new NLinearLayoutManager(getAttachActivity());
        recyclerPoints.setLayoutManager(layoutManager);
        recyclerPoints.setAdapter(targetReferenceAdapter);
        pathReferenceAdapter=new PathAdapter(R.layout.item_show_recycler);
        NLinearLayoutManager layoutManager1 = new NLinearLayoutManager(getAttachActivity());
        recyclerPaths.setLayoutManager(layoutManager1);
        recyclerPaths.setAdapter(pathReferenceAdapter);
        targetReferenceAdapter.setNewData(targetPoints);
        pathReferenceAdapter.setNewData(pathLines);
        targetReferenceAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (targetPoints.get(position).isMultiple()){
                targetPoints.get(position).setMultiple(false);
                PointView.getInstance(getAttachActivity()).setTargetPoints(targetPoints);
                zoomMap.invalidate();
                targetReferenceAdapter.setNewData(targetPoints);
            }else {
                targetPoints.get(position).setMultiple(true);
                PointView.getInstance(getAttachActivity()).setTargetPoints(targetPoints);
                zoomMap.invalidate();
                targetReferenceAdapter.setNewData(targetPoints);
            }
        });

        pathReferenceAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (pathLines.get(position).isMultiple()){
                pathLines.get(position).setMultiple(false);
                LineView.getInstance(getAttachActivity()).setPathLines(pathLines);
                zoomMap.invalidate();
                pathReferenceAdapter.setNewData(pathLines);
            }else {
                pathLines.get(position).setMultiple(true);
                LineView.getInstance(getAttachActivity()).setPathLines(pathLines);
                zoomMap.invalidate();
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


    /**
     * 对比本地文件和服务的文件是否有区别,将服务端不存在的本地文件删除，保持一致性
     *
     * @return
     */
    public void checkFilesAllName(List<String> downloadMapNames) {
        if (downloadMapNames != null) {
            File file = new File(GlobalParameter.ROBOT_FOLDER);
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
        }else {
            // 相当于onpause()方法---失去焦点
            Logger.e("不可见");
            backToMapList();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (tv1m!=null){
            for (TargetPoint targetPoint:targetPoints){
                targetPoint.setMultiple(false);
            }
            for (PathLine pathLine:pathLines){
                pathLine.setMultiple(false);
            }
            setIconDefault1();
            allShowPath=false;
        }
    }

    /**
     * 返回到地图列表页面
     */
    private void backToMapList(){
        if (mapDetailLayout!=null){
            mapDetailLayout.setVisibility(View.GONE);
            mapLayout.setVisibility(View.VISIBLE);
            mPosition = 0;
            PointView.getInstance(getAttachActivity()).clearDraw();
            LineView.getInstance(getAttachActivity()).clearDraw();
            GridLayerView.getInstance(zoomMap).onDestroy();

        }
    }

    private NotifyBaseStatusEx notifyBaseStatusEx = NotifyBaseStatusEx.getInstance();
    @SuppressLint("SetTextI18n")
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void update(MessageEvent messageEvent) {
        switch (messageEvent.getType()) {
            case updateDDRVLNMap:
                if (dialog != null) {
                    if (dialog.isShowing()) {
                        getAttachActivity().cancelDelay(waitRunnable);
                        getAttachActivity().postDelayed(() -> {
                            try {
                                targetPoints = ListTool.deepCopy(mapFileStatus.getTargetPoints());
                                pathLines = ListTool.deepCopy(mapFileStatus.getPathLines());
                                taskModes = ListTool.deepCopy(mapFileStatus.getTaskModes());
                            } catch (IOException | ClassNotFoundException e) {
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
                                if (targetPoints.get(0).getName().equals(getString(R.string.initial_point))) {
                                    layoutEdit.setVisibility(View.GONE);
                                    tvRevampCharge.setVisibility(View.GONE);
                                } else if (targetPoints.get(0).getPointType().equals(PointType.eMarkingTypeCharging)) {
                                    layoutEdit.setVisibility(View.GONE);
                                    tvRevampCharge.setVisibility(View.VISIBLE);
                                }else {
                                    layoutEdit.setVisibility(View.VISIBLE);
                                    tvRevampCharge.setVisibility(View.GONE);
                                }
                            } else {
                                etPointName.setText(R.string.no_point);
                                etX.setText(0);
                                etY.setText(0);
                                etToward.setText(0);
                            }
                            tvTargetPoint.setText(getString(R.string.target_point_label)+ "(" + targetPoints.size() + ")");
                            tvPath.setText(getString(R.string.path_label) + "(" + pathLines.size() + ")");
                            dialog.dismiss();
                            mapLayout.setVisibility(View.GONE);
                            mapDetailLayout.setVisibility(View.VISIBLE);
                            leftDetailLayout.setVisibility(View.VISIBLE);
                            pointDetailLayout.setVisibility(View.VISIBLE);
                            pathDetailLayout.setVisibility(View.GONE);
                            if (haveChargePoint()){
                                tvSetCharge.setVisibility(View.GONE);
                            }else {
                                tvSetCharge.setVisibility(View.VISIBLE);
                            }
                            targetPointAdapter.setNewData(targetPoints);
                            recyclerDetail.setAdapter(targetPointAdapter);
                            setIconDefault();
                            tvTargetPoint.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.iv_target_blue), null, null, null);
                            tvTargetPoint.setTextColor(Color.parseColor("#0399ff"));
                            LineView.getInstance(getAttachActivity()).setSpaceItems(mapFileStatus.getSpaceItems());
                            zoomMap.setImageBitmap(bitmapPath);
                        }, 800);
                    }
                }
                break;
            case updatePoints:
                List<TargetPoint> targetPoints1 = (List<TargetPoint>) messageEvent.getData();
                targetPoints.addAll(targetPoints1);
                for (TargetPoint targetPoint:targetPoints){
                    targetPoint.setMultiple(false);
                }
                targetPointAdapter.setNewData(targetPoints);
                tvTargetPoint.setText(getString(R.string.target_point_label)+ "(" + targetPoints.size() + ")");
                //tcpClient.saveDataToServer(mapFileStatus.getReqDDRVLNMapEx(), targetPoints, pathLines, taskModes);
                break;
            case setChargePoint:
                TargetPoint chargePoint= (TargetPoint) messageEvent.getData();
                addChargePoint(chargePoint);
                targetPointAdapter.setNewData(targetPoints);
                tvTargetPoint.setText(getString(R.string.target_point_label)+ "(" + targetPoints.size() + ")");
                tcpClient.saveDataToServer(mapFileStatus.getReqDDRVLNMapEx(), targetPoints, pathLines, taskModes);
                if (haveChargePoint()){
                    tvSetCharge.setVisibility(View.GONE);
                }
                break;
            case updatePaths:
                List<PathLine> pathLines1 = (List<PathLine>) messageEvent.getData();
                pathLines.addAll(pathLines1);
                for (PathLine pathLine:pathLines){
                    pathLine.setMultiple(false);
                }
                pathAdapter.setNewData(pathLines);
                tvPath.setText(getString(R.string.path_label) + "(" + pathLines.size() + ")");
                //tcpClient.saveDataToServer(mapFileStatus.getReqDDRVLNMapEx(), targetPoints, pathLines, taskModes);
                break;
            case updateVirtualWall:
                zoomMap.setImageBitmap(bitmapPath);
                tcpClient.saveDataToServer(mapFileStatus.getReqDDRVLNMapEx(), targetPoints, pathLines, taskModes);
                break;
            case updateDenSuccess:
                zoomMap.setImageBitmap(bitmapPath);
                break;
            case updateRevamp:
                Logger.e("更新数据");
                break;
            case updateMapList:
                Logger.e("更新地图列表!");
                if (waitDialog != null) {
                    if (waitDialog.isShowing()) {
                        waitDialog.dismiss();
                    }
                }
                getAttachActivity().postDelayed(() -> {
                    mapInfos=mapFileStatus.getMapInfos();
                    mapAdapter.setNewData(mapInfos);
                }, 500);
                break;
            case mapOperationalSucceed:
                if (waitDialog != null) {
                    tcpClient.requestFile();
                    waitDialog.dismiss();
                }
                break;
            case switchMapSucceed:
                Logger.e("切换地图");
                tcpClient.requestFile();
                if (waitDialog != null) {
                    getAttachActivity().postDelayed(() -> {
                        if (waitDialog.isShowing()) {
                            waitDialog.dismiss();
                        }
                    }, 200);
                }
                tcpClient.getMapInfo(ByteString.copyFromUtf8(notifyBaseStatusEx.getCurroute()));
                break;
        }
    }



    /**
     * 添加充电点 一张地图只能有一个充电点
     */
    private void addChargePoint(TargetPoint targetPoint){
        if (targetPoint!=null){
            for (int i=0;i<targetPoints.size();i++){
                if (targetPoints.get(i).getPointType().equals(PointType.eMarkingTypeCharging)){
                    targetPoints.set(i,targetPoint);
                    return;
                }
            }
            targetPoints.add(0,targetPoint);
        }else {
            Logger.e("接收的充電點不存在！");
        }
    }


    /**
     * 是否存在充电点
     * @return
     */
    private boolean haveChargePoint(){
        for (TargetPoint targetPoint:targetPoints){
            if (targetPoint.getPointType().equals(PointType.eMarkingTypeCharging)){
                return true;
            }
        }
        return false;
    }
}
