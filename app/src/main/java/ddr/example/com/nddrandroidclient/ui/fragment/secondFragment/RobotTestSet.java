package ddr.example.com.nddrandroidclient.ui.fragment.secondFragment;

import android.view.View;
import android.widget.TextView;

import DDRCommProto.BaseCmd;
import butterknife.BindView;
import butterknife.OnClick;
import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.common.DDRLazyFragment;
import ddr.example.com.nddrandroidclient.entity.info.NotifyBaseStatusEx;
import ddr.example.com.nddrandroidclient.entity.info.NotifyEnvInfo;
import ddr.example.com.nddrandroidclient.entity.other.NotifyHardState;
import ddr.example.com.nddrandroidclient.other.Logger;
import ddr.example.com.nddrandroidclient.protocobuf.dispatcher.ClientMessageDispatcher;
import ddr.example.com.nddrandroidclient.socket.TcpClient;

public class RobotTestSet extends DDRLazyFragment {

    @BindView(R.id.one_test)
    TextView one_test;
    @BindView(R.id.tv_dj_state)
    TextView tv_dj_state;
    @BindView(R.id.tv_dj_time)
    TextView tv_dj_time;
    @BindView(R.id.tv_dj_test)
    TextView tv_dj_test;
    @BindView(R.id.tv_photo_state)
    TextView tv_photo_state;
    @BindView(R.id.tv_photo_time)
    TextView tv_photo_time;
    @BindView(R.id.tv_photo_test)
    TextView tv_photo_test;
    @BindView(R.id.tv_ld_state)
    TextView tv_ld_state;
    @BindView(R.id.tv_ld_time)
    TextView tv_ld_time;
    @BindView(R.id.tv_ld_test)
    TextView tv_ld_test;
    @BindView(R.id.tv_rgbd_state)
    TextView tv_rgbd_state;
    @BindView(R.id.tv_rgbd_time)
    TextView tv_rgbd_time;
    @BindView(R.id.tv_rgbd_test)
    TextView tv_rgbd_test;
    @BindView(R.id.tv_xrgbd_state)
    TextView tv_xrgbd_state;
    @BindView(R.id.tv_xrgbd_time)
    TextView tv_xrgbd_time;
    @BindView(R.id.tv_xrgbd_test)
    TextView tv_xrgbd_test;
    @BindView(R.id.tv_qr_state)
    TextView tv_qr_state;
    @BindView(R.id.tv_qr_time)
    TextView tv_qr_time;
    @BindView(R.id.tv_qr_test)
    TextView tv_qr_test;

    private TcpClient tcpClient;
    private NotifyBaseStatusEx notifyBaseStatusEx;
    private NotifyEnvInfo notifyEnvInfo;
    private NotifyHardState notifyHardState;



    public static RobotTestSet newInstance(){return new RobotTestSet();}
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_s_robottest;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        tcpClient= TcpClient.getInstance(getContext(), ClientMessageDispatcher.getInstance());
        notifyBaseStatusEx = NotifyBaseStatusEx.getInstance();
        notifyEnvInfo = NotifyEnvInfo.getInstance();
        notifyHardState=NotifyHardState.getInstance();
        postHardState();
        getHardState(1);
        getHardState(2);
        getHardState(3);

    }
    @OnClick({R.id.tv_qr_test,R.id.tv_rgbd_test,R.id.tv_ld_test,R.id.tv_dj_test,R.id.tv_photo_test,R.id.tv_xrgbd_test,R.id.one_test})
    public void onViewClicked(View view){
        postHardState();
        switch (view.getId()){
            case R.id.one_test://一键自检
                getHardState(1);
                getHardState(2);
                getHardState(3);
                break;
            case R.id.tv_dj_test://电机
                break;
            case R.id.tv_ld_test://雷达
                getHardState(2);
                break;
            case R.id.tv_photo_test://摄像头
                getHardState(3);
                break;
            case R.id.tv_rgbd_test://上RGBD
                break;
            case R.id.tv_xrgbd_test://下RGBD
                break;
            case R.id.tv_qr_test://嵌入式
                getHardState(1);
                break;
        }
    }



    /**
     * 请求自检
     */
    private void postHardState() {
        BaseCmd.reqHardwareCheck reqHardwareCheck = BaseCmd.reqHardwareCheck.newBuilder()
                .build();
        BaseCmd.CommonHeader commonHeader = BaseCmd.CommonHeader.newBuilder()
                .setFromCltType(BaseCmd.eCltType.eLocalAndroidClient)
                .setToCltType(BaseCmd.eCltType.eLSMSlamNavigation)
                .addFlowDirection(BaseCmd.CommonHeader.eFlowDir.Forward)
                .build();
        tcpClient.sendData(commonHeader, reqHardwareCheck);
    }
    /**
     * 获取自检信息
     */
    private void getHardState(int type){
        for (int i=0;i<notifyHardState.getHardwareStatItemList().size();i++){
            if (type==notifyHardState.getHardwareStatItemList().get(i).getTypeValue()){
                switch (type){
                    case 1://嵌入式
                        if (notifyHardState.getHardwareStatItemList().get(i).getStatValue()==1){
                            tv_qr_state.setText("正常");
                            tv_qr_test.setBackgroundResource(R.drawable.status_button);
                        }else {
                            tv_qr_test.setBackgroundResource(R.drawable.robot_test_bg);
                            tv_qr_state.setText("异常");
                        }
                        tv_qr_test.setText("自检完成");
                        Logger.e("时间"+notifyHardState.getHardwareStatItemList().get(i).getDate().toStringUtf8());
                        tv_qr_time.setText(notifyHardState.getHardwareStatItemList().get(i).getDate().toStringUtf8().substring(0,16).replace("-"," "));
                        break;
                    case 2://激光雷达
                        if (notifyHardState.getHardwareStatItemList().get(i).getStatValue()==1){
                            tv_ld_state.setText("正常");
                            tv_ld_test.setBackgroundResource(R.drawable.status_button);
                        }else {
                            tv_ld_state.setText("异常");
                            tv_ld_test.setBackgroundResource(R.drawable.robot_test_bg);
                        }
                        tv_ld_test.setText("自检完成");
                        Logger.e("时间"+notifyHardState.getHardwareStatItemList().get(i).getDate().toStringUtf8());
                        tv_ld_time.setText(notifyHardState.getHardwareStatItemList().get(i).getDate().toStringUtf8().substring(0,16).replace("-"," "));
                        break;
                    case 3://摄像头
                        if (notifyHardState.getHardwareStatItemList().get(i).getStatValue()==1){
                            tv_photo_state.setText("正常");
                            tv_photo_test.setBackgroundResource(R.drawable.status_button);
                        }else {
                            tv_photo_state.setText("异常");
                            tv_photo_test.setBackgroundResource(R.drawable.robot_test_bg);
                        }
                        tv_photo_test.setText("自检完成");
                        Logger.e("时间"+notifyHardState.getHardwareStatItemList().get(i).getDate().toStringUtf8());
                        tv_photo_time.setText(notifyHardState.getHardwareStatItemList().get(i).getDate().toStringUtf8().substring(0,16).replace("-"," "));
                        break;
                }
            }

        }


    }
    private void setHardState(){

    }

}
