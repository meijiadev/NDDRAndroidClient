package ddr.example.com.nddrandroidclient.ui.fragment.secondFragment;

import android.view.View;
import android.widget.TextView;

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
import ddr.example.com.nddrandroidclient.protocobuf.CmdSchedule;
import ddr.example.com.nddrandroidclient.protocobuf.dispatcher.ClientMessageDispatcher;
import ddr.example.com.nddrandroidclient.socket.TcpClient;
import ddr.example.com.nddrandroidclient.ui.adapter.NLinearLayoutManager;
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
            case updateHardState:
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
        NLinearLayoutManager layoutManager =new NLinearLayoutManager(getAttachActivity());
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
        tcpClient.sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eModuleServer), reqHardwareCheck);
    }


    /**
     * 插入数据
     */
    private void setData(){
        robotTestList=new ArrayList<>();
        for (int i=0;i<notifyHardState.getHardwareStatItemList().size();i++){
            robotTest=new RobotTest();
                switch (notifyHardState.getHardwareStatItemList().get(i).getTypeValue()) {
                    case 1://嵌入式
                        robotTest.setName(getResources().getString(R.string.st_embedded_system));
                        break;
                    case 2://激光雷达
                        robotTest.setName(getResources().getString(R.string.st_lidar));
                        break;
                    case 3://摄像头
                        robotTest.setName(getResources().getString(R.string.st_dual_cameras));
                        break;
                }
                if (notifyHardState.getHardwareStatItemList().get(i).getStatValue()==1){
                    robotTest.setResult(getResources().getString(R.string.st_normal));
                    robotTest.setRnum(0);
                }else {
                    robotTest.setResult(getResources().getString(R.string.st_abnormal));
                    robotTest.setRnum(1);
                }
                Logger.e("时间"+notifyHardState.getHardwareStatItemList().get(i).getDate().toStringUtf8());
                robotTest.setTime(notifyHardState.getHardwareStatItemList().get(i).getDate().toStringUtf8().substring(0,16).replace("-"," "));
            robotTestList.add(robotTest);
            }


        robotTestAdapter.setNewData(robotTestList);
    }

}
