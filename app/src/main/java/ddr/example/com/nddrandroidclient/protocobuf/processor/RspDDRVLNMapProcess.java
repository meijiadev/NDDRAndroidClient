package ddr.example.com.nddrandroidclient.protocobuf.processor;

import android.content.Context;

import com.google.protobuf.GeneratedMessageLite;

import DDRCommProto.BaseCmd;
import DDRVLNMapProto.DDRVLNMap;
import ddr.example.com.nddrandroidclient.other.Logger;


public class RspDDRVLNMapProcess extends BaseProcessor {
    @Override
    public void process(Context context, BaseCmd.CommonHeader commonHeader, GeneratedMessageLite msg) {
        super.process(context, commonHeader, msg);
        DDRVLNMap.rspDDRVLNMap rspDDRVLNMap= (DDRVLNMap.rspDDRVLNMap) msg;
        Logger.e("------保存结果"+rspDDRVLNMap.getTypeValue());
    }
}
