package ddr.example.com.nddrandroidclient.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.protobuf.ByteString;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
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
import ddr.example.com.nddrandroidclient.helper.ActivityStackManager;
import ddr.example.com.nddrandroidclient.helper.ListTool;
import ddr.example.com.nddrandroidclient.other.DpOrPxUtils;
import ddr.example.com.nddrandroidclient.other.InputFilterMinMax;
import ddr.example.com.nddrandroidclient.other.Logger;
import ddr.example.com.nddrandroidclient.protocobuf.dispatcher.ClientMessageDispatcher;
import ddr.example.com.nddrandroidclient.socket.TcpClient;
import ddr.example.com.nddrandroidclient.ui.adapter.NLinearLayoutManager;
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
    private BaseDialog waitDialog,waitDialog1,waitDialog2;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_map_setting;
    }

    @Override
    protected void initView() {
        super.initView();
        mapFileStatus=MapFileStatus.getInstance();
        tcpClient=TcpClient.getInstance(context,ClientMessageDispatcher.getInstance());
        etABSpeed.setFilters(new InputFilter[]{new InputFilterMinMax("0", "1.0")});
    }

    @Override
    protected void initData() {
        super.initData();
        Intent intent=getIntent();
        mapName=intent.getStringExtra("mapName");
        String name = mapName.replaceAll("OneRoute_", "");
        etMapName.setText(name);
    }


    @OnClick({R.id.tv_title, R.id.tv_recover, R.id.tv_navigation, R.id.tv_line_patrol, R.id.tv_cancel, R.id.tv_confirm,R.id.tv_switch_point,R.id.tv_static_mode,R.id.tv_dynamic_mode,R.id.tv_point_set})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_title:
                new InputDialog.Builder(this)
                        .setTitle(R.string.is_save_setting)
                        .setConfirm(R.string.save_dialog)
                        .setCancel(R.string.not_save_dialog)
                        .setEditVisibility(View.GONE)
                        .setListener(new InputDialog.OnListener() {
                            @Override
                            public void onConfirm(BaseDialog dialog, String content) {
                                waitDialog2=new WaitDialog.Builder(getActivity())
                                        .setMessage(R.string.in_storage)
                                        .show();
                                postDelayed(() -> {
                                    if (waitDialog2.isShowing()) {
                                        waitDialog2.dismiss();
                                        toast(R.string.save_failed);
                                    }
                                }, 4000);
                                abSpeed= Float.parseFloat(etABSpeed.getText().toString());
                                String name=tvSwitchPoint.getText().toString().trim();
                                if (name.equals(getString(R.string.stay_put))){
                                    name="";
                                }
                                tcpClient.saveDataToServer(modeType,name,abMode,abSpeed);
                            }
                            @Override
                            public void onCancel(BaseDialog dialog) {
                                //tcpClient.requestFile();
                                finish();

                            }
                        }).show();
                break;
            case R.id.tv_recover:
                new InputDialog.Builder(this)
                        .setTitle(R.string.empty_map_data)
                        .setEditVisibility(View.GONE)
                        .setListener(new InputDialog.OnListener() {
                            @Override
                            public void onConfirm(BaseDialog dialog, String content) {
                                if (NotifyBaseStatusEx.getInstance().getCurroute().equals(mapName)){
                                    toast(R.string.current_map_using);
                                }else {
                                    List<DDRVLNMap.reqMapOperational.OptItem> optItems1=new ArrayList<>();
                                    DDRVLNMap.reqMapOperational.OptItem optItem1=DDRVLNMap.reqMapOperational.OptItem.newBuilder()
                                            .setTypeValue(2)
                                            .setSourceName(ByteString.copyFromUtf8(mapName))
                                            .build();
                                    optItems1.add(optItem1);
                                    tcpClient.reqMapOperational(optItems1);
                                    waitDialog=new WaitDialog.Builder(getActivity())
                                            .setMessage(R.string.under_revision)
                                            .show();
                                   postDelayed(() -> {
                                        if (waitDialog.isShowing()) {
                                            waitDialog.dismiss();
                                            toast(R.string.modification_failed);
                                        }
                                    }, 4000);
                                }
                            }
                            @Override
                            public void onCancel(BaseDialog dialog) {
                            }
                        }).show();
                break;
            case R.id.tv_point_set:
                toast(R.string.point_set_notify);
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
                tvNavigation.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.checkedwg),null,null,null);
                tvLinePatrol.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.nocheckedwg),null,null,null);
                modeType=2;
                Logger.e("-----------"+mapFileStatus.getCurrentMapEx().getBasedata().getAbNaviTypeValue());
                break;
            case R.id.tv_line_patrol:
                tvLinePatrol.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.checkedwg),null,null,null);
                tvNavigation.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.nocheckedwg),null,null,null);
                modeType=1;
                Logger.e("-----------"+mapFileStatus.getCurrentMapEx().getBasedata().getAbNaviTypeValue());
                break;
            case R.id.tv_cancel:
                finish();
                break;
            case R.id.tv_confirm:
                waitDialog1=new WaitDialog.Builder(this)
                        .setMessage(R.string.in_storage)
                        .show();
                postDelayed(() -> {
                    if (waitDialog1.isShowing()) {
                        waitDialog1.dismiss();
                        toast(R.string.save_failed);
                    }
                }, 4000);
                abSpeed= Float.parseFloat(etABSpeed.getText().toString());
                String name=tvSwitchPoint.getText().toString().trim();
                if (name.equals(getString(R.string.stay_put))){
                    name="";
                }
                newMapName=etMapName.getText().toString().trim();
                newMapName="OneRoute_"+newMapName;
                List<DDRVLNMap.reqMapOperational.OptItem> optItems=new ArrayList<>();
                DDRVLNMap.reqMapOperational.OptItem optItem=DDRVLNMap.reqMapOperational.OptItem.newBuilder()
                        .setTypeValue(3)
                        .setSourceName(ByteString.copyFromUtf8(mapName))
                        .setTargetName(ByteString.copyFromUtf8(newMapName))
                        .build();
                optItems.add(optItem);
                tcpClient.reqMapOperational(optItems);
                tcpClient.saveDataToServer(modeType,name,abMode,abSpeed);
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
        NLinearLayoutManager layoutManager=new NLinearLayoutManager(this);
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
    private String newMapName;
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
                modeType=ddrMapBaseData.getAbNaviTypeValue();
                try {
                    targetPoints = ListTool.deepCopy(mapFileStatus.getTargetPoints());
                    TargetPoint targetPoint=new TargetPoint();
                    targetPoint.setName(getString(R.string.stay_put));
                    targetPoints.add(0,targetPoint);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                String pointName=ddrMapBaseData.getTargetPtName().toStringUtf8();
                if (pointName.equals("")){
                    pointName=getString(R.string.stay_put);
                }
                tvSwitchPoint.setText(pointName);
                abSpeed=ddrMapBaseData.getAbPathSpeed();
                Logger.e("ab点速度："+abSpeed);
                abMode=ddrMapBaseData.getAbPathModeValue();
                if (modeType==1){
                    tvLinePatrol.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.checkedwg),null,null,null);
                    tvNavigation.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.nocheckedwg),null,null,null);
                }else if (modeType==2){
                    tvNavigation.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.checkedwg),null,null,null);
                    tvLinePatrol.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.nocheckedwg),null,null,null);
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
                            toast(R.string.save_succeed);
                            tcpClient.requestFile();
                            finish();
                        }
                    }else if (waitDialog2!=null){
                        if (waitDialog2.isShowing()){
                            waitDialog2.dismiss();
                            tcpClient.requestFile();
                            finish();
                        }
                    }
                },1000);
                break;
            case mapOperationalSucceed:
                if (waitDialog!=null){
                    if (waitDialog.isShowing()){
                        waitDialog.dismiss();
                    }
                }else {
                    String name = newMapName.replaceAll("OneRoute_", "");
                    etMapName.setText(name);
                    mapName=newMapName;
                }
                break;
            case notifyTCPDisconnected:
                netWorkStatusDialog();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (tcpClient!=null&&!tcpClient.isConnected()){
            Logger.e("网络已断开");
           // netWorkStatusDialog();
        }
    }

    /**
     * 显示网络连接弹窗
     */
    private void  netWorkStatusDialog(){
        waitDialog=new WaitDialog.Builder(this).setMessage(R.string.common_network_connecting).show();
        postDelayed(()->{
            if (waitDialog.isShowing()){
                toast(R.string.network_not_connect);
                ActivityStackManager.getInstance().finishAllActivities();
                startActivity(LoginActivity.class);
            }
        },6000);
    }
}
