package ddr.example.com.nddrandroidclient.protocobuf.processor;

import android.content.Context;

import com.google.protobuf.GeneratedMessageLite;

import DDRCommProto.BaseCmd;
import ddr.example.com.nddrandroidclient.entity.info.NotifyLidarCurSubMap;
import ddr.example.com.nddrandroidclient.helper.ZlibUtil;
import ddr.example.com.nddrandroidclient.other.Logger;

/**
 * desc：最近活跃的雷达子概率栅格地图
 * time：2020/06/19
 */
public class NotifyLidarCurSubMapProcessor extends BaseProcessor {
    @Override
    public void process(Context context, BaseCmd.CommonHeader commonHeader, GeneratedMessageLite msg) {
        super.process(context, commonHeader, msg);
        BaseCmd.notifyLidarCurSubMap notifyLidarCurSubMap= (BaseCmd.notifyLidarCurSubMap) msg;
        NotifyLidarCurSubMap notifyLidarCurSubMap1=NotifyLidarCurSubMap.getInstance();
        NotifyLidarCurSubMap notifyLidarCurSubMap2=new NotifyLidarCurSubMap();
        notifyLidarCurSubMap2.setCompressionType(notifyLidarCurSubMap.getCompressionType());
        notifyLidarCurSubMap2.setGridItem(notifyLidarCurSubMap.getGridIndex());
        notifyLidarCurSubMap2.setHeight(notifyLidarCurSubMap.getHeight());
        notifyLidarCurSubMap2.setWidth(notifyLidarCurSubMap.getWidth());
        notifyLidarCurSubMap2.setPosDirection(notifyLidarCurSubMap.getPosdirection());
        notifyLidarCurSubMap2.setPosX(notifyLidarCurSubMap.getPosx());
        notifyLidarCurSubMap2.setPosY(notifyLidarCurSubMap.getPosy());
        notifyLidarCurSubMap2.setTimes(notifyLidarCurSubMap.getTimestamp());
        String index=notifyLidarCurSubMap2.getGridItem().getGridX()+","+notifyLidarCurSubMap2.getGridItem().getGridX();
        //Logger.e("-----地图索引："+index+"----"+notifyLidarCurSubMap1.getDataMap().size());
        notifyLidarCurSubMap2.setSubMap(ZlibUtil.unZip(notifyLidarCurSubMap.getSubmap().toByteArray()));
        notifyLidarCurSubMap1.addData(index,notifyLidarCurSubMap2);
        Logger.e("-----接收栅格地图"+notifyLidarCurSubMap2.getCompressionType()+"数组长度："+notifyLidarCurSubMap2.getSubMap().length);
    }
}
