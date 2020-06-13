package ddr.example.com.nddrandroidclient.protocobuf.processor;

import android.content.Context;

import com.google.protobuf.GeneratedMessageLite;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Date;

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
        Logger.d("接收点云："+longToDate(notifyLidarPts.getTimestamp()));
        NotifyLidarPtsEntity notifyLidarPtsEntity=NotifyLidarPtsEntity.getInstance();
        notifyLidarPtsEntity.setPosX(notifyLidarPts.getPosx());
        notifyLidarPtsEntity.setPosY(notifyLidarPts.getPosy());
        notifyLidarPtsEntity.setPosdirection(notifyLidarPts.getPosdirection());
        notifyLidarPtsEntity.setPositionList(notifyLidarPts.getPtsDataList());
        EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.receivePointCloud));
    }

    /**
     * 将long型时间戳转成年月日的格式
     * @param time
     * @return
     */
    private String longToDate(long time){
        Date date=new Date(time);
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        return simpleDateFormat.format(date);
    }
}
