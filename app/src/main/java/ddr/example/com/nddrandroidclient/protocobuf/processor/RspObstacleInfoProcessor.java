package ddr.example.com.nddrandroidclient.protocobuf.processor;

import android.content.Context;

import com.google.protobuf.GeneratedMessageLite;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import DDRCommProto.BaseCmd;
import DDRModuleProto.DDRModuleCmd;
import ddr.example.com.nddrandroidclient.entity.MessageEvent;

public class RspObstacleInfoProcessor extends BaseProcessor {
    List<DDRModuleCmd.rspObstacleInfo.ObstacleInfo> obstacleInfos;
    @Override
    public void process(Context context, BaseCmd.CommonHeader commonHeader, GeneratedMessageLite msg) {
        super.process(context, commonHeader, msg);
        DDRModuleCmd.rspObstacleInfo rspObstacleInfo= (DDRModuleCmd.rspObstacleInfo) msg;
        obstacleInfos=rspObstacleInfo.getInfosList();
        EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.receiveObstacleInfo,obstacleInfos));

    }
}
