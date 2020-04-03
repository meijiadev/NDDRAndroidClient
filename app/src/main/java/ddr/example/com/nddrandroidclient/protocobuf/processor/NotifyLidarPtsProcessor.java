package ddr.example.com.nddrandroidclient.protocobuf.processor;

import android.content.Context;

import com.google.protobuf.GeneratedMessageLite;

import org.greenrobot.eventbus.EventBus;

import DDRCommProto.BaseCmd;
import ddr.example.com.nddrandroidclient.entity.MessageEvent;
import ddr.example.com.nddrandroidclient.entity.info.NotifyLidarPtsEntity;
import ddr.example.com.nddrandroidclient.other.Logger;


/**
 *接收点云
 */
public class NotifyLidarPtsProcessor extends BaseProcessor {
    @Override
    public void process(Context context, BaseCmd.CommonHeader commonHeader, GeneratedMessageLite msg) {
        super.process(context, commonHeader, msg);
        BaseCmd.notifyLidarPts notifyLidarPts= (BaseCmd.notifyLidarPts) msg;
        NotifyLidarPtsEntity notifyLidarPtsEntity=NotifyLidarPtsEntity.getInstance();
        notifyLidarPtsEntity.setPosX(notifyLidarPts.getPosx());
        notifyLidarPtsEntity.setPosY(notifyLidarPts.getPosy());
        notifyLidarPtsEntity.setPosdirection(notifyLidarPts.getPosdirection());
        notifyLidarPtsEntity.setPositionList(notifyLidarPts.getPtsDataList());
        EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.receivePointCloud));
        //Logger.d("------发送点云数据:"+notifyLidarPtsEntity.getPosX()+";"+notifyLidarPtsEntity.getPosY());
    }
}
