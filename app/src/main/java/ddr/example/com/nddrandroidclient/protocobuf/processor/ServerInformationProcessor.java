package ddr.example.com.nddrandroidclient.protocobuf.processor;

import android.content.Context;
import com.google.protobuf.GeneratedMessageLite;
import org.greenrobot.eventbus.EventBus;
import DDRCommProto.BaseCmd;
import ddr.example.com.nddrandroidclient.entity.MessageEvent;
import ddr.example.com.nddrandroidclient.other.Logger;
import ddr.example.com.nddrandroidclient.ui.fragment.StatusFragment;


/**
 * 当解析出IP和端口就关闭Udp连接并连接tcp
 */
public class ServerInformationProcessor extends BaseProcessor {
    private String robotId;
    @Override
    public void process(Context context, BaseCmd.CommonHeader commonHeader, GeneratedMessageLite msg) {
        Logger.w("udp广播处理程序");
        BaseCmd.bcLSAddr bcLSAddr=(BaseCmd.bcLSAddr) msg;
        for (BaseCmd.bcLSAddr.ServerInfo serverInfo:bcLSAddr.getLSInfosList()) {
            for (String ip:serverInfo.getIpsList()){
                EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.updateIPList,ip));
            }
            robotId = serverInfo.getRobotid();
            StatusFragment.setRobotID(robotId);
            //TaskPerformActivity.setRobotID(robotId,context);
           // DatabaseHelper.setRobotID(robotId,context);
            EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.updatePort,serverInfo.getPort()));

        }
    }
}
