package ddr.example.com.nddrandroidclient.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.protobuf.ByteString;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import DDRVLNMapProto.DDRVLNMap;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;
import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.base.BaseDialog;
import ddr.example.com.nddrandroidclient.common.DDRActivity;
import ddr.example.com.nddrandroidclient.entity.MessageEvent;
import ddr.example.com.nddrandroidclient.entity.info.MapFileStatus;
import ddr.example.com.nddrandroidclient.entity.info.NotifyBaseStatusEx;
import ddr.example.com.nddrandroidclient.entity.point.TargetPoint;
import ddr.example.com.nddrandroidclient.other.DpOrPxUtils;
import ddr.example.com.nddrandroidclient.other.Logger;
import ddr.example.com.nddrandroidclient.protocobuf.dispatcher.ClientMessageDispatcher;
import ddr.example.com.nddrandroidclient.socket.TcpClient;
import ddr.example.com.nddrandroidclient.ui.adapter.TargetPointAdapter;
import ddr.example.com.nddrandroidclient.ui.dialog.InputDialog;

import ddr.example.com.nddrandroidclient.ui.dialog.WaitDialog;
import ddr.example.com.nddrandroidclient.widget.view.CustomPopuWindow;

/**
 * time： 2020/2/18
 * desc: 地图设置界面
 */
public class MapSettingActivity extends DDRActivity {
    @BindView(R.id.tv_recover)
    TextView tvRecover;
    @BindView(R.id.et_map_name)
    EditText etMapName;
    @BindView(R.id.tv_point_set)
    TextView tvPointSet;
    @BindView(R.id.tv_switch_point)
    TextView tvSwitchPoint;             //用于切换待机点
    @BindView(R.id.tv_a_b_mode)
    TextView tvABMode;
    @BindView(R.id.tv_static_mode)
    TextView tvStaticMode;
    @BindView(R.id.tv_dynamic_mode)
    TextView tvDynamicMode;
    @BindView(R.id.et_a_b_speed)
    EditText etABSpeed;
    @BindView(R.id.tv_navigation)
    TextView tvNavigation;
    @BindView(R.id.tv_line_patrol)
    TextView tvLinePatrol;
    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    @BindView(R.id.tv_confirm)
    TextView tvConfirm;


    private MapFileStatus mapFileStatus;
    private TcpClient tcpClient;
    private BaseDialog waitDialog,waitDialog1;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_map_setting;
    }

    @Override
    protected void initView() {
        super.initView();
        mapFileStatus=MapFileStatus.getInstance();
        tcpClient=TcpClient.getInstance(context,ClientMessageDispatcher.getInstance());
    }

    @Override
    protected void initData() {
        super.initData();
    }


    @OnClick({R.id.tv_title, R.id.tv_recover, R.id.tv_navigation, R.id.tv_line_patrol, R.id.tv_cancel, R.id.tv_confirm,R.id.tv_switch_point,R.id.tv_static_mode,R.id.tv_dynamic_mode})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_title:
                tcpClient.requestFile();
                finish();
                break;
            case R.id.tv_recover:
                new InputDialog.Builder(this)
                        .setTitle("将清空全部地图数据，确认操作？")
                        .setEditVisibility(View.GONE)
                        .setListener(new InputDialog.OnListener() {
                            @Override
                            public void onConfirm(BaseDialog dialog, String content) {
                                if (NotifyBaseStatusEx.getInstance().getCurroute().equals(mapName)){
                                    toast("当前地图正在使用中，无法修改");
                                }else {
                                    List<DDRVLNMap.reqMapOperational.OptItem> optItems1=new ArrayList<>();
                                    DDRVLNMap.reqMapOperational.OptItem optItem1=DDRVLNMap.reqMapOperational.OptItem.newBuilder()
                                            .setTypeValue(2)
                                            .setSourceName(ByteString.copyFromUtf8(mapName))
                                            .build();
                                    optItems1.add(optItem1);
                                    tcpClient.reqMapOperational(optItems1);
                                    waitDialog=new WaitDialog.Builder(getActivity())
                                            .setMessage("正在修改中")
                                            .show();
                                   postDelayed(() -> {
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
                break;
            case R.id.tv_switch_point:
                showListPopupWindow(tvSwitchPoint);
                break;
            case R.id.tv_static_mode:
                abMode=65;
                tvStaticMode.setBackgroundResource(R.drawable.tv_mode_selected_bg);
                tvDynamicMode.setBackgroundResource(R.drawable.tv_mode_default_bg);
                break;
            case R.id.tv_dynamic_mode:
                abMode=64;
                tvDynamicMode.setBackgroundResource(R.drawable.tv_mode_selected_bg);
                tvStaticMode.setBackgroundResource(R.drawable.tv_mode_default_bg);
                break;
            case R.id.tv_navigation:
                tvNavigation.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.iv_selected_blue),null,null,null);
                tvLinePatrol.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.iv_selected_gray),null,null,null);
                modeType=2;
                Logger.e("-----------"+mapFileStatus.getCurrentMapEx().getBasedata().getAbNaviTypeValue());
                break;
            case R.id.tv_line_patrol:
                tvLinePatrol.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.iv_selected_blue),null,null,null);
                tvNavigation.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.iv_selected_gray),null,null,null);
                modeType=1;
                Logger.e("-----------"+mapFileStatus.getCurrentMapEx().getBasedata().getAbNaviTypeValue());
                break;
            case R.id.tv_cancel:
                finish();
                break;
            case R.id.tv_confirm:
                waitDialog1=new WaitDialog.Builder(this)
                        .setMessage("正在保存...")
                        .show();
                postDelayed(() -> {
                    if (waitDialog1.isShowing()) {
                        waitDialog1.dismiss();
                        toast("保存失败！");
                    }
                }, 4000);
                abSpeed= Float.parseFloat(etABSpeed.getText().toString());
                tcpClient.saveDataToServer(modeType,tvSwitchPoint.getText().toString().trim(),abMode,abSpeed);
                break;
        }
    }

    private CustomPopuWindow customPopuWindow;
    private  RecyclerView showRecycler;
    private TargetPointAdapter targetPointAdapter;
    private List<TargetPoint> targetPoints=new ArrayList<>();
    private  void showListPopupWindow(View view){
        View contentView = getActivity().getLayoutInflater().from(this).inflate(R.layout.recycle_task, null);
        customPopuWindow = new CustomPopuWindow.PopupWindowBuilder(this)
                .setView(contentView)
                .enableOutsideTouchableDissmiss(false)
                .create()
                .showAsDropDown(view, DpOrPxUtils.dip2px(this, 0), 5);
        showRecycler =contentView.findViewById(R.id.recycler_task_check);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        showRecycler.setLayoutManager(layoutManager);
        targetPointAdapter=new TargetPointAdapter(R.layout.item_recycle_task_check);
        targetPointAdapter.setNewData(targetPoints);
        showRecycler.setAdapter(targetPointAdapter);

        targetPointAdapter.setOnItemClickListener(((adapter, view1, position) -> {
            String pointName=targetPoints.get(position).getName();
            toast("  "+pointName);
            tvSwitchPoint.setText(pointName);
            customPopuWindow.dissmiss();
        }));

    }


    private DDRVLNMap.reqDDRVLNMapEx reqDDRVLNMapEx;     //获取指定某一地图的相关信息
    private DDRVLNMap.DDRMapBaseData ddrMapBaseData;
    private String mapName="";
    private int modeType;
    private float abSpeed;           //ab点速度
    private int abMode;              //ab点模式

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void update(MessageEvent messageEvent) {
        switch (messageEvent.getType()) {
            case updateDDRVLNMap:
                mapFileStatus=MapFileStatus.getInstance();
                reqDDRVLNMapEx=mapFileStatus.getReqDDRVLNMapEx();
                ddrMapBaseData=reqDDRVLNMapEx.getBasedata();
                mapName=ddrMapBaseData.getName().toStringUtf8();
                modeType=ddrMapBaseData.getAbNaviTypeValue();
                targetPoints=mapFileStatus.getTargetPoints();
                etMapName.setText(mapName);
                tvSwitchPoint.setText(ddrMapBaseData.getTargetPtName().toStringUtf8());
                abSpeed=ddrMapBaseData.getAbPathSpeed();
                Logger.e("ab点速度："+abSpeed);
                abMode=ddrMapBaseData.getAbPathModeValue();
                if (modeType==1){
                    tvLinePatrol.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.iv_selected_blue),null,null,null);
                    tvNavigation.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.iv_selected_gray),null,null,null);
                }else if (modeType==2){
                    tvNavigation.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.iv_selected_blue),null,null,null);
                    tvLinePatrol.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.iv_selected_gray),null,null,null);
                }
                etABSpeed.setText(String.valueOf(abSpeed));
                if (abMode==64){
                    tvDynamicMode.setBackgroundResource(R.drawable.tv_mode_selected_bg);
                    tvStaticMode.setBackgroundResource(R.drawable.tv_mode_default_bg);
                }else if (abMode==65){
                    tvStaticMode.setBackgroundResource(R.drawable.tv_mode_selected_bg);
                    tvDynamicMode.setBackgroundResource(R.drawable.tv_mode_default_bg);
                }
                postDelayed(()->{
                    if (waitDialog1!=null){
                        if (waitDialog1.isShowing()){
                            waitDialog1.dismiss();
                            toast("保存成功");
                        }
                    }
                },1500);
                break;
            case mapOperationalSucceed:
                if (waitDialog!=null){
                    if (waitDialog.isShowing()){
                        waitDialog.dismiss();
                    }
                }
                break;
        }
    }

}
