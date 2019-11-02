package ddr.example.com.nddrandroidclient.ui.fragment;

import android.annotation.SuppressLint;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;
import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.common.DDRLazyFragment;
import ddr.example.com.nddrandroidclient.download.FileUtil;
import ddr.example.com.nddrandroidclient.entity.MapFileStatus;
import ddr.example.com.nddrandroidclient.entity.MapInfo;
import ddr.example.com.nddrandroidclient.entity.MessageEvent;
import ddr.example.com.nddrandroidclient.other.Logger;
import ddr.example.com.nddrandroidclient.ui.activity.CollectingActivity;
import ddr.example.com.nddrandroidclient.ui.activity.HomeActivity;
import ddr.example.com.nddrandroidclient.ui.adapter.MapAdapter;

/**
 * time: 2019/10/26
 * desc: 地图管理界面
 */
public class MapFragment extends DDRLazyFragment<HomeActivity> {
    @BindView(R.id.bt_create_map)
    TextView btCreatMap;
    @BindView(R.id.recycler_map)
    RecyclerView mapRecycler;

    private MapFileStatus mapFileStatus;
    private MapAdapter mapAdapter;
    private List<MapInfo> mapInfos=new ArrayList<>();
    private List<String>downloadMapNames=new ArrayList<>();

    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void update(MessageEvent messageEvent){
        switch (messageEvent.getType()){
            case updateMapList:
                downloadMapNames=mapFileStatus.getMapNames();
                checkFilesAllName(downloadMapNames);
                transformMapInfo(messageEvent.getMapInfoList());
                mapAdapter.setNewData(mapInfos);
                break;
        }
    }

    public static MapFragment newInstance(){
        return new MapFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_map;
    }

    @Override
    protected void initView() {
        mapAdapter=new MapAdapter(R.layout.item_map_recycler,getAttachActivity());
        @SuppressLint("WrongConstant")
        GridLayoutManager gridLayoutManager=new GridLayoutManager(getAttachActivity(),4,LinearLayoutManager.VERTICAL,false);
        mapRecycler.setLayoutManager(gridLayoutManager);
        mapRecycler.setAdapter(mapAdapter);
        onItemClick();
    }

    @Override
    protected void initData() {
        mapFileStatus=MapFileStatus.getInstance();
    }
    @OnClick({R.id.bt_create_map})
    public void onViewClicked(View view){
        switch (view.getId()){
            case R.id.bt_create_map:
            startActivity(CollectingActivity.class);
            break;
        }
    }

    public void onItemClick(){
        mapAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                List<MapInfo> newMapInfos=new ArrayList<>();   //创建一个新的列表
                Collections.copy(newMapInfos,mapInfos);        //将原始数据复制到新列表中 然后改变新列表数据
                MapInfo mapInfo=newMapInfos.get(position);
                mapInfo.setUsing(true);
                newMapInfos.set(position,mapInfo);
                mapAdapter.setNewData(newMapInfos);
            }
        });
    }


    /**
     * 对比本地文件和服务的文件是否有区别,将服务端不存在的本地文件删除，保持一致性
     * @return
     */
    public void checkFilesAllName(List<String> downloadMapNames) {
        if (downloadMapNames!=null){
            File file = new File(Environment.getExternalStorageDirectory().getPath() + "/" + "机器人");
            if (file.exists()) {
                File[] files = file.listFiles();
                if (files == null) {
                    Logger.e("空目录");
                }
                for (int i = 0; i < files.length; i++) {
                    if (downloadMapNames.size()>0){
                        if (downloadMapNames.contains(files[i].getName())){
                        }else {
                            FileUtil.deleteFile(files[i]);
                        }
                    }
                }
            }
        }
    }

    /**
     * 设置图片的路径
     * @param infoList
     */
    public void transformMapInfo(List<MapInfo> infoList) {
        for (int i=0;i<infoList.size();i++){
            String dirName=infoList.get(i).getMapName();
            String pngPath=Environment.getExternalStorageDirectory().getPath() + "/" + "机器人" + "/" + dirName + "/" + "bkPic.png";
            if (pngPath!=null){
                infoList.get(i).setBitmap(pngPath);
            }else {
                infoList.remove(i);
            }
        }
        for (int i=0;i<infoList.size();i++){
            Logger.e("------"+infoList.get(i).getTime());
        }
        mapInfos=infoList;
    }



    @Override
    protected void onRestart() {
        super.onRestart();
        Logger.e("------onRestart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Logger.e("-----onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Logger.e("------onPause");
    }
}
