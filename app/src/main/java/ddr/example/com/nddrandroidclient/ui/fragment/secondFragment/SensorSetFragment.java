package ddr.example.com.nddrandroidclient.ui.fragment.secondFragment;

import android.graphics.Color;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
import ddr.example.com.nddrandroidclient.entity.other.SensorSea;
import ddr.example.com.nddrandroidclient.entity.other.SensorSeas;
import ddr.example.com.nddrandroidclient.entity.other.Sensors;
import ddr.example.com.nddrandroidclient.entity.other.Ultrasonic;
import ddr.example.com.nddrandroidclient.other.InputFilterMinMax;
import ddr.example.com.nddrandroidclient.other.Logger;
import ddr.example.com.nddrandroidclient.other.SlideButton;
import ddr.example.com.nddrandroidclient.protocobuf.CmdSchedule;
import ddr.example.com.nddrandroidclient.protocobuf.dispatcher.ClientMessageDispatcher;
import ddr.example.com.nddrandroidclient.socket.TcpClient;
import ddr.example.com.nddrandroidclient.ui.adapter.SensorAdapter;

import ddr.example.com.nddrandroidclient.ui.adapter.UltrasonicAdapter;
import ddr.example.com.nddrandroidclient.widget.view.CustomPopuWindow;

/**
 * time: 2020/03/24
 * desc: 高级设置传感器设置界面
 */
public class SensorSetFragment extends DDRLazyFragment {
    @BindView(R.id.slideButton)
    SlideButton slideButton;
    @BindView(R.id.ed_imu)
    EditText ed_imu;
    @BindView(R.id.tv_save_sensor)
    TextView tv_save_sensor;
    @BindView(R.id.rl_ul_cs)
    RecyclerView rl_ul_cs;
    @BindView(R.id.tv_sea_sensor)
    TextView tv_sea_senesor;



    private TcpClient tcpClient;
    private Sensor sensor;
    private Sensors sensors;
    private List<Sensor> sensorList=new ArrayList<>();
    private Parameters parameters;
    private List<Parameter> parameterList=new ArrayList<>();
    private String sensorKey="Emb_Params.ENABLE_SERSOR_AVOIDANCE";
    private String imuKey="Emb_Params.TARGET_IMU_WORKING_TEMP";

    private UltrasonicAdapter ultrasonicAdapter;
    private Ultrasonic ultrasonic;
    private List<Ultrasonic> ultrasonicList;

    private CustomPopuWindow customPopWindow;
    private SensorAdapter sensorAdapter;


    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void update(MessageEvent messageEvent) {
        switch (messageEvent.getType()) {
            case updateSensor:
//                setSensorParam();
                break;
            case updateParameter:
                setNaparmeter();
                break;
            case updateSenesorSea:
                sensorSeaList= sensorSeas.getSensorSeaList();
                sensorAdapter.setNewData(sensorSeaList);
                break;
        }
    }
    @OnClick({R.id.slideButton,R.id.tv_save_sensor,R.id.tv_sea_sensor})
    public void onViewClicked(View view){
        switch (view.getId()){
            case R.id.slideButton:
                getChosseStatus();
                break;
            case R.id.tv_save_sensor:
                postAndGet();
                toast(R.string.save_succeed);
                break;
            case R.id.tv_sea_sensor:
                showControlPopupWindow(tv_sea_senesor);
                break;
        }
    }

    public static SensorSetFragment newInstance(){return new SensorSetFragment();}
    @Override
    protected int getLayoutId() {
        return R.layout.fragmen_s_senesor;
    }

    @Override
    protected void initView() {
//        ultrasonicAdapter = new UltrasonicAdapter(R.layout.item_recycle_ulrcs);
//        GridLayoutManager gridLayoutManager =new GridLayoutManager(getAttachActivity(),4);
//        rl_ul_cs.setLayoutManager(gridLayoutManager);
//        rl_ul_cs.setAdapter(ultrasonicAdapter);
        sensorAdapter = new SensorAdapter(R.layout.item_recycle_sensor);
        GridLayoutManager gridLayoutManager =new GridLayoutManager(getAttachActivity(),4);
        rl_ul_cs.setLayoutManager(gridLayoutManager);
        rl_ul_cs.setAdapter(sensorAdapter);
        slideButton.setSmallCircleModel(
                Color.parseColor("#00FFFFFF"), Color.parseColor("#999999"),Color.parseColor("#49c265"),
                Color.parseColor("#ffffff"), Color.parseColor("#ffffff"));
        ed_imu.setFilters(new InputFilter[]{new InputFilterMinMax("3","65")});
        setEditMax();
    }

    @Override
    protected void initData() {
        tcpClient= TcpClient.getInstance(getContext(), ClientMessageDispatcher.getInstance());
        parameters=Parameters.getInstance();
        sensors=Sensors.getInstance();
        sensorSeas=SensorSeas.getInstance();
        sensorSeaList=sensorSeas.getSensorSeaList();
        getSensorParam();
        getChosseStatus();
        getNaparmeter();
        setNaparmeter();
//        setSensorParam();

    }

    /**
     * 超声弹窗
     */

    private ImageView iv_quit_c;
    private RecyclerView recyclerView_c_sensor;
    private SensorSeas sensorSeas;
    private List<SensorSea> sensorSeaList;
    public void showControlPopupWindow(View view) {
        View contentView = null;
        contentView = getAttachActivity().getLayoutInflater().from(getAttachActivity()).inflate(R.layout.dialog_sea_sensor, null);
        customPopWindow = new CustomPopuWindow.PopupWindowBuilder(getAttachActivity())
                .setView(contentView)
                .size(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                .enableOutsideTouchableDissmiss(true)// 设置点击PopupWindow之外的地方，popWindow不关闭，如果不设置这个属性或者为true，则关闭
                .setOutsideTouchable(false)//是否PopupWindow 以外触摸dissmiss
                .create()
                .showAsDropDown(view, 0, -10);
        iv_quit_c=contentView.findViewById(R.id.iv_quit_sensor);
        recyclerView_c_sensor =contentView.findViewById(R.id.recycle_c_status);
        ultrasonicAdapter = new UltrasonicAdapter(R.layout.item_recycle_ulrcs);
        GridLayoutManager gridLayoutManager =new GridLayoutManager(getAttachActivity(),4);
        rl_ul_cs.setLayoutManager(gridLayoutManager);
        rl_ul_cs.setAdapter(ultrasonicAdapter);

//        sensorAdapter = new SensorAdapter(R.layout.item_recycle_sensor);
//        GridLayoutManager gridLayoutManager =new GridLayoutManager(getAttachActivity(),1);
//        recyclerView_c_sensor.setLayoutManager(gridLayoutManager);
//        recyclerView_c_sensor.setAdapter(sensorAdapter);
//        sensorAdapter.setNewData(sensorSeaList);

        iv_quit_c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customPopWindow.dissmiss();
                toast("关闭");
            }
        });

    }
    //获取传感器参数
    private void getSensorParam(){
        BaseCmd.eSensorConfigItemOptType eSensorConfigItemOptType;
        eSensorConfigItemOptType=BaseCmd.eSensorConfigItemOptType.eSensorConfigOptTypeGetData;
        BaseCmd.reqSensorConfigOperational reqSensorConfigOperational = BaseCmd.reqSensorConfigOperational.newBuilder()
                .setType(eSensorConfigItemOptType)
                .build();
        tcpClient.sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eModuleServer), reqSensorConfigOperational);
    }
    //设置传感器参数
    private void setSensorParam(){
        sensorList=sensors.getSensorList();
        ultrasonicList=new ArrayList<>();
        for (int i=0;i<sensorList.size();i++){
            int cs=(int)Float.parseFloat(sensorList.get(i).getStaticdistance());
            switch (Integer.parseInt(sensorList.get(i).getKey())){
                case 1:
                    ultrasonic=new Ultrasonic();
                    ultrasonic.setTextNum("1");
                    ultrasonic.setTextContant(String.valueOf(cs));
                    break;
                case 2:
                    ultrasonic=new Ultrasonic();
                    ultrasonic.setTextNum("2");
                    ultrasonic.setTextContant(String.valueOf(cs));
                    break;
                case 3:
                    ultrasonic=new Ultrasonic();
                    ultrasonic.setTextNum("3");
                    ultrasonic.setTextContant(String.valueOf(cs));
                    break;
                case 4:
                    ultrasonic=new Ultrasonic();
                    ultrasonic.setTextNum("4");
                    ultrasonic.setTextContant(String.valueOf(cs));
                    break;
                case 5:
                    ultrasonic=new Ultrasonic();
                    ultrasonic.setTextNum("5");
                    ultrasonic.setTextContant(String.valueOf(cs));
                    break;
                case 6:
                    ultrasonic=new Ultrasonic();
                    ultrasonic.setTextNum("6");
                    ultrasonic.setTextContant(String.valueOf(cs));
                    break;
                case 7:
                    ultrasonic=new Ultrasonic();
                    ultrasonic.setTextNum("7");
                    ultrasonic.setTextContant(String.valueOf(cs));
                    break;
                case 8:
                    ultrasonic=new Ultrasonic();
                    ultrasonic.setTextNum("8");
                    ultrasonic.setTextContant(String.valueOf(cs));
                    break;
                case 9:
                    ultrasonic=new Ultrasonic();
                    ultrasonic.setTextNum("9");
                    ultrasonic.setTextContant(String.valueOf(cs));
                    break;
                case 10:
                    ultrasonic=new Ultrasonic();
                    ultrasonic.setTextNum("10");
                    ultrasonic.setTextContant(String.valueOf(cs));
                    break;
                case 11:
                    ultrasonic=new Ultrasonic();
                    ultrasonic.setTextNum("11");
                    ultrasonic.setTextContant(String.valueOf(cs));
                    break;
                case 12:
                    ultrasonic=new Ultrasonic();
                    ultrasonic.setTextNum("12");
                    ultrasonic.setTextContant(String.valueOf(cs));
                    break;
            }
            ultrasonicList.add(ultrasonic);

        }
        ultrasonicAdapter.setNewData(ultrasonicList);
    }
    //发送传感器参数
    private void postSensorParam(List<BaseCmd.sensorConfigItem> sensorConfigItems,int type){
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
        BaseCmd.reqSensorConfigOperational reqSensorConfigOperational=BaseCmd.reqSensorConfigOperational.newBuilder()
                .setType(eSensorConfigItemOptType)
                .addAllData(sensorConfigItems)
                .build();
        tcpClient.sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eModuleServer),reqSensorConfigOperational);
    }
    //获取导航参数
    private void getNaparmeter(){
        BaseCmd.eConfigItemOptType eConfigItemOptType;
        eConfigItemOptType=BaseCmd.eConfigItemOptType.eConfigOptTypeGetData;//获取数据
        BaseCmd.reqConfigOperational reqConfigOperational = BaseCmd.reqConfigOperational.newBuilder()
                .setType(eConfigItemOptType)
                .build();
        tcpClient.sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eModuleServer), reqConfigOperational);
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
        tcpClient.sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eModuleServer),reqConfigOperational);

    }

    /**
     * 提交时解析
     */
    private void postAndGet(){
        List<Sensor> sensorList1=new ArrayList<>();
        for (int i=0;i<sensorList.size();i++){
            Sensor sensor1=new Sensor();
            String tcontant=ultrasonicList.get(i).getTextContant();
            switch (i){
                case 0:
                    if (tcontant!=null){
                        sensor1.setKey(String.valueOf(1));
                        sensor1.setStaticdistance(tcontant);
                        sensor1.setDydistance(sensorList.get(i).getDydistance());
                    }
                    break;
                case 1:
                    if (tcontant!=null){
                        sensor1.setKey(String.valueOf(2));
                        sensor1.setStaticdistance(tcontant);
                        sensor1.setDydistance(sensorList.get(i).getDydistance());
                    }
                    break;
                case 2:
                    if (tcontant!=null){
                        sensor1.setKey(String.valueOf(3));
                        sensor1.setStaticdistance(tcontant);
                        sensor1.setDydistance(sensorList.get(i).getDydistance());
                    }
                    break;
                case 3:
                    if (tcontant!=null){
                        sensor1.setKey(String.valueOf(4));
                        sensor1.setStaticdistance(tcontant);
                        sensor1.setDydistance(sensorList.get(i).getDydistance());
                    }
                    break;
                case 4:
                    if (tcontant!=null){
                        sensor1.setKey(String.valueOf(5));
                        sensor1.setStaticdistance(tcontant);
                        sensor1.setDydistance(sensorList.get(i).getDydistance());
                    }
                    break;
                case 5:
                    if (tcontant!=null){
                        sensor1.setKey(String.valueOf(6));
                        Logger.e("上传数值"+tcontant);
                        sensor1.setStaticdistance(tcontant);
                        sensor1.setDydistance(sensorList.get(i).getDydistance());
                    }
                    break;
                case 6:
                    if (tcontant!=null){
                        sensor1.setKey(String.valueOf(7));
                        sensor1.setStaticdistance(tcontant);
                        sensor1.setDydistance(sensorList.get(i).getDydistance());
                    }
                    break;
                case 7:
                    if (tcontant!=null){
                        sensor1.setKey(String.valueOf(8));
                        sensor1.setStaticdistance(tcontant);
                        sensor1.setDydistance(sensorList.get(i).getDydistance());
                    }

                    break;
                case 8:
                    if (tcontant!=null){
                        sensor1.setKey(String.valueOf(9));
                        sensor1.setStaticdistance(tcontant);
                        sensor1.setDydistance(sensorList.get(i).getDydistance());
                    }
                    break;
                case 9:
                    if (tcontant!=null){
                        sensor1.setKey(String.valueOf(10));
                        sensor1.setStaticdistance(tcontant);
                        sensor1.setDydistance(sensorList.get(i).getDydistance());
                    }
                    break;
                case 10:
                    if (tcontant!=null){
                        sensor1.setKey(String.valueOf(11));
                        sensor1.setStaticdistance(tcontant);
                        sensor1.setDydistance(sensorList.get(i).getDydistance());
                    }
                    break;
                case 11:
                    if (tcontant!=null){
                        sensor1.setKey(String.valueOf(12));
                        sensor1.setStaticdistance(tcontant);
                        sensor1.setDydistance(sensorList.get(i).getDydistance());
                    }

                    break;
            }
            sensorList1.add(sensor1);
        }
        Logger.e("提交数量"+sensorList1.size());
        List<BaseCmd.sensorConfigItem> sensorConfigItemList=new ArrayList<>();
        for (int i=0;i<sensorList1.size();i++){
            BaseCmd.sensorConfigItem sensorConfigItem=BaseCmd.sensorConfigItem.newBuilder()
                    .setKey(ByteString.copyFromUtf8(sensorList1.get(i).getKey()))
                    .setStaticOATriggerDist(ByteString.copyFromUtf8(sensorList1.get(i).getStaticdistance()))
                    .setDynamicOATriggerDist(ByteString.copyFromUtf8(sensorList1.get(i).getDydistance()))
                    .build();

            sensorConfigItemList.add(sensorConfigItem);
        }
        postSensorParam(sensorConfigItemList,3);
        int imu=(int)Float.parseFloat(ed_imu.getText().toString());
        postNaparmeter(ByteString.copyFromUtf8(sensorKey),ByteString.copyFromUtf8(autoValue),2,3);
        postNaparmeter(ByteString.copyFromUtf8(imuKey),ByteString.copyFromUtf8(String.valueOf(imu)),2,3);
        getNaparmeter();
        getSensorParam();
    }

    //获取选择的状态
    private String autoValue;
    private void getChosseStatus(){
        boolean isChecked=slideButton.isChecked();
        if (isChecked==true){
            autoValue="1";
        }else {
            autoValue="0";
        }
        Logger.e("是否选择"+isChecked);
    }
    private void setEditMax(){
        ed_imu.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                try {
                    if (Integer.parseInt(s.toString())<30){
                        toast(R.string.n_num_between);
                    }else {
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }});
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getSensorParam();
        getChosseStatus();
        getNaparmeter();
        setNaparmeter();
//        setSensorParam();
    }
}
