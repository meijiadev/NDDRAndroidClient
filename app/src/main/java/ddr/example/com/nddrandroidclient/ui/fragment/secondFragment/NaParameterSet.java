package ddr.example.com.nddrandroidclient.ui.fragment.secondFragment;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
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
import ddr.example.com.nddrandroidclient.entity.info.MapFileStatus;
import ddr.example.com.nddrandroidclient.entity.info.NotifyBaseStatusEx;
import ddr.example.com.nddrandroidclient.entity.info.NotifyEnvInfo;
import ddr.example.com.nddrandroidclient.entity.other.Naparam;
import ddr.example.com.nddrandroidclient.entity.other.Parameter;
import ddr.example.com.nddrandroidclient.entity.other.Parameters;
import ddr.example.com.nddrandroidclient.other.Logger;
import ddr.example.com.nddrandroidclient.other.SlideButton;
import ddr.example.com.nddrandroidclient.protocobuf.dispatcher.ClientMessageDispatcher;
import ddr.example.com.nddrandroidclient.socket.TcpClient;
import ddr.example.com.nddrandroidclient.ui.adapter.NaparamAdapter;
import ddr.example.com.nddrandroidclient.widget.textview.LineTextView;

/**
 * time: 2020/03/24
 * desc: 高级设置导航参数界面
 */
public class NaParameterSet extends DDRLazyFragment{

    @BindView(R.id.tv_restartDefault)
    TextView tv_restartDefault;
    @BindView(R.id.tv_save_param)
    TextView tv_save_param;
    @BindView(R.id.recycle_naparam)
    RecyclerView recycle_naparam;

    private NaparamAdapter naparamAdapter;
    private Naparam naparam;
    private List<Naparam> naparamList;

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
    private String isFormOneKey="Common_Params.AUTO_START_FROM_SEG0";
    private String isPainHuKey="Common_Params.AUTO_NO_CORNER_SMOOTHING";
    private List<BaseCmd.configData> configDataList=new ArrayList<>();
    private SlideButton slideButton;

    public static NaParameterSet newInstance(){
        return new NaParameterSet();
    }

    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void update(MessageEvent messageEvent) {
        switch (messageEvent.getType()) {
            case updataParameter:
                setNaparmeter();
                setData();
                break;
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_s_naparam;
    }

    @Override
    protected void initView() {
        naparamAdapter = new NaparamAdapter(R.layout.item_recycle_naparam);
        LinearLayoutManager layoutManager =new LinearLayoutManager(getAttachActivity());
        recycle_naparam.setLayoutManager(layoutManager);
        recycle_naparam.setAdapter(naparamAdapter);

    }

    @Override
    protected void initData() {
        tcpClient= TcpClient.getInstance(getContext(), ClientMessageDispatcher.getInstance());
        notifyBaseStatusEx = NotifyBaseStatusEx.getInstance();
        notifyEnvInfo = NotifyEnvInfo.getInstance();
        mapFileStatus = MapFileStatus.getInstance();
        parameters=Parameters.getInstance();
        getNaparmeter(1);
        setNaparmeter();
        setData();
        onClick();

    }
    @OnClick ({R.id.tv_restartDefault,R.id.tv_save_param})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.tv_restartDefault:
                postNaparmeter(configDataList,2);
                getNaparmeter(1);
                break;
            case R.id.tv_save_param:
                postAndGet(1);
                postAndGet(2);
                getNaparmeter(1);
                toast("保存成功");
                break;
        }
    }
    //发送导航参数
    private void postNaparmeter(List<BaseCmd.configData> configDataList,int optType){
        BaseCmd.eConfigItemOptType eConfigItemOptType;
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
        BaseCmd.reqConfigOperational reqConfigOperational=BaseCmd.reqConfigOperational.newBuilder()
                .setType(eConfigItemOptType)
                .addAllData(configDataList)
                .build();
        tcpClient.sendData(null,reqConfigOperational);

    }
    //设置导航参数
    private int bz_ra,bz_sl,bz_st,isFrom,isPain;
    private void setNaparmeter(){
        parameterList=parameters.getParameterList();
        Logger.e("数量"+parameterList.size());
        for (int i=0;i<parameterList.size();i++){
            if(parameterList.get(i).getKey().contains(bzRadiusKey)){
                bz_ra=(int)(Float.parseFloat(parameterList.get(i).getValue())*100);
            }
            if(parameterList.get(i).getKey().contains(bzDistanceKey)){
                bz_sl=(int)(Float.parseFloat(parameterList.get(i).getValue())*100);
            }
            if(parameterList.get(i).getKey().contains(bzStopKey)){
                bz_st=(int)(Float.parseFloat(parameterList.get(i).getValue())*100);
            }
            if (parameterList.get(i).getKey().contains(isFormOneKey)){
                isFrom=Integer.parseInt(parameterList.get(i).getValue());
            }
            if (parameterList.get(i).getKey().contains(isPainHuKey)){
                isPain=Integer.parseInt(parameterList.get(i).getValue());
            }

        }
        Logger.e("获得数值----"+bz_ra);
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

    /**
     * 列表下的点击事件
     */
    private int mposition;
    private void onClick(){
        naparamAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            mposition=position;
            slideButton=recycle_naparam.getLayoutManager().findViewByPosition(position).findViewById(R.id.slide_is);
            switch (view.getId()){
                case R.id.slide_is:
                    slideButton=(SlideButton) view;
                    getChosseStatus();
                    Logger.e("点击-------------"+position);
                    break;
            }
            naparamAdapter.setNewData(naparamList);

        });
    }
    /**
     * 提交时解析
     */
    private Float bz_radius_text;
    private Float bz_sldown_text;
    private Float bz_stop_text;
    private int isForm_v;
    private int isPain_v;
    private void postAndGet(int type) {
        naparamAdapter.setNewData(naparamList);
        for (int i=0;i<naparamList.size();i++){
            switch (i){
                case 0:
                    bz_radius_text=Float.parseFloat(naparamList.get(i).getValue());
                    break;
                case 1:
                    bz_sldown_text=Float.parseFloat(naparamList.get(i).getValue());
                    break;
                case 2:
                    bz_stop_text=Float.parseFloat(naparamList.get(i).getValue());
                    break;
                case 3:
                    isForm_v=Integer.parseInt(naparamList.get(i).getValue());
                    break;
                case 4:
                    isPain_v=Integer.parseInt(naparamList.get(i).getValue());
                    break;

            }

        }
        List<Parameter> parameterList1 = new ArrayList<>();
        BaseCmd.eConfigItemType eConfigItemType;
        switch (type){
            case 0:
                eConfigItemType=BaseCmd.eConfigItemType.eConfigTypeError;
                break;
            case 1:
                eConfigItemType=BaseCmd.eConfigItemType.eConfigTypeCore;
                for (int i=0;i<3;i++){
                    Parameter parameter1=new Parameter();
                    Logger.e("开始添加-----------"+bz_radius_text);
                    switch (i){
                        case 0:
                            parameter1.setKey(bzRadiusKey);
                            parameter1.setValue(String.valueOf(bz_radius_text/100));
                            break;
                        case 1:
                            parameter1.setKey(bzDistanceKey);
                            parameter1.setValue(String.valueOf(bz_sldown_text/100));
                            break;
                        case 2:
                            parameter1.setKey(bzStopKey);
                            parameter1.setValue(String.valueOf(bz_stop_text/100));
                            break;
                    }
                    parameterList1.add(parameter1);
                }
                break;
            case 2:
                eConfigItemType=BaseCmd.eConfigItemType.eConfigTypeLogic;
                for (int i=0;i<2;i++){
                    Parameter parameter1=new Parameter();
                    Logger.e("开始添加-----------"+isForm_v);
                    switch (i){
                        case 0:
                            parameter1.setKey(isFormOneKey);
                            parameter1.setValue(String.valueOf(isForm_v));
                            break;
                        case 1:
                            parameter1.setKey(isPainHuKey);
                            parameter1.setValue(String.valueOf(isPain_v));
                            break;
                    }
                    parameterList1.add(parameter1);
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
//        Logger.e("存在"+ed_naparam_value+"大小"+parameterList.size());

        Logger.e("111大小----"+parameterList1.size());
        for (int i=0;i<parameterList1.size();i++){
            BaseCmd.configItem configItem=BaseCmd.configItem.newBuilder()
                    .setKey(ByteString.copyFromUtf8(parameterList1.get(i).getKey()))
                    .setValue(ByteString.copyFromUtf8(parameterList1.get(i).getValue()))
                    .build();
            BaseCmd.configData configData=BaseCmd.configData.newBuilder()
                    .setType(eConfigItemType)
                    .setData(configItem)
                    .build();
            configDataList.add(configData);
        }
        postNaparmeter(configDataList,3);


    }

    /**
     *插入数据
     */
    private void setData() {
        Logger.e("数值----"+bz_ra);
        int number = 5;
        naparamList=new ArrayList<>();
        for (int i=0;i<number;i++){
            naparam = new Naparam();
            switch (i){
                case 0:
                    naparam.setText("避障半径");
                    naparam.setTitle("（机器人安全通行的虚拟半径，建议比物理半径大10cm）");
                    naparam.setValue(String.valueOf(bz_ra));
                    break;
                case 1:
                    naparam.setText("避障开始减速距离");
                    naparam.setTitle("（运动中心到障碍距离小于此值，机器人减速前进，建议比实际半径大100cm）");
                    naparam.setValue(String.valueOf(bz_sl));
                    break;
                case 2:
                    naparam.setText("避障停止距离");
                    naparam.setTitle("（运动中心到障碍距离小于等于此值，机器人停止，建议比实际半径大30cm）");
                    naparam.setValue(String.valueOf(bz_st));
                    break;
                case 3:
                    naparam.setText("是否从第一段开始");
                    naparam.setValue(String.valueOf(isFrom));
                    break;
                case 4:
                    naparam.setText("是否画弧");
                    naparam.setValue(String.valueOf(isPain));
                    break;
            }
            naparamList.add(naparam);
        }
        naparamAdapter.setNewData(naparamList);

    }

    //获取选择的状态
    private String autoValue;
    private void getChosseStatus(){
        boolean isChecked=slideButton.isChecked;
        if (isChecked==true){
            autoValue="1";
        }else {
            autoValue="0";
        }
        naparamList.get(mposition).setValue(autoValue);
        Logger.e("是否选择"+isChecked);
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
