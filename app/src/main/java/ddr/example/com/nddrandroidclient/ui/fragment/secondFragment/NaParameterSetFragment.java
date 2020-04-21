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
import androidx.recyclerview.widget.LinearLayoutManager;
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
import ddr.example.com.nddrandroidclient.protocobuf.CmdSchedule;
import ddr.example.com.nddrandroidclient.protocobuf.dispatcher.ClientMessageDispatcher;
import ddr.example.com.nddrandroidclient.socket.TcpClient;
import ddr.example.com.nddrandroidclient.ui.adapter.NaparamAdapter;

/**
 * time: 2020/03/24
 * desc: 高级设置导航参数界面
 */
public class NaParameterSetFragment extends DDRLazyFragment  {

    @BindView(R.id.tv_restartDefault)
    TextView tv_restartDefault;
    @BindView(R.id.tv_save_param)
    TextView tv_save_param;
    @BindView(R.id.tv_task_origin)
    TextView tvTaskOrigin;
    @BindView(R.id.tv_task_nearby)
    TextView tvTaskNearby;
    @BindView(R.id.tv_navigation_loop)
    TextView tvNavigationLoop;
    @BindView(R.id.tv_return_to_loop)
    TextView tvReturnToLoop;
    @BindView(R.id.tv_target_corner)
    TextView tvTargetCorner;
    @BindView(R.id.tv_smart_smooth_turn)
    TextView tvSmartSmoothTurn;
    @BindView(R.id.ed_bzRadius)
    EditText edBzRadius;
    @BindView(R.id.tv_cm)
    TextView tvCm;
    @BindView(R.id.et_deceleration_distance)
    EditText etDecelerationDistance;
    @BindView(R.id.et_stop_distance)
    EditText etStopDistance;

    private Naparam naparam;
    private List<Naparam> naparamList;

    private TcpClient tcpClient;
    private NotifyBaseStatusEx notifyBaseStatusEx;
    private NotifyEnvInfo notifyEnvInfo;
    private MapFileStatus mapFileStatus;
    private Parameter parameter;
    private Parameters parameters;
    private List<Parameter> parameterList = new ArrayList<>();
    private String bzRadiusKey = "PPOAC_Params.OA_OBS_RADIUS";         // 避障半径
    private String bzDistanceKey = "PPOAC_Params.OA_DETECT_DISTANCE";  // 避障开始减速距离
    private String bzStopKey = "PPOAC_Params.OA_MIN_DETECTDIST";       // 避障停止距离
    private String isFormOneKey = "Common_Params.AUTO_START_FROM_SEG0";    // 是否从第一段开始 --》任务启动方式 1-表示会从第一段开始
    private String isPainHuKey = "Common_Params.AUTO_NO_CORNER_SMOOTHING"; // 是否不画弧                       1-表示不画弧
    private String isOriginalWayBack="Common_Params.AUTOMODE_RETURN";           // 是否原路返回                1-原路返回
    private List<BaseCmd.configData> configDataList = new ArrayList<>();

    public static NaParameterSetFragment newInstance() {
        return new NaParameterSetFragment();
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void update(MessageEvent messageEvent) {
        switch (messageEvent.getType()) {
            case updateParameter:
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
        tcpClient = TcpClient.getInstance(getContext(), ClientMessageDispatcher.getInstance());
        notifyBaseStatusEx = NotifyBaseStatusEx.getInstance();
        notifyEnvInfo = NotifyEnvInfo.getInstance();
        mapFileStatus = MapFileStatus.getInstance();
        parameters = Parameters.getInstance();
        getNaParmeter(1);
        setNaparmeter();
    }

    @OnClick({R.id.tv_restartDefault, R.id.tv_save_param,R.id.tv_task_origin, R.id.tv_task_nearby, R.id.tv_navigation_loop, R.id.tv_return_to_loop, R.id.tv_target_corner, R.id.tv_smart_smooth_turn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_restartDefault:
                postNaparmeter(configDataList, 2);
                getNaParmeter(1);
                break;
            case R.id.tv_save_param:
                postAndGet(1);
                postAndGet(2);
                //getNaParmeter(1);

                toast("保存成功");
                break;
            case R.id.tv_task_origin:
                tvTaskOrigin.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.checkedwg),null,null,null);
                tvTaskNearby.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.nocheckedwg),null,null,null);
                isFrom=1;
                break;
            case R.id.tv_task_nearby:
                tvTaskOrigin.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.nocheckedwg),null,null,null);
                tvTaskNearby.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.checkedwg),null,null,null);
                isFrom=0;
                break;
            case R.id.tv_navigation_loop:
                isOriginal=0;
                tvReturnToLoop.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.nocheckedwg),null,null,null);
                tvNavigationLoop.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.checkedwg),null,null,null);
                break;
            case R.id.tv_return_to_loop:
                isOriginal=1;
                tvReturnToLoop.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.checkedwg),null,null,null);
                tvNavigationLoop.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.nocheckedwg),null,null,null);
                break;
            case R.id.tv_target_corner:
                isPain=1;
                tvTargetCorner.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.checkedwg),null,null,null);
                tvSmartSmoothTurn.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.nocheckedwg),null,null,null);
                break;
            case R.id.tv_smart_smooth_turn:
                isPain=0;
                tvTargetCorner.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.nocheckedwg),null,null,null);
                tvSmartSmoothTurn.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.checkedwg),null,null,null);
                break;
        }
    }

    /**
     * 获取导航参数
     *
     */
    private void getNaParmeter(int type) {
        BaseCmd.eConfigItemOptType eConfigItemOptType;
        switch (type) {
            case 0:
                eConfigItemOptType = BaseCmd.eConfigItemOptType.eConfigOptTypeError;//全部
                break;
            case 1:
                eConfigItemOptType = BaseCmd.eConfigItemOptType.eConfigOptTypeGetData;//获取数据
                break;
            case 2:
                eConfigItemOptType = BaseCmd.eConfigItemOptType.eConfigOptTypeResumeData;//恢复数据
                break;
            case 3:
                eConfigItemOptType = BaseCmd.eConfigItemOptType.eConfigOptTypeSetData;//设置数据
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }

        BaseCmd.reqConfigOperational reqConfigOperational = BaseCmd.reqConfigOperational.newBuilder()
                .setType(eConfigItemOptType)
                .build();
        BaseCmd.CommonHeader commonHeader = BaseCmd.CommonHeader.newBuilder()
                .setFromCltType(BaseCmd.eCltType.eLocalAndroidClient)
                .setToCltType(BaseCmd.eCltType.eModuleServer)
                .addFlowDirection(BaseCmd.CommonHeader.eFlowDir.Forward)
                .build();
        tcpClient.sendData(commonHeader, reqConfigOperational);
    }


    /**
     *上传数据
     * @param configDataList
     * @param optType 1-获取数据 2-恢复数据 3-设置数据
     */
    private void postNaparmeter(List<BaseCmd.configData> configDataList, int optType) {
        BaseCmd.eConfigItemOptType eConfigItemOptType;
        switch (optType) {
            case 0:
                eConfigItemOptType = BaseCmd.eConfigItemOptType.eConfigOptTypeError;//全部
                break;
            case 1:
                eConfigItemOptType = BaseCmd.eConfigItemOptType.eConfigOptTypeGetData;//获取数据
                break;
            case 2:
                eConfigItemOptType = BaseCmd.eConfigItemOptType.eConfigOptTypeResumeData;//恢复数据
                break;
            case 3:
                eConfigItemOptType = BaseCmd.eConfigItemOptType.eConfigOptTypeSetData;//设置数据
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + optType);
        }
        BaseCmd.reqConfigOperational reqConfigOperational = BaseCmd.reqConfigOperational.newBuilder()
                .setType(eConfigItemOptType)
                .addAllData(configDataList)
                .build();
        tcpClient.sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eModuleServer), reqConfigOperational);

    }

    //设置导航参数
    private int bz_ra, bz_sl, bz_st, isFrom, isPain, isOriginal;

    /**
     * 筛选配置参数
     */
    private void setNaparmeter() {
        parameterList = parameters.getParameterList();
        Logger.e("数量" + parameterList.size());
        for (int i = 0; i < parameterList.size(); i++) {
            if (parameterList.get(i).getKey().contains(bzRadiusKey)) {
                bz_ra = (int) (Float.parseFloat(parameterList.get(i).getValue()) * 100);
                edBzRadius.setText(String.valueOf(bz_ra));
            }
            if (parameterList.get(i).getKey().contains(bzDistanceKey)) {
                bz_sl = (int) (Float.parseFloat(parameterList.get(i).getValue()) * 100);
                etDecelerationDistance.setText(String.valueOf(bz_sl));
            }
            if (parameterList.get(i).getKey().contains(bzStopKey)) {
                bz_st = (int) (Float.parseFloat(parameterList.get(i).getValue()) * 100);
                etStopDistance.setText(String.valueOf(bz_st));
            }
            //是否从第一段开始--》任务启动方式 1-任务起点启动
            if (parameterList.get(i).getKey().contains(isFormOneKey)) {
                isFrom = Integer.parseInt(parameterList.get(i).getValue());
                Logger.e("是否从第一端开始："+isFrom);
                if (isFrom==1){
                    tvTaskOrigin.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.checkedwg),null,null,null);
                    tvTaskNearby.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.nocheckedwg),null,null,null);
                }else {
                    tvTaskOrigin.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.nocheckedwg),null,null,null);
                    tvTaskNearby.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.checkedwg),null,null,null);
                }
            }
            // 是否不画弧   1-不画弧
            if (parameterList.get(i).getKey().contains(isPainHuKey)) {
                isPain = Integer.parseInt(parameterList.get(i).getValue());
                Logger.e("是否从画弧："+isPain);
                if (isPain==1){
                    tvTargetCorner.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.checkedwg),null,null,null);
                    tvSmartSmoothTurn.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.nocheckedwg),null,null,null);
                }else {
                    tvTargetCorner.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.nocheckedwg),null,null,null);
                    tvSmartSmoothTurn.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.checkedwg),null,null,null);
                }
            }
            // 是否原路返回 1-表示原路返回
            if (parameterList.get(i).getKey().equals(isOriginalWayBack)){
                isOriginal= Integer.parseInt(parameterList.get(i).getValue());
                Logger.e("是否原路返回："+isOriginal);
                if (isOriginal==1){
                    tvReturnToLoop.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.checkedwg),null,null,null);
                    tvNavigationLoop.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.nocheckedwg),null,null,null);
                }else {
                    tvReturnToLoop.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.nocheckedwg),null,null,null);
                    tvNavigationLoop.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.checkedwg),null,null,null);
                }
            }
        }

        int number = 6;
        naparamList = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            naparam = new Naparam();
            switch (i) {
                case 0:
                    naparam.setValue(String.valueOf(bz_ra));
                    break;
                case 1:
                    naparam.setValue(String.valueOf(bz_sl));
                    break;
                case 2:
                    naparam.setValue(String.valueOf(bz_st));
                    break;
                case 3:
                    naparam.setValue(String.valueOf(isFrom));
                    break;
                case 4:
                    naparam.setValue(String.valueOf(isPain));
                    break;
                case 5:
                    naparam.setValue(String.valueOf(isOriginal));
                    break;
            }
            naparamList.add(naparam);
        }
    }



    /**
     * 提交时解析
     */
    private Float bz_radius_text;
    private Float bz_sldown_text;
    private Float bz_stop_text;
    /**
     *
     * @param type
     */
    private void  postAndGet(int type) {
        try {
            bz_radius_text = Float.parseFloat(edBzRadius.getText().toString());
            bz_sldown_text = Float.parseFloat(etDecelerationDistance.getText().toString());
            bz_stop_text = Float.parseFloat(etStopDistance.getText().toString());
        }catch (Exception e){
            e.printStackTrace();
        }
        List<Parameter> parameterList1 =new ArrayList<>();
        BaseCmd.eConfigItemType eConfigItemType;
        switch (type) {
            case 0:
                eConfigItemType = BaseCmd.eConfigItemType.eConfigTypeError;
                break;
            case 1:

                eConfigItemType = BaseCmd.eConfigItemType.eConfigTypeCore;
                for (int i = 0; i < 3; i++) {
                    Parameter parameter1 = new Parameter();
                    switch (i) {
                        case 0:
                            parameter1.setKey(bzRadiusKey);
                            parameter1.setValue(String.valueOf(bz_radius_text / 100));
                            break;
                        case 1:
                            parameter1.setKey(bzDistanceKey);
                            parameter1.setValue(String.valueOf(bz_sldown_text / 100));
                            break;
                        case 2:
                            parameter1.setKey(bzStopKey);
                            parameter1.setValue(String.valueOf(bz_stop_text / 100));
                            break;
                    }
                    parameterList1.add(parameter1);
                }
                break;
            case 2:
                eConfigItemType = BaseCmd.eConfigItemType.eConfigTypeLogic;
                for (int i = 0; i < 3; i++) {
                    Parameter parameter1 = new Parameter();
                    switch (i) {
                        case 0:
                            parameter1.setKey(isFormOneKey);
                            parameter1.setValue(String.valueOf(isFrom));
                            break;
                        case 1:
                            parameter1.setKey(isPainHuKey);
                            parameter1.setValue(String.valueOf(isPain));
                            break;
                        case 2:
                            parameter1.setKey(isOriginalWayBack);
                            parameter1.setValue(String.valueOf(isOriginal));
                            break;

                    }
                    parameterList1.add(parameter1);
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
        configDataList=new ArrayList<>();
        for (int i = 0; i < parameterList1.size(); i++) {
            BaseCmd.configItem configItem = BaseCmd.configItem.newBuilder()
                    .setKey(ByteString.copyFromUtf8(parameterList1.get(i).getKey()))
                    .setValue(ByteString.copyFromUtf8(parameterList1.get(i).getValue()))
                    .build();
            BaseCmd.configData configData = BaseCmd.configData.newBuilder()
                    .setType(eConfigItemType)
                    .setData(configItem)
                    .build();
            configDataList.add(configData);
            Logger.e("-----value:"+parameterList1.get(i).getValue());
        }
        postNaparmeter(configDataList, 3);

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
