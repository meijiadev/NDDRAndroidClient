package ddr.example.com.nddrandroidclient.ui.fragment;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.protobuf.ByteString;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

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
import ddr.example.com.nddrandroidclient.entity.point.BaseMode;
import ddr.example.com.nddrandroidclient.entity.point.PathLine;
import ddr.example.com.nddrandroidclient.entity.point.TargetPoint;
import ddr.example.com.nddrandroidclient.entity.point.TaskMode;
import ddr.example.com.nddrandroidclient.entity.point.XyEntity;
import ddr.example.com.nddrandroidclient.other.Logger;
import ddr.example.com.nddrandroidclient.protocobuf.dispatcher.ClientMessageDispatcher;
import ddr.example.com.nddrandroidclient.socket.TcpClient;
import ddr.example.com.nddrandroidclient.ui.activity.CollectingActivity;
import ddr.example.com.nddrandroidclient.ui.activity.HomeActivity;
import ddr.example.com.nddrandroidclient.ui.activity.MapEditActivity;
import ddr.example.com.nddrandroidclient.ui.adapter.MapAdapter;
import ddr.example.com.nddrandroidclient.ui.adapter.TargetPointAdapter;
import ddr.example.com.nddrandroidclient.ui.dialog.WaitDialog;
import ddr.example.com.nddrandroidclient.widget.edit.DDREditText;
import ddr.example.com.nddrandroidclient.widget.edit.RegexEditText;
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
    @BindView(R.id.point_detail_layout)
    RelativeLayout pointDetailLayout;
    @BindView(R.id.iv_back)
    ImageView iv_back;
    @BindView(R.id.tv_target_point)
    TextView tvTargetPoint;
    @BindView(R.id.tv_path)
    TextView tvPath;
    @BindView(R.id.tv_task)
    TextView tvTask;
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
    @BindView(R.id.et_point_name)
    RegexEditText etPointName;
    @BindView(R.id.et_x)
    DDREditText etX;
    @BindView(R.id.et_y)
    DDREditText etY;
    @BindView(R.id.et_toward)
    DDREditText etToward;


    private DDRVLNMap.reqDDRVLNMapEx data;
    private DDRVLNMap.DDRMapBaseData baseData;       // 存放基础信息，采集模式结束时就有的东西。
    private DDRVLNMap.affine_mat affine_mat;
    private DDRVLNMap.DDRMapTargetPointData targetPointData;
    private List<DDRVLNMap.targetPtItem> targetPtItems;          // 目标点列表
    private List<DDRVLNMap.path_line_itemEx> pathLineItemExes;  // 路径列表
    private List<DDRVLNMap.task_itemEx> taskItemExes;          //  任务列表
    private List<TargetPoint> targetPoints;  //目标点列表
    private List<PathLine> pathLines;         //路径列表
    private MapFileStatus mapFileStatus;
    private MapAdapter mapAdapter;        //地图列表适配器
    private List<MapInfo> mapInfos = new ArrayList<>(); //地图列表
    private List<String> downloadMapNames = new ArrayList<>();
    private TcpClient tcpClient;
    private boolean isShowSelected;   //显示批量管理的按钮

    private TargetPointAdapter targetPointAdapter;


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
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getAttachActivity());
        recyclerDetail.setLayoutManager(linearLayoutManager);
        targetPointAdapter=new TargetPointAdapter(R.layout.item_target_point);
        recyclerDetail.setAdapter(targetPointAdapter);
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


    }

    @OnClick({R.id.bt_create_map, R.id.iv_back, R.id.tv_target_point, R.id.tv_add_new, R.id.bt_batch_delete})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_create_map:
                startActivity(CollectingActivity.class);
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
                break;
            case R.id.tv_target_point:
                if (leftDetailLayout.getVisibility() == View.GONE) {
                    leftDetailLayout.setVisibility(View.VISIBLE);
                } else {
                    leftDetailLayout.setVisibility(View.GONE);
                }
                break;
            case R.id.tv_path:

                break;
            case R.id.tv_task:

                break;
            case R.id.tv_add_new:
                startActivity(MapEditActivity.class);
                break;
        }
    }

    private BaseDialog dialog;

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
                tcpClient.getMapInfo(ByteString.copyFromUtf8(mapInfos.get(position).getMapName()));
                dialog = new WaitDialog.Builder(getAttachActivity())
                        .setMessage("加载地图信息中")
                        .show();
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(mapInfos.get(position).getBitmap());
                    Bitmap bitmap = BitmapFactory.decodeStream(fis);
                    zoomMap.setImageBitmap(bitmap);
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
                    }

                }, 5000);
            }
        }));
    }

    /**
     * 目标点Recycler的点击事件
     */
    public void onTargetItemClick(){
        targetPointAdapter.setOnItemClickListener((adapter, view, position) -> {

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
    public void update(MessageEvent messageEvent) {
        switch (messageEvent.getType()) {
            case updateDDRVLNMap:
                data=mapFileStatus.getRspGetDDRVLNMapEx().getData();
                targetPtItems=data.getTargetPtdata().getTargetPtList();
                pathLineItemExes=data.getPathSet().getPathLineDataList();
                taskItemExes=data.getTaskSetList();
                targetPoints=new ArrayList<>();
                for (int i=0;i<targetPtItems.size();i++){
                    TargetPoint targetPoint=new TargetPoint();
                    targetPoint.setName(targetPtItems.get(i).getPtName().toStringUtf8());
                    targetPoint.setX(targetPtItems.get(i).getPtData().getX());
                    targetPoint.setY(targetPtItems.get(i).getPtData().getY());
                    targetPoint.setTheta(targetPtItems.get(i).getPtData().getTheta());
                    targetPoints.add(targetPoint);
                }
                for (int i=0;i<pathLineItemExes.size();i++){
                    List<PathLine.PathPoint> pathPoints=new ArrayList<>();
                    List<DDRVLNMap.path_line_itemEx.path_lint_pt_Item> path_lint_pt_items=pathLineItemExes.get(i).getPointSetList();
                    for (int j=0;j<path_lint_pt_items.size();j++){
                        PathLine.PathPoint pathPoint=new PathLine().new PathPoint();
                        pathPoint.setX(path_lint_pt_items.get(j).getPt().getX());
                        pathPoint.setY(path_lint_pt_items.get(j).getPt().getY());
                        pathPoint.setPointType(path_lint_pt_items.get(j).getTypeValue());
                        pathPoint.setRotationAngle(path_lint_pt_items.get(j).getRotationangle());
                        pathPoints.add(pathPoint);
                    }
                    PathLine pathLine=new PathLine();
                    pathLine.setName(pathLineItemExes.get(i).getName().toStringUtf8());
                    pathLine.setPathPoints(pathPoints);
                    pathLine.setPathModel(pathLineItemExes.get(i).getModeValue());
                    pathLine.setVelocity(pathLineItemExes.get(i).getVelocity());
                    pathLines.add(pathLine);
                }

                for (int i=0;i<taskItemExes.size();i++){
                    List<DDRVLNMap.path_elementEx> path_elementExes=taskItemExes.get(i).getPathSetList();
                    List<BaseMode> baseModes=new ArrayList<>();
                    for (int j=0;j<path_elementExes.size();j++){
                        if (path_elementExes.get(j).getTypeValue()==1){
                            PathLine pathLine=new PathLine(1);
                            pathLine.setName(path_elementExes.get(j).getName().toStringUtf8());
                            baseModes.add(pathLine);
                        }else if (path_elementExes.get(j).getTypeValue()==2){
                            TargetPoint targetPoint=new TargetPoint(2);
                            targetPoint.setName(path_elementExes.get(j).getName().toStringUtf8());
                            baseModes.add(targetPoint);
                        }
                    }
                    TaskMode taskMode=new TaskMode();
                    taskMode.setName(taskItemExes.get(i).getName().toStringUtf8());
                    taskMode.setBaseModes(baseModes);
                    taskMode.setRunCounts(taskItemExes.get(i).getRunCount());
                    taskMode.setTimeItem(taskItemExes.get(i).getTimeSet());
                }
                targetPointAdapter.setNewData(targetPoints);
                if (dialog.isShowing()) {
                    getAttachActivity().postDelayed(() -> {
                        dialog.dismiss();
                        mapLayout.setVisibility(View.GONE);
                        mapDetailLayout.setVisibility(View.VISIBLE);
                    }, 800);
                }
                break;
        }
    }

}
