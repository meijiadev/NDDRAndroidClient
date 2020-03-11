package ddr.example.com.nddrandroidclient.protocobuf.processor;

import android.content.Context;

import com.google.protobuf.GeneratedMessageLite;

import org.greenrobot.eventbus.EventBus;

import DDRCommProto.BaseCmd;
import DDRVLNMapProto.DDRVLNMap;
import ddr.example.com.nddrandroidclient.entity.MessageEvent;
import ddr.example.com.nddrandroidclient.other.Logger;

public class RspGetTaskOperational extends BaseProcessor{
    @Override
    public void process(Context context, BaseCmd.CommonHeader commonHeader, GeneratedMessageLite msg) {
        super.process(context, commonHeader, msg);
        DDRVLNMap.rspTaskOperational rspTaskOperational= (DDRVLNMap.rspTaskOperational) msg;
        Logger.e("返回结果"+rspTaskOperational.getType());
        switch (rspTaskOperational.getType()){
            case eSuccess:
//                Logger.e("任务模式"+rspTaskOperational.getClientdata().getOptSet().getTypeValue());
                if (rspTaskOperational.getClientdata().getOptSet().getTypeValue()==2){
                    EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.getSwitchTaskSuccess));
                }
                break;
            case eCmdFailed:
                EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.getSwitchTaskFaild));
                break;
        }
    }
}
