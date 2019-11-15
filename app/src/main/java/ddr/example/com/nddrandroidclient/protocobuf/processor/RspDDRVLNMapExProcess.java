package ddr.example.com.nddrandroidclient.protocobuf.processor;

import android.content.Context;

import com.google.protobuf.GeneratedMessageLite;

import org.greenrobot.eventbus.EventBus;

import DDRCommProto.BaseCmd;
import DDRVLNMapProto.DDRVLNMap;
import ddr.example.com.nddrandroidclient.entity.MessageEvent;
import ddr.example.com.nddrandroidclient.other.Logger;

/**
 * 保存修改地图信息之后的返回值
 */
public class RspDDRVLNMapExProcess extends BaseProcessor {
    @Override
    public void process(Context context, BaseCmd.CommonHeader commonHeader, GeneratedMessageLite msg) {
        super.process(context, commonHeader, msg);
        DDRVLNMap.rspDDRVLNMapEx rspDDRVLNMapEx= (DDRVLNMap.rspDDRVLNMapEx) msg;
        Logger.e("------保存结果"+rspDDRVLNMapEx.getTypeValue());
        EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.updateRevamp));
    }
}
