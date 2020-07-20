package ddr.example.com.nddrandroidclient.protocobuf.processor;

import android.content.Context;

import com.google.protobuf.GeneratedMessageLite;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;

import java.util.ArrayList;
import java.util.List;

import DDRCommProto.BaseCmd;
import ddr.example.com.nddrandroidclient.entity.info.GridItem;
import ddr.example.com.nddrandroidclient.helper.OpenCVUtility;
import ddr.example.com.nddrandroidclient.helper.ZlibUtil;
import ddr.example.com.nddrandroidclient.other.Logger;

public class RspGetAllLidarCurSubMapProcessor extends BaseProcessor {
    List<BaseCmd.notifyLidarCurSubMap> notifyLidarCurSubMaps;
    @Override
    public void process(Context context, BaseCmd.CommonHeader commonHeader, GeneratedMessageLite msg) {
        super.process(context, commonHeader, msg);
        Logger.e("获取所有块地图");
        try {
            notifyLidarCurSubMaps=new ArrayList<>();
            long startTime=System.currentTimeMillis();
            BaseCmd.rspGetAllLidarCurSubMap rspGetAllLidarCurSubMap= (BaseCmd.rspGetAllLidarCurSubMap) msg;
            notifyLidarCurSubMaps=rspGetAllLidarCurSubMap.getDataList();
            for (int i=0;i<notifyLidarCurSubMaps.size();i++){
                GridItem gridItem=new GridItem(notifyLidarCurSubMaps.get(i).getGridIndex().getGridX(),notifyLidarCurSubMaps.get(i).getGridIndex().getGridY());
                byte[]data= ZlibUtil.unZip(notifyLidarCurSubMaps.get(i).getSubmap().toByteArray());
                try {
                    Mat mat= new Mat(new Size(notifyLidarCurSubMaps.get(i).getWidth(),notifyLidarCurSubMaps.get(i).getHeight()), CvType.CV_8UC3);
                    mat.put(0,0,data);
                    OpenCVUtility.getInstance().putValue(gridItem,mat);
                }catch (UnsatisfiedLinkError error){
                    error.printStackTrace();
                    break;
                }
            }
            long endTime=System.currentTimeMillis();
            Logger.e("耗费的时间："+(endTime-startTime)+";"+notifyLidarCurSubMaps.size());
        }catch (Exception e){
            e.printStackTrace();
        }
        Logger.e("------总共："+OpenCVUtility.getInstance().getMatMap().size()+"块");
    }
}
