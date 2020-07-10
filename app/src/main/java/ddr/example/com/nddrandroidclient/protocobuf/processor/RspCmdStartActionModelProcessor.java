package ddr.example.com.nddrandroidclient.protocobuf.processor;

import android.content.Context;

import com.google.protobuf.GeneratedMessageLite;

import org.greenrobot.eventbus.EventBus;

import DDRCommProto.BaseCmd;
import ddr.example.com.nddrandroidclient.entity.MessageEvent;
import ddr.example.com.nddrandroidclient.other.Logger;


public class RspCmdStartActionModelProcessor extends BaseProcessor {
    @Override
    public void process(Context context, BaseCmd.CommonHeader commonHeader, GeneratedMessageLite msg) {
        super.process(context, commonHeader,msg);
        BaseCmd.rspCmdStartActionMode rspCmdStartActionMode= (BaseCmd.rspCmdStartActionMode) msg;
        Logger.e("--------》》》"+rspCmdStartActionMode.getMode()+"----->>"+rspCmdStartActionMode.getType());
       // EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.switchTaskSuccess));
        switch (rspCmdStartActionMode.getModeValue()){
            case 5:
                EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.GoToChargingPoint,rspCmdStartActionMode.getType()));
                break;
        }
    }
}
