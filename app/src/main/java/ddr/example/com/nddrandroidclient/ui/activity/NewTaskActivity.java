package ddr.example.com.nddrandroidclient.ui.activity;

import android.content.Intent;
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
import ddr.example.com.nddrandroidclient.widget.view.LineView;
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


    @OnClick({R.id.iv_back, R.id.tv_finish, R.id.tv_target_point, R.id.tv_path,R.id.tv_task_name})
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
