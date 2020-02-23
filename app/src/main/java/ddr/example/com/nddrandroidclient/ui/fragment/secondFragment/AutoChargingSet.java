package ddr.example.com.nddrandroidclient.ui.fragment.secondFragment;

import android.graphics.Color;
import android.location.LocationManager;
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
import ddr.example.com.nddrandroidclient.entity.info.MapFileStatus;
import ddr.example.com.nddrandroidclient.entity.info.NotifyBaseStatusEx;
import ddr.example.com.nddrandroidclient.entity.info.NotifyEnvInfo;
import ddr.example.com.nddrandroidclient.entity.other.Parameter;
import ddr.example.com.nddrandroidclient.entity.other.Parameters;
import ddr.example.com.nddrandroidclient.other.Logger;
import ddr.example.com.nddrandroidclient.other.SlideButton;
import ddr.example.com.nddrandroidclient.protocobuf.dispatcher.ClientMessageDispatcher;
import ddr.example.com.nddrandroidclient.socket.TcpClient;

public class AutoChargingSet extends DDRLazyFragment {
    @BindView(R.id.slideButton)
    SlideButton slideButton;
    @BindView(R.id.ed_trigger_auto)
    EditText ed_trigger_auto;
    @BindView(R.id.ed_out_auto)
    EditText ed_out_auto;

    private TcpClient tcpClient;
    private NotifyBaseStatusEx notifyBaseStatusEx;
    private NotifyEnvInfo notifyEnvInfo;
    private Parameter parameter;
    private Parameters parameters;
    private List<Parameter> parameterList=new ArrayList<>();
    private String triggerAutoKey="MR_Params.RECHARGING_BATT_LO_PER";
    private String outAutoKey="MR_Params.RECHARGING_BATT_HI_PER";
    private String swithAutoKey="Common_Params.AUTO_ENTER_RECHARGING";
    private String autoValue="1";

    public static AutoChargingSet newInstance(){
        return new AutoChargingSet();
    }

    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void update(MessageEvent messageEvent) {
        switch (messageEvent.getType()) {
            case updataParameter:
                setNaparmeter();
                break;
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_s_autocharg;
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
        notifyBaseStatusEx = NotifyBaseStatusEx.getInstance();
        notifyEnvInfo = NotifyEnvInfo.getInstance();
        getNaparmeter();
        parameters=Parameters.getInstance();
        getChosseStatus();
    }

    @OnClick({R.id.slideButton})
    public void onViewClicked(View view){
        switch (view.getId()){
            case R.id.slideButton:
                getChosseStatus();
                postNaparmeter(ByteString.copyFromUtf8(swithAutoKey),ByteString.copyFromUtf8(autoValue),2,3);
                break;
        }
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
            if(parameterList.get(i).getKey().contains(triggerAutoKey)){
                ed_trigger_auto.setText(parameterList.get(i).getdValue());
            }
            if(parameterList.get(i).getKey().contains(outAutoKey)){
                ed_out_auto.setText(parameterList.get(i).getdValue());
            }
            if(parameterList.get(i).getKey().contains(swithAutoKey)){
                if (parameterList.get(i).getValue().equals("1")){
                    slideButton.setChecked(true);
                }else {
                    slideButton.setChecked(false);
                }
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
    private void getChosseStatus(){
        boolean isChecked=slideButton.isChecked;
        if (isChecked==true){
            autoValue="1";
        }else {
            autoValue="0";
        }
        Logger.e("是否选择"+isChecked);
    }
}
