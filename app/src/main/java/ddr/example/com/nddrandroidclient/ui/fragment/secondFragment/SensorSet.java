package ddr.example.com.nddrandroidclient.ui.fragment.secondFragment;

import android.graphics.Color;
import android.view.View;
import android.widget.EditText;

import com.google.protobuf.ByteString;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import DDRCommProto.BaseCmd;
import butterknife.BindView;
import butterknife.OnClick;
import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.common.DDRLazyFragment;
import ddr.example.com.nddrandroidclient.entity.MessageEvent;
import ddr.example.com.nddrandroidclient.entity.other.Parameter;
import ddr.example.com.nddrandroidclient.entity.other.Parameters;
import ddr.example.com.nddrandroidclient.entity.other.Sensor;
import ddr.example.com.nddrandroidclient.entity.other.Sensors;
import ddr.example.com.nddrandroidclient.other.Logger;
import ddr.example.com.nddrandroidclient.other.SlideButton;
import ddr.example.com.nddrandroidclient.protocobuf.dispatcher.ClientMessageDispatcher;
import ddr.example.com.nddrandroidclient.socket.TcpClient;

public class SensorSet extends DDRLazyFragment {
    @BindView(R.id.slideButton)
    SlideButton slideButton;
    @BindView(R.id.ed_cs1)
    EditText ed_cs1;
    @BindView(R.id.ed_cs2)
    EditText ed_cs2;
    @BindView(R.id.ed_cs3)
    EditText ed_cs3;
    @BindView(R.id.ed_cs4)
    EditText ed_cs4;
    @BindView(R.id.ed_cs5)
    EditText ed_cs5;
    @BindView(R.id.ed_cs6)
    EditText ed_cs6;
    @BindView(R.id.ed_cs7)
    EditText ed_cs7;
    @BindView(R.id.ed_cs8)
    EditText ed_cs8;
    @BindView(R.id.ed_cs9)
    EditText ed_cs9;
    @BindView(R.id.ed_cs10)
    EditText ed_cs10;
    @BindView(R.id.ed_cs11)
    EditText ed_cs11;
    @BindView(R.id.ed_cs12)
    EditText ed_cs12;
    @BindView(R.id.ed_imu)
    EditText ed_imu;



    private TcpClient tcpClient;
    private Sensor sensor;
    private Sensors sensors;
    private List<Sensor> sensorList=new ArrayList<>();
    private Parameters parameters;
    private List<Parameter> parameterList=new ArrayList<>();
    private String sensorKey="Emb_Params.ENABLE_SERSOR_AVOIDANCE";
    private String imuKey="Emb_Params.MAX_SILENT_TIME_FOR_EMBEDDED_MB_MS";

    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void update(MessageEvent messageEvent) {
        switch (messageEvent.getType()) {
            case updataSenesor:
                setSensorParam();
                break;
            case updataParameter:
                setNaparmeter();
                break;
        }
    }
    @OnClick({R.id.slideButton})
    public void onViewClicked(View view){
        switch (view.getId()){
            case R.id.slideButton:
                getChosseStatus();
                postNaparmeter(ByteString.copyFromUtf8(sensorKey),ByteString.copyFromUtf8(autoValue),2,3);
                break;
        }
    }

    public static SensorSet newInstance(){return new SensorSet();}
    @Override
    protected int getLayoutId() {
        return R.layout.fragmen_s_senesor;
    }

    @Override
    protected void initView() {
        slideButton.setSmallCircleModel(
                Color.parseColor("#999999"), Color.parseColor("#999999"),
                Color.parseColor("#ffffff"), Color.parseColor("#ffffff"));
    }

    @Override
    protected void initData() {
        tcpClient= TcpClient.getInstance(getContext(), ClientMessageDispatcher.getInstance());
        sensors=Sensors.getInstance();
        getSensorParam();
        getChosseStatus();
        getNaparmeter();

    }
    //获取传感器参数
    private void getSensorParam(){
        BaseCmd.reqSensorConfigOperational reqSensorConfigOperational = BaseCmd.reqSensorConfigOperational.newBuilder()
                .build();
        BaseCmd.CommonHeader commonHeader = BaseCmd.CommonHeader.newBuilder()
                .setFromCltType(BaseCmd.eCltType.eLocalAndroidClient)
                .setToCltType(BaseCmd.eCltType.eLSMSlamNavigation)
                .addFlowDirection(BaseCmd.CommonHeader.eFlowDir.Forward)
                .build();
        tcpClient.sendData(commonHeader, reqSensorConfigOperational);
    }
    //设置传感器参数
    private void setSensorParam(){
        sensorList=sensors.getSensorList();
        for (int i=0;i<sensorList.size();i++){
            if (sensorList.get(i).getKey().equals("1")){
                ed_cs1.setText(sensorList.get(i).getDydistance());
            }
            if (sensorList.get(i).getKey().equals("2")){
                ed_cs2.setText(sensorList.get(i).getDydistance());
            }
            if (sensorList.get(i).getKey().equals("3")){
                ed_cs3.setText(sensorList.get(i).getDydistance());
            }
            if (sensorList.get(i).getKey().equals("4")){
                ed_cs4.setText(sensorList.get(i).getDydistance());
            }
            if (sensorList.get(i).getKey().equals("5")){
                ed_cs5.setText(sensorList.get(i).getDydistance());
            }
            if (sensorList.get(i).getKey().equals("6")){
                ed_cs6.setText(sensorList.get(i).getDydistance());
            }
            if (sensorList.get(i).getKey().equals("7")){
                ed_cs7.setText(sensorList.get(i).getDydistance());
            }
            if (sensorList.get(i).getKey().equals("8")){
                ed_cs8.setText(sensorList.get(i).getDydistance());
            }
            if (sensorList.get(i).getKey().equals("9")){
                ed_cs9.setText(sensorList.get(i).getDydistance());
            }
            if (sensorList.get(i).getKey().equals("10")){
                ed_cs10.setText(sensorList.get(i).getDydistance());
            }
            if (sensorList.get(i).getKey().equals("11")){
                ed_cs11.setText(sensorList.get(i).getDydistance());
            }
            if (sensorList.get(i).getKey().equals("12")){
                ed_cs12.setText(sensorList.get(i).getDydistance());
            }
        }
    }
    //发送传感器参数
    private void postSensorParam(ByteString key,ByteString value,int type){
        BaseCmd.eSensorConfigItemOptType eSensorConfigItemOptType;
        switch (type){
            case 0:
                eSensorConfigItemOptType=BaseCmd.eSensorConfigItemOptType.eSensorConfigOptTypeError;//
                break;
            case  1:
                eSensorConfigItemOptType=BaseCmd.eSensorConfigItemOptType.eSensorConfigOptTypeGetData;//获取数据
                break;
            case 2:
                eSensorConfigItemOptType=BaseCmd.eSensorConfigItemOptType.eSensorConfigOptTypeResumeData;//恢复
                break;
            case 3:
                eSensorConfigItemOptType=BaseCmd.eSensorConfigItemOptType.eSensorConfigOptTypeSetData;//设置
                break;
            case 4:
                eSensorConfigItemOptType=BaseCmd.eSensorConfigItemOptType.eSensorConfigOptTypeDisableAll;//失能
                break;
            case 5:
                eSensorConfigItemOptType=BaseCmd.eSensorConfigItemOptType.eSensorConfigOptTypeEnableAll;//使能
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
        BaseCmd.sensorConfigItem sensorConfigItem=BaseCmd.sensorConfigItem.newBuilder()
                .setKey(key)
                .setDynamicOATriggerDist(value)
                .build();
        List<BaseCmd.sensorConfigItem> sensorConfigItemList=new ArrayList<>();
        sensorConfigItemList.add(sensorConfigItem);
        BaseCmd.reqSensorConfigOperational reqSensorConfigOperational=BaseCmd.reqSensorConfigOperational.newBuilder()
                .setType(eSensorConfigItemOptType)
                .addAllData(sensorConfigItemList)
                .build();
        tcpClient.sendData(null,reqSensorConfigOperational);
    }
    //获取导航参数
    private void getNaparmeter(){
        BaseCmd.reqConfigOperational reqConfigOperational = BaseCmd.reqConfigOperational.newBuilder()
                .build();
        BaseCmd.CommonHeader commonHeader = BaseCmd.CommonHeader.newBuilder()
                .setFromCltType(BaseCmd.eCltType.eLocalAndroidClient)
                .setToCltType(BaseCmd.eCltType.eLSMSlamNavigation)
                .addFlowDirection(BaseCmd.CommonHeader.eFlowDir.Forward)
                .build();
        tcpClient.sendData(commonHeader, reqConfigOperational);
    }
    //设置导航参数
    private void setNaparmeter(){
        parameterList=parameters.getParameterList();
        for (int i=0;i<parameterList.size();i++){
            if(parameterList.get(i).getKey().contains(sensorKey)){
                if (parameterList.get(i).getValue().equals("1")){
                    slideButton.setChecked(true);
                }else {
                    slideButton.setChecked(false);
                }
            }
            if (parameterList.get(i).getKey().equals(imuKey)){
                ed_imu.setText(parameterList.get(i).getValue());
            }

        }
    }

    //发送导航参数
    private void postNaparmeter(ByteString key, ByteString value, int type, int optType){
        BaseCmd.eConfigItemType eConfigItemType;
        BaseCmd.eConfigItemOptType eConfigItemOptType;
        switch (type){
            case 0:
                eConfigItemType=BaseCmd.eConfigItemType.eConfigTypeError;
                break;
            case 1:
                eConfigItemType=BaseCmd.eConfigItemType.eConfigTypeCore;
                break;
            case 2:
                eConfigItemType=BaseCmd.eConfigItemType.eConfigTypeLogic;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
        switch (optType){
            case 0:
                eConfigItemOptType=BaseCmd.eConfigItemOptType.eConfigOptTypeError;//全部
                break;
            case 1:
                eConfigItemOptType=BaseCmd.eConfigItemOptType.eConfigOptTypeGetData;//获取数据
                break;
            case 2:
                eConfigItemOptType=BaseCmd.eConfigItemOptType.eConfigOptTypeResumeData;//恢复数据
                break;
            case 3:
                eConfigItemOptType=BaseCmd.eConfigItemOptType.eConfigOptTypeSetData;//设置数据
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + optType);
        }
        BaseCmd.configItem configItem=BaseCmd.configItem.newBuilder()
                .setKey(key)
                .setValue(value)
                .build();
        BaseCmd.configData configData=BaseCmd.configData.newBuilder()
                .setType(eConfigItemType)
                .setData(configItem)
                .build();
        List<BaseCmd.configData> configDataList=new ArrayList<>();
        configDataList.add(configData);
        BaseCmd.reqConfigOperational reqConfigOperational=BaseCmd.reqConfigOperational.newBuilder()
                .setType(eConfigItemOptType)
                .addAllData(configDataList)
                .build();
        tcpClient.sendData(null,reqConfigOperational);

    }

    //获取选择的状态
    private String autoValue;
    private void getChosseStatus(){
        boolean isChecked=slideButton.isChecked;
        if (isChecked==true){
            autoValue="5";
        }else {
            autoValue="4";
        }
        Logger.e("是否选择"+isChecked);
    }
}
