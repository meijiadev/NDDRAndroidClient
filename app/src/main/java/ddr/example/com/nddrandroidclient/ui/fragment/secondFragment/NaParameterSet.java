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
    @BindView(R.id.tv_save_param)
    TextView tv_save_param;

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
        parameters=Parameters.getInstance();
        getNaparmeter(1);
    }
    @OnClick ({R.id.tv_restartDefault,R.id.tv_save_param})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.tv_restartDefault:
                postNaparmeter(ByteString.copyFromUtf8(bzRadiusKey),ByteString.copyFromUtf8("30"),1,2);
                postNaparmeter(ByteString.copyFromUtf8(bzDistanceKey),ByteString.copyFromUtf8("30"),1,2);
                postNaparmeter(ByteString.copyFromUtf8(bzStopKey),ByteString.copyFromUtf8("30"),1,2);
                getNaparmeter(1);
                break;
            case R.id.tv_save_param:
                float bz_ra=Float.parseFloat(ed_bzRadius.getText().toString())/100;//半径
                float bz_dis=Float.parseFloat(ed_slowDown.getText().toString())/100;//减速距离
                float bz_st=Float.parseFloat(ed_bzStop.getText().toString())/100;//停止距离
                postNaparmeter(ByteString.copyFromUtf8(bzRadiusKey),ByteString.copyFromUtf8(String.valueOf(bz_ra)),1,3);
                postNaparmeter(ByteString.copyFromUtf8(bzDistanceKey),ByteString.copyFromUtf8(String.valueOf(bz_dis)),1,3);
                postNaparmeter(ByteString.copyFromUtf8(bzStopKey),ByteString.copyFromUtf8(String.valueOf(bz_st)),1,3);
                getNaparmeter(1);
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
                int bz_ra=(int)(Float.parseFloat(parameterList.get(i).getValue())*100);
                ed_bzRadius.setText(String.valueOf(bz_ra));
            }
            if(parameterList.get(i).getKey().contains(bzDistanceKey)){
                int bz_sl=(int)(Float.parseFloat(parameterList.get(i).getValue())*100);
                ed_slowDown.setText(String.valueOf(bz_sl));
            }
            if(parameterList.get(i).getKey().contains(bzStopKey)){
                int bz_st=(int)(Float.parseFloat(parameterList.get(i).getValue())*100);
                ed_bzStop.setText(String.valueOf(bz_st));
            }

        }
    }

    /**
     * 获取导航参数
     */
    private void getNaparmeter(int type){
        Logger.e("-------------获取");
        BaseCmd.eConfigItemOptType eConfigItemOptType;
        switch (type){
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
                throw new IllegalStateException("Unexpected value: " + type);
        }
        BaseCmd.reqConfigOperational reqConfigOperational = BaseCmd.reqConfigOperational.newBuilder()
                .setType(eConfigItemOptType)
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
