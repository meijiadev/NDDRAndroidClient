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

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;
import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.base.BaseDialog;
import ddr.example.com.nddrandroidclient.common.DDRLazyFragment;
import ddr.example.com.nddrandroidclient.download.FileUtil;
import ddr.example.com.nddrandroidclient.entity.info.MapFileStatus;
import ddr.example.com.nddrandroidclient.entity.info.MapInfo;
import ddr.example.com.nddrandroidclient.entity.MessageEvent;
import ddr.example.com.nddrandroidclient.other.Logger;
import ddr.example.com.nddrandroidclient.protocobuf.dispatcher.ClientMessageDispatcher;
import ddr.example.com.nddrandroidclient.socket.TcpClient;
import ddr.example.com.nddrandroidclient.ui.activity.CollectingActivity;
import ddr.example.com.nddrandroidclient.ui.activity.HomeActivity;
import ddr.example.com.nddrandroidclient.ui.activity.MapEditActivity;
import ddr.example.com.nddrandroidclient.ui.adapter.MapAdapter;
import ddr.example.com.nddrandroidclient.ui.dialog.WaitDialog;
import ddr.example.com.nddrandroidclient.widget.ZoomImageView;

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
    RelativeLayout mapDetailLayout;

    @BindView(R.id.left_detail_layout)
    RelativeLayout leftDetailLayout;
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


    private MapFileStatus mapFileStatus;
    private MapAdapter mapAdapter;
    private List<MapInfo> mapInfos = new ArrayList<>();
    private List<String> downloadMapNames = new ArrayList<>();
    private TcpClient tcpClient;
    private boolean isShowSelected;   //显示批量管理的按钮

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void update(MessageEvent messageEvent) {
        switch (messageEvent.getType()) {
            case updateMapList:
                downloadMapNames = mapFileStatus.getMapNames();
                checkFilesAllName(downloadMapNames);
                transformMapInfo(messageEvent.getMapInfoList());
                mapAdapter.setNewData(mapInfos);

                break;
            case updateDDRVLNMap:
                if (dialog.isShowing()){
                    dialog.dismiss();
                    mapLayout.setVisibility(View.GONE);
                    mapDetailLayout.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

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
        onItemClick();
    }

    @Override
    protected void initData() {
        tcpClient=TcpClient.getInstance(getAttachActivity(),ClientMessageDispatcher.getInstance());
        mapFileStatus = MapFileStatus.getInstance();
        mapAdapter.setNewData(mapInfos);
    }

    @OnClick({R.id.bt_create_map,R.id.iv_back,R.id.tv_target_point,R.id.tv_add_new,R.id.bt_batch_delete})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_create_map:
                startActivity(CollectingActivity.class);
                break;
            case R.id.bt_batch_delete:
                if (isShowSelected){
                    isShowSelected=false;
                    mapAdapter.showSelected(false);
                }else {
                    isShowSelected=true;
                    mapAdapter.showSelected(true);
                }
                break;
            case R.id.iv_back:
                mapDetailLayout.setVisibility(View.GONE);
                mapLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_target_point:
                if (leftDetailLayout.getVisibility()==View.GONE){
                    leftDetailLayout.setVisibility(View.VISIBLE);
                }else {
                    leftDetailLayout.setVisibility(View.GONE);
                }
                break;
            case R.id.tv_add_new:
                startActivity(MapEditActivity.class);
                break;
        }
    }

    private BaseDialog dialog;
    public void onItemClick() {
        mapAdapter.setOnItemClickListener(((adapter, view, position) -> {
            Logger.e("--------:"+mapInfos.get(position).getMapName());
            if (isShowSelected){
                MapInfo mapInfo=mapInfos.get(position);
                if (mapInfo.isSelected()){
                    mapInfo.setSelected(false);
                }else {
                    mapInfo.setSelected(true);
                }
                mapAdapter.setData(position,mapInfo);
            }else {
                tcpClient.getMapInfo(ByteString.copyFromUtf8(mapInfos.get(position).getMapName()));
                dialog=new WaitDialog.Builder(getAttachActivity())
                        .setMessage("加载地图信息中")
                        .show();
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(mapInfos.get(position).getBitmap());
                    Bitmap bitmap = BitmapFactory.decodeStream(fis);
                    zoomMap.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
                getAttachActivity().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (dialog.isShowing()){
                            dialog.dismiss();
                            toast("加载失败！");
                            mapLayout.setVisibility(View.GONE);
                            mapDetailLayout.setVisibility(View.VISIBLE);
                        }
                    }
                }, 5000);
            }
        }));
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

}
