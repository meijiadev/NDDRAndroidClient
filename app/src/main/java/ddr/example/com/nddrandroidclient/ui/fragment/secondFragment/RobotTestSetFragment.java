package ddr.example.com.nddrandroidclient.ui.fragment.secondFragment;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import ddr.example.com.nddrandroidclient.entity.info.NotifyBaseStatusEx;
import ddr.example.com.nddrandroidclient.entity.info.NotifyEnvInfo;
import ddr.example.com.nddrandroidclient.entity.other.NotifyHardState;
import ddr.example.com.nddrandroidclient.entity.other.RobotTest;
import ddr.example.com.nddrandroidclient.other.Logger;
import ddr.example.com.nddrandroidclient.protocobuf.dispatcher.ClientMessageDispatcher;
import ddr.example.com.nddrandroidclient.socket.TcpClient;
import ddr.example.com.nddrandroidclient.ui.adapter.RobotTestAdapter;

/**
 * time: 2020/03/24
 * desc: 高级设置机器检测界面
 */
public class RobotTestSetFragment extends DDRLazyFragment {

    @BindView(R.id.one_test)
    TextView one_test;
    @BindView(R.id.recycle_robot_test)
    RecyclerView recycle_robot_test;

    private TcpClient tcpClient;
    private NotifyBaseStatusEx notifyBaseStatusEx;
    private NotifyEnvInfo notifyEnvInfo;
    private NotifyHardState notifyHardState;

    private RobotTestAdapter robotTestAdapter;
    private RobotTest robotTest;
    private List<RobotTest> robotTestList;

    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void update(MessageEvent messageEvent) {
        switch (messageEvent.getType()) {
            case updataHardState:
                setData();
                break;
        }

    }

    public static RobotTestSetFragment newInstance(){return new RobotTestSetFragment();}
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_s_robottest;
    }

    @Override
    protected void initView() {
        robotTestAdapter = new RobotTestAdapter(R.layout.item_robot_test);
        LinearLayoutManager layoutManager =new LinearLayoutManager(getAttachActivity());
        recycle_robot_test.setLayoutManager(layoutManager);
        recycle_robot_test.setAdapter(robotTestAdapter);
    }

    @Override
    protected void initData() {
        tcpClient= TcpClient.getInstance(getContext(), ClientMessageDispatcher.getInstance());
        notifyBaseStatusEx = NotifyBaseStatusEx.getInstance();
        notifyEnvInfo = NotifyEnvInfo.getInstance();
        notifyHardState=NotifyHardState.getInstance();
        postHardState();

    }
    @OnClick({R.id.one_test})
    public void onViewClicked(View view){
        postHardState();
        switch (view.getId()){
            case R.id.one_test://一键自检
                break;
        }
    }



    /**
     * 请求自检
     */
    private void postHardState() {
        Logger.e("请求自检----");
        BaseCmd.reqHardwareCheck reqHardwareCheck = BaseCmd.reqHardwareCheck.newBuilder()
                .build();
        BaseCmd.CommonHeader commonHeader = BaseCmd.CommonHeader.newBuilder()
                .setFromCltType(BaseCmd.eCltType.eLocalAndroidClient)
                .setToCltType(BaseCmd.eCltType.eLSMSlamNavigation)
                .addFlowDirection(BaseCmd.CommonHeader.eFlowDir.Forward)
                .build();
        tcpClient.sendData(commonHeader, reqHardwareCheck);
    }
//    /**
//     * 获取自检信息
//     */
//    private void getHardState(int type){
//        Logger.e("获取到的自检项目"+notifyHardState.getHardwareStatItemList().size());
//        for (int i=0;i<notifyHardState.getHardwareStatItemList().size();i++){
//            if (type==notifyHardState.getHardwareStatItemList().get(i).getTypeValue()){
//                switch (type){
//                    case 1://嵌入式
//                        if (notifyHardState.getHardwareStatItemList().get(i).getStatValue()==1){
//                            tv_qr_state.setText("正常");
//                            tv_qr_test.setBackgroundResource(R.drawable.status_button);
//                        }else {
//                            tv_qr_test.setBackgroundResource(R.drawable.robot_test_bg);
//                            tv_qr_state.setText("异常");
//                        }
//                        tv_qr_test.setText("自检完成");
//                        Logger.e("时间"+notifyHardState.getHardwareStatItemList().get(i).getDate().toStringUtf8());
//                        tv_qr_time.setText(notifyHardState.getHardwareStatItemList().get(i).getDate().toStringUtf8().substring(0,16).replace("-"," "));
//                        break;
//                    case 2://激光雷达
//                        if (notifyHardState.getHardwareStatItemList().get(i).getStatValue()==1){
//                            tv_ld_state.setText("正常");
//                            tv_ld_test.setBackgroundResource(R.drawable.status_button);
//                        }else {
//                            tv_ld_state.setText("异常");
//                            tv_ld_test.setBackgroundResource(R.drawable.robot_test_bg);
//                        }
//                        tv_ld_test.setText("自检完成");
//                        Logger.e("时间"+notifyHardState.getHardwareStatItemList().get(i).getDate().toStringUtf8());
//                        tv_ld_time.setText(notifyHardState.getHardwareStatItemList().get(i).getDate().toStringUtf8().substring(0,16).replace("-"," "));
//                        break;
//                    case 3://摄像头
//                        if (notifyHardState.getHardwareStatItemList().get(i).getStatValue()==1){
//                            tv_photo_state.setText("正常");
//                            tv_photo_test.setBackgroundResource(R.drawable.status_button);
//                        }else {
//                            tv_photo_state.setText("异常");
//                            tv_photo_test.setBackgroundResource(R.drawable.robot_test_bg);
//                        }
//                        tv_photo_test.setText("自检完成");
//                        Logger.e("时间"+notifyHardState.getHardwareStatItemList().get(i).getDate().toStringUtf8());
//                        tv_photo_time.setText(notifyHardState.getHardwareStatItemList().get(i).getDate().toStringUtf8().substring(0,16).replace("-"," "));
//                        break;
//                }
//            }
//
//        }

//
//    }

    /**
     * 插入数据
     */
    private void setData(){
        robotTestList=new ArrayList<>();
        for (int i=0;i<notifyHardState.getHardwareStatItemList().size();i++){
            robotTest=new RobotTest();
                switch (notifyHardState.getHardwareStatItemList().get(i).getTypeValue()) {
                    case 1://嵌入式
                        robotTest.setName("嵌入式");
                        break;
                    case 2://激光雷达
                        robotTest.setName("激光雷达");
                        break;
                    case 3://摄像头
                        robotTest.setName("摄像头");
                        break;
                }
                if (notifyHardState.getHardwareStatItemList().get(i).getStatValue()==1){
                    robotTest.setResult("正常");
                    robotTest.setRnum(0);
                }else {
                    robotTest.setResult("异常");
                    robotTest.setRnum(1);
                }
                Logger.e("时间"+notifyHardState.getHardwareStatItemList().get(i).getDate().toStringUtf8());
                robotTest.setTime(notifyHardState.getHardwareStatItemList().get(i).getDate().toStringUtf8().substring(0,16).replace("-"," "));
            robotTestList.add(robotTest);
            }


        robotTestAdapter.setNewData(robotTestList);
    }
    private void setHardState(){

    }

}
