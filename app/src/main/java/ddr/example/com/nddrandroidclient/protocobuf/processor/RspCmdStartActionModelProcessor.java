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
        switch (rspCmdStartActionMode.getType()){
            case eSuccess:
                EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.switchTaskSuccess));
                break;
        }
    }
}
