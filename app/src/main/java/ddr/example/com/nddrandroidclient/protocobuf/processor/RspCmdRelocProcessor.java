package ddr.example.com.nddrandroidclient.protocobuf.processor;

import android.content.Context;

import com.google.protobuf.GeneratedMessageLite;

import org.greenrobot.eventbus.EventBus;

import DDRCommProto.BaseCmd;
import ddr.example.com.nddrandroidclient.entity.MessageEvent;

/**
 * 是否进入重定位模式的返回信息
 */
public class RspCmdRelocProcessor extends BaseProcessor {
    @Override
    public void process(Context context, BaseCmd.CommonHeader commonHeader, GeneratedMessageLite msg) {
        super.process(context, commonHeader, msg);
        BaseCmd.rspCmdReloc rspCmdReloc= (BaseCmd.rspCmdReloc) msg;
        if (rspCmdReloc.getTypeValue()==0){
            EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.enterRelocationMode));
        }
    }
}
