package ddr.example.com.nddrandroidclient.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback;
import com.chad.library.adapter.base.listener.OnItemDragListener;
import com.google.protobuf.ByteString;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;
import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.base.BaseDialog;
import ddr.example.com.nddrandroidclient.common.DDRActivity;
import ddr.example.com.nddrandroidclient.entity.MessageEvent;
import ddr.example.com.nddrandroidclient.entity.info.MapFileStatus;
import ddr.example.com.nddrandroidclient.entity.info.MapInfo;
import ddr.example.com.nddrandroidclient.entity.info.NotifyBaseStatusEx;
import ddr.example.com.nddrandroidclient.entity.point.BaseMode;
import ddr.example.com.nddrandroidclient.entity.point.PathLine;
import ddr.example.com.nddrandroidclient.entity.point.TargetPoint;
import ddr.example.com.nddrandroidclient.entity.point.TaskMode;
import ddr.example.com.nddrandroidclient.other.Logger;
import ddr.example.com.nddrandroidclient.protocobuf.dispatcher.ClientMessageDispatcher;
import ddr.example.com.nddrandroidclient.socket.TcpClient;
import ddr.example.com.nddrandroidclient.ui.adapter.BaseModeDraggableAdapter;
import ddr.example.com.nddrandroidclient.ui.adapter.NLinearLayoutManager;
import ddr.example.com.nddrandroidclient.ui.adapter.PathAdapter;
import ddr.example.com.nddrandroidclient.ui.adapter.TargetPointAdapter;
import ddr.example.com.nddrandroidclient.ui.dialog.InputDialog;
import ddr.example.com.nddrandroidclient.ui.dialog.WaitDialog;
import ddr.example.com.nddrandroidclient.ui.fragment.TaskFragment;
import ddr.example.com.nddrandroidclient.widget.textview.GridTextView;
import ddr.example.com.nddrandroidclient.widget.view.CustomPopuWindow;
import ddr.example.com.nddrandroidclient.widget.view.GridLayerView;
import ddr.example.com.nddrandroidclient.widget.view.LineView;
import ddr.example.com.nddrandroidclient.widget.view.PointView;
import ddr.example.com.nddrandroidclient.widget.zoomview.ZoomImageView;


/**
 * time: 2020/04/10
 * desc: 新建任务
 */
public class NewTaskActivity extends DDRActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_task_name)
    TextView tvTaskName;
    @BindView(R.id.tv_finish)
    TextView tvFinish;
    @BindView(R.id.tv_target_point)
    TextView tvTargetPoint;
    @BindView(R.id.recycler_target)
    RecyclerView recyclerTarget;
    @BindView(R.id.tv_path)
    TextView tvPath;
    @BindView(R.id.recycler_path)
    RecyclerView recyclerPath;
    @BindView(R.id.layout_select)
    LinearLayout layoutSelect;
    @BindView(R.id.tv_suggest)
    TextView tvSuggest;
    @BindView(R.id.recycle_task_item)
    RecyclerView recycleTaskItem;
    @BindView(R.id.layout_left)
    RelativeLayout layoutLeft;
    @BindView(R.id.zoom_view)
    ZoomImageView zoomView;
    @BindView(R.id.tv_map_name)
    TextView tvMapName;
    @BindView(R.id.tv_map_size)
    TextView tvMapSize;
    @BindView(R.id.tv_create_time)
    TextView tvCreateTime;
    @BindView(R.id.tv_reference)
    TextView tvReference;
    private MapFileStatus mapFileStatus;
    private NotifyBaseStatusEx notifyBaseStatusEx;
    //目标点列表适配器
    private TargetPointAdapter targetPointAdapter;
    //路径列表适配器
    private PathAdapter pathAdapter;
    //任务内容列表适配器（可拖拽）
    private BaseModeDraggableAdapter baseModeDraggableAdapter;
    //目标点列表
    private List<TargetPoint> targetPoints;
    //路径列表
    private List<PathLine> pathLines;
    //单个任务子项列表
    private List<BaseMode> baseModes = new ArrayList<>();
    // 任务列表
    private List<TaskMode> taskModes;

    private TcpClient tcpClient;

    private String taskName;

    //等待弹窗
    private BaseDialog waitDialog;

    private int viewType;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_new_task;
    }

    @Override
    protected void initView() {
        super.initView();
        tcpClient=TcpClient.getInstance(context,ClientMessageDispatcher.getInstance());
        mapFileStatus = MapFileStatus.getInstance();
        notifyBaseStatusEx=NotifyBaseStatusEx.getInstance();
        targetPoints = mapFileStatus.getcTargetPoints();
        pathLines = mapFileStatus.getcPathLines();
        taskModes=mapFileStatus.getcTaskModes();
        targetPointAdapter = new TargetPointAdapter(R.layout.item_select_to_task);
        pathAdapter = new PathAdapter(R.layout.item_select_to_task);
        NLinearLayoutManager layoutManager = new NLinearLayoutManager(this);
        recyclerTarget.setLayoutManager(layoutManager);
        recyclerTarget.setAdapter(targetPointAdapter);
        NLinearLayoutManager linearLayoutManager = new NLinearLayoutManager(this);
        recyclerPath.setLayoutManager(linearLayoutManager);
        recyclerPath.setAdapter(pathAdapter);
        targetPointAdapter.setNewData(targetPoints);
        pathAdapter.setNewData(pathLines);
        //初始化 拖拽适配器
        baseModeDraggableAdapter = new BaseModeDraggableAdapter(R.layout.item_sort_task, baseModes);
        ItemDragAndSwipeCallback itemDragAndSwipeCallback = new ItemDragAndSwipeCallback(baseModeDraggableAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemDragAndSwipeCallback);
        itemTouchHelper.attachToRecyclerView(recycleTaskItem);
        baseModeDraggableAdapter.enableDragItem(itemTouchHelper, R.id.item_sort_task, true);
        baseModeDraggableAdapter.setOnItemDragListener(onItemDragListener);
        NLinearLayoutManager linearLayoutManager0 = new NLinearLayoutManager(this);
        recycleTaskItem.setLayoutManager(linearLayoutManager0);
        recycleTaskItem.setAdapter(baseModeDraggableAdapter);
        onItemClick();


    }

    @Override
    protected void initData() {
        super.initData();
        Intent intent = getIntent();
        viewType =intent.getIntExtra("viewType",0);
        if (viewType==TaskFragment.CREATE_NEW_TASK){
            taskName = intent.getStringExtra("taskName");
        }else if (viewType==TaskFragment.REVAMP_TASK){
            TaskMode taskMode= taskModes.get(intent.getIntExtra("taskMode",0));
            taskName=taskMode.getName();
            baseModes=taskMode.getBaseModes();
            taskName=taskName.replaceAll("DDRTask_","");
            taskName=taskName.replaceAll(".task","");
            baseModeDraggableAdapter.setNewData(baseModes);
            LineView.getInstance(context).setBaseModes(baseModes);
            zoomView.invalidate();
        }
        tvTaskName.setText(taskName);
        for (MapInfo mapInfo:mapFileStatus.getMapInfos()){
            if (mapInfo.isUsing()){
                String name = mapInfo.getMapName();
                name = name.replaceAll("OneRoute_", "");
                tvMapName.setText(getString(R.string.map_name) + name);
                tvMapSize.setText(getString(R.string.map_size) + (int) mapInfo.getWidth() + "x" + (int)mapInfo.getHeight() + "m²");
                tvCreateTime.setText(getString(R.string.create_time) + mapInfo.getTime());
                //设置图片
                zoomView.setImageBitmap(mapInfo.getBitmap());
            }
        }
        if (baseModes.size() > 0) {
            tvSuggest.setText(R.string.create_task_hint_1);
        }
    }


    @OnClick({R.id.iv_back, R.id.tv_finish, R.id.tv_target_point, R.id.tv_path,R.id.tv_task_name,R.id.tv_reference})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_finish:
                if (baseModes.size()>0){
                    String name="DDRTask_" + tvTaskName.getText().toString() + ".task";
                    TaskMode taskMode=new TaskMode();
                    taskMode.setName(name);
                    taskMode.setBaseModes(baseModes);
                    taskMode.setType(2);
                    taskMode.setTaskState(1);
                    taskMode.setRunCounts(999);
                    taskMode.setStartHour(0);
                    taskMode.setStartMin(0);
                    taskMode.setEndHour(24);
                    taskMode.setEndMin(0);
                    taskModes.add(taskMode);
                    tcpClient.saveTaskData(mapFileStatus.getCurrentMapEx(),taskModes);
                    waitDialog=new WaitDialog.Builder(this)
                            .setMessage(R.string.in_storage)
                            .show();
                    postDelayed(()->{
                        if (waitDialog!=null&&waitDialog.isShowing()){
                            waitDialog.dismiss();
                            toast(R.string.save_failed);
                        }
                    },5000);
                }else {
                    toast(R.string.create_task_hint_2);
                }
                break;
            case R.id.tv_task_name:
                showRenameDialog();
                break;
            case R.id.tv_target_point:
                if (recyclerTarget.getVisibility() == View.GONE) {
                    tvTargetPoint.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.iv_down), null);
                    recyclerTarget.setVisibility(View.VISIBLE);
                } else {
                    tvTargetPoint.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.iv_shrink), null);
                    recyclerTarget.setVisibility(View.GONE);
                }
                tvPath.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.iv_shrink), null);
                recyclerPath.setVisibility(View.GONE);
                break;
            case R.id.tv_path:
                if (recyclerPath.getVisibility() == View.GONE) {
                    recyclerPath.setVisibility(View.VISIBLE);
                    tvPath.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.iv_down), null);
                } else {
                    recyclerPath.setVisibility(View.GONE);
                    tvPath.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.iv_shrink), null);
                }
                tvTargetPoint.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.iv_shrink), null);
                recyclerTarget.setVisibility(View.GONE);
                break;
            case R.id.tv_reference:
                showPopupWindowReference(tvReference);
                break;
        }
    }


    /**
     * 防止任务重名
     * @return true 表示任务重名
     */
    private boolean checkTaskName(String taskName){
        for (TaskMode taskMode:taskModes){
            if (taskMode.getName().equals(taskName)){
                Logger.e("------"+taskName+";"+taskMode.getName());
                return true;
            }
        }
        return false;
    }
    /**
     * 列表点击事件
     */
    private void onItemClick() {
        targetPointAdapter.setOnItemClickListener((adapter, view, position) -> {
            Logger.e("------当前位置：" + position);
            TargetPoint targetPoint = targetPoints.get(position);
            targetPoint.setType(2);
            int size=baseModes.size();
            if (size>0&&baseModes.get(size-1).getType()==2){
                TargetPoint targetPoint1= (TargetPoint) baseModes.get(size-1);
                if (targetPoint.getName().equals(targetPoint1.getName())){
                    toast(R.string.create_task_hint_3);
                }else {
                    baseModes.add(targetPoint);
                    baseModeDraggableAdapter.setNewData(baseModes);
                }
            }else {
                baseModes.add(targetPoint);
                baseModeDraggableAdapter.setNewData(baseModes);
            }
            LineView.getInstance(context).setBaseModes(baseModes);
            zoomView.invalidate();
            tvSuggest.setText(R.string.create_task_hint_1);
        });

        pathAdapter.setOnItemClickListener((adapter, view, position) -> {
            PathLine pathLine = pathLines.get(position);
            pathLine.setType(1);
            int size=baseModes.size();
            if (size>0&&baseModes.get(size-1).getType()==1){
                PathLine pathLine1= (PathLine) baseModes.get(size-1);
                if (pathLine.getName().equals(pathLine1.getName())){
                    toast(R.string.create_task_hint_4);
                }else {
                    baseModes.add(pathLine);
                    baseModeDraggableAdapter.setNewData(baseModes);
                }
            }else {
                baseModes.add(pathLine);
                baseModeDraggableAdapter.setNewData(baseModes);
            }
            tvSuggest.setText(R.string.create_task_hint_1);
            LineView.getInstance(context).setBaseModes(baseModes);
            zoomView.invalidate();
        });

        baseModeDraggableAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            Logger.e("点击删除：" + position);
            baseModes.remove(position);
            baseModeDraggableAdapter.setNewData(baseModes);
            if (baseModes.size() == 0) {
                tvSuggest.setText(R.string.create_task_hint);
            }
            LineView.getInstance(context).setBaseModes(baseModes);
            zoomView.invalidate();
        });
    }


    OnItemDragListener onItemDragListener = new OnItemDragListener() {
        @Override
        public void onItemDragStart(RecyclerView.ViewHolder viewHolder, int pos) {
            Logger.e("------开始拖拽的位置：" + pos);
        }

        @Override
        public void onItemDragMoving(RecyclerView.ViewHolder source, int from, RecyclerView.ViewHolder target, int to) {

        }

        @Override
        public void onItemDragEnd(RecyclerView.ViewHolder viewHolder, int pos) {
            Logger.e("------最后的位置：" + pos);
            LineView.getInstance(context).setBaseModes(baseModes);
            zoomView.invalidate();
        }
    };


    private BaseDialog inputDialog;
    /**
     * 重命名任务名弹窗
     */
    private void showRenameDialog(){
       inputDialog= new InputDialog.Builder(this)
                .setTitle(R.string.task_rename)
                .setAutoDismiss(false)
                .setListener(new InputDialog.OnListener() {
                    @Override
                    public void onConfirm(BaseDialog dialog, String content) {
                        String name="DDRTask_" +content+ ".task";
                        if (checkTaskName(name)){
                            toast(R.string.name_already_exists);
                        }else {
                            tvTaskName.setText(content);
                            inputDialog.dismiss();
                        }
                    }

                    @Override
                    public void onCancel(BaseDialog dialog) {
                        toast(R.string.cancel_rename);
                        inputDialog.dismiss();
                    }
                }).show();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        LineView.getInstance(context).clearDraw();
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
        View contentView=getLayoutInflater().from(this).inflate(R.layout.popupwindow_reference,null);
        customPopuWindow=new CustomPopuWindow.PopupWindowBuilder(this)
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
                        GridLayerView.getInstance(zoomView).setPrecision((float) 0.25);        //将图片网格化
                        zoomView.invalidate();
                        setIconDefault1();
                        tv025m.setTextColor(Color.parseColor("#FFFFFFFF"));
                        tv025m.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.item_show), null);
                        tv025m.setSelected(true);
                        gridStatus=1;
                    } else {
                        GridLayerView.getInstance(zoomView).setPrecision(0);        //取消网格
                        zoomView.invalidate();
                        setIconDefault1();
                    }
                    break;
                case R.id.tv_05m:
                    if (!tv05m.getSelected()) {
                        GridLayerView.getInstance(zoomView).setPrecision((float) 0.5);        //将图片网格化
                        zoomView.invalidate();
                        setIconDefault1();
                        tv05m.setSelected(true);
                        tv05m.setTextColor(Color.parseColor("#FFFFFFFF"));
                        tv05m.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.mipmap.item_show),null);
                        gridStatus=2;
                    } else {
                        setIconDefault1();
                        GridLayerView.getInstance(zoomView).setPrecision(0);        //取消网格
                        zoomView.invalidate();
                    }
                    break;
                case R.id.tv_1m:
                    if (!tv1m.getSelected()) {
                        GridLayerView.getInstance(zoomView).setPrecision((float) 1);        //将图片网格化
                        zoomView.invalidate();
                        setIconDefault1();
                        tv1m.setSelected(true);
                        tv1m.setTextColor(Color.parseColor("#FFFFFFFF"));
                        tv1m.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.mipmap.item_show),null);
                        gridStatus=3;
                    } else {
                        setIconDefault1();
                        GridLayerView.getInstance(zoomView).setPrecision(0);        //取消网格
                        zoomView.invalidate();
                    }
                    break;
                case R.id.tv_2m:
                    if (!tv2m.getSelected()) {
                        GridLayerView.getInstance(zoomView).setPrecision((float) 2);        //将图片网格化
                        zoomView.invalidate();
                        setIconDefault1();
                        tv2m.setSelected(true);
                        tv2m.setTextColor(Color.parseColor("#FFFFFFFF"));
                        tv2m.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.mipmap.item_show),null);
                        gridStatus=4;
                    } else {
                        setIconDefault1();
                        GridLayerView.getInstance(zoomView).setPrecision(0);        //取消网格
                        zoomView.invalidate();
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
                    PointView.getInstance(context).setTargetPoints(targetPoints);
                    zoomView.invalidate();
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
                    LineView.getInstance(context).setPathLines(pathLines);
                    zoomView.invalidate();
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
                PointView.getInstance(context).setTargetPoints(targetPoints);
                zoomView.invalidate();
                targetReferenceAdapter.setNewData(targetPoints);
            }else {
                targetPoints.get(position).setMultiple(true);
                PointView.getInstance(context).setTargetPoints(targetPoints);
                zoomView.invalidate();
                targetReferenceAdapter.setNewData(targetPoints);
            }
        });

        pathReferenceAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (pathLines.get(position).isMultiple()){
                pathLines.get(position).setMultiple(false);
                LineView.getInstance(context).setPathLines(pathLines);
                zoomView.invalidate();
                pathReferenceAdapter.setNewData(pathLines);
            }else {
                pathLines.get(position).setMultiple(true);
                LineView.getInstance(context).setPathLines(pathLines);
                zoomView.invalidate();
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

    @Subscribe(threadMode =ThreadMode.MAIN)
    public void update(MessageEvent messageEvent){
        switch (messageEvent.getType()){
            case updateRevamp:
                if (waitDialog!=null){
                    waitDialog.dismiss();
                    toast(R.string.save_succeed);
                    tcpClient.getMapInfo(ByteString.copyFromUtf8(notifyBaseStatusEx.getCurroute()));
                    finish();
                }
                break;
        }
    }




}
