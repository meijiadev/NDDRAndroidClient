package ddr.example.com.nddrandroidclient.protocobuf.processor;

import android.content.Context;

import com.google.protobuf.GeneratedMessageLite;

import org.greenrobot.eventbus.EventBus;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;

import java.util.List;

import DDRCommProto.BaseCmd;
import ddr.example.com.nddrandroidclient.entity.MessageEvent;
import ddr.example.com.nddrandroidclient.entity.info.GridItem;
import ddr.example.com.nddrandroidclient.entity.info.NotifyLidarCurSubMap;
import ddr.example.com.nddrandroidclient.helper.OpenCVUtility;
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
        Logger.d("栅格地图机器人当前位置："+notifyLidarCurSubMap.getPosx()+";"+notifyLidarCurSubMap.getPosy());
        NotifyLidarCurSubMap notifyLidarCurSubMap1=NotifyLidarCurSubMap.getInstance();
        notifyLidarCurSubMap1.setCompressionType(notifyLidarCurSubMap.getCompressionType());
        notifyLidarCurSubMap1.setHeight(notifyLidarCurSubMap.getHeight());
        notifyLidarCurSubMap1.setWidth(notifyLidarCurSubMap.getWidth());
        notifyLidarCurSubMap1.setPosDirection(notifyLidarCurSubMap.getPosdirection());
        notifyLidarCurSubMap1.setPosX(notifyLidarCurSubMap.getPosx());
        notifyLidarCurSubMap1.setPosY(notifyLidarCurSubMap.getPosy());
        notifyLidarCurSubMap1.setTimes(notifyLidarCurSubMap.getTimestamp());
        notifyLidarCurSubMap1.setLidarRange(notifyLidarCurSubMap.getOaLidarRange());
        GridItem gridItem=new GridItem(notifyLidarCurSubMap.getGridIndex().getGridX(),notifyLidarCurSubMap.getGridIndex().getGridY());
        notifyLidarCurSubMap1.setGridItem(gridItem);
        try {
            long startTime=System.currentTimeMillis();
            byte[]data=ZlibUtil.unZip(notifyLidarCurSubMap.getSubmap().toByteArray());
            double w=notifyLidarCurSubMap.getWidth();
            double h=notifyLidarCurSubMap.getHeight();
            Mat mat= new Mat(new Size(w,h), CvType.CV_8UC3);
            mat.put(0,0,data);
            OpenCVUtility.getInstance().putValue(gridItem,mat);
            long endTime=System.currentTimeMillis();
            //Logger.e("耗费的时间："+(endTime-startTime));
        }catch (Exception e){
            e.printStackTrace();
        }
        EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.receiveLidarMap));
        //Logger.e("------总共："+OpenCVUtility.getInstance().getMatMap().size()+"块");
    }
}
