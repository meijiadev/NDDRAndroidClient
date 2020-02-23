package ddr.example.com.nddrandroidclient.ui.fragment.secondFragment;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

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
import ddr.example.com.nddrandroidclient.protocobuf.dispatcher.ClientMessageDispatcher;
import ddr.example.com.nddrandroidclient.socket.TcpClient;

public class NaParameterSet extends DDRLazyFragment{

    @BindView(R.id.ed_bzRadius)
    EditText ed_bzRadius;
    @BindView(R.id.ed_bzStop)
    EditText ed_bzStop;
    @BindView(R.id.ed_slowDown)
    EditText ed_slowDown;
    @BindView(R.id.tv_restartDefault)
    TextView tv_restartDefault;

    private TcpClient tcpClient;
    private NotifyBaseStatusEx notifyBaseStatusEx;
    private NotifyEnvInfo notifyEnvInfo;
    private MapFileStatus mapFileStatus;
    private Parameter parameter;
    private Parameters parameters;
    private List<Parameter> parameterList=new ArrayList<>();
    private String bzRadiusKey="PPOAC_Params.OA_OBS_RADIUS";
    private String bzDistanceKey="PPOAC_Params.OA_DETECT_DISTANCE";
    private String bzStopKey="PPOAC_Params.OA_MIN_DETECTDIST";

    public static NaParameterSet newInstance(){
        return new NaParameterSet();
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
        return R.layout.fragment_s_naparam;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        tcpClient= TcpClient.getInstance(getContext(), ClientMessageDispatcher.getInstance());
        notifyBaseStatusEx = NotifyBaseStatusEx.getInstance();
        notifyEnvInfo = NotifyEnvInfo.getInstance();
        mapFileStatus = MapFileStatus.getInstance();
        getNaparmeter();
        parameters=Parameters.getInstance();
    }
    @OnClick ({R.id.tv_restartDefault})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.tv_restartDefault:
                postNaparmeter(ByteString.copyFromUtf8(bzRadiusKey),ByteString.copyFromUtf8("30"),1,2);
                postNaparmeter(ByteString.copyFromUtf8(bzDistanceKey),ByteString.copyFromUtf8("30"),1,2);
                postNaparmeter(ByteString.copyFromUtf8(bzStopKey),ByteString.copyFromUtf8("30"),1,2);
                break;
        }
    }
    //发送导航参数
    private void postNaparmeter(ByteString key,ByteString value, int type,int optType){
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
    //设置导航参数
    private void setNaparmeter(){
        parameterList=parameters.getParameterList();
        Logger.e("数量"+parameterList.size());
        for (int i=0;i<parameterList.size();i++){
            if(parameterList.get(i).getKey().contains(bzRadiusKey)){
                ed_bzRadius.setText(parameterList.get(i).getdValue());
            }
            if(parameterList.get(i).getKey().contains(bzDistanceKey)){
                ed_slowDown.setText(parameterList.get(i).getdValue());
            }
            if(parameterList.get(i).getKey().contains(bzStopKey)){
                ed_bzStop.setText(parameterList.get(i).getdValue());
            }

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
    @Override
    public void onResume() {
        Logger.e("-----------------跳转");
        super.onResume();
    }

    @Override
    public void onPause() {
        Logger.e("-----------------跳转");
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Logger.e("-----------------跳转");
    }
}
