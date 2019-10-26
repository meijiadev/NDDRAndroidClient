package ddr.example.com.ddrandroidclient.protocobuf.processor;

import android.content.Context;

import com.google.protobuf.GeneratedMessageLite;

import org.greenrobot.eventbus.EventBus;

import DDRCommProto.BaseCmd;
import DDRVLNMapProto.DDRVLNMap;
import ddr.example.com.ddrandroidclient.entity.MessageEvent;
import ddr.example.com.ddrandroidclient.entity.MapFileStatus;
import ddr.example.com.ddrandroidclient.other.Logger;

/**
 * 获取某一地图下的信息
 */
public class RspGetDDRVLNMapProcessor extends BaseProcessor {
    @Override
    public void process(Context context, BaseCmd.CommonHeader commonHeader, GeneratedMessageLite msg) {
        super.process(context, commonHeader, msg);
        Logger.e("-----接收地图信息");
        DDRVLNMap.rspGetDDRVLNMap rspGetDDRVLNMap= (DDRVLNMap.rspGetDDRVLNMap) msg;
        MapFileStatus mapFileStatus=MapFileStatus.getInstance();
        mapFileStatus.setRspGetDDRVLNMap(rspGetDDRVLNMap);
        EventBus.getDefault().postSticky(new MessageEvent(MessageEvent.Type.updateDDRVLNMap));
    }
}
