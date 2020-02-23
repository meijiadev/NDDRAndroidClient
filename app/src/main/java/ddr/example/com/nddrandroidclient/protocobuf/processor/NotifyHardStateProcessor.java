package ddr.example.com.nddrandroidclient.protocobuf.processor;

import android.content.Context;

import com.google.protobuf.GeneratedMessageLite;

import org.greenrobot.eventbus.EventBus;

import DDRCommProto.BaseCmd;
import ddr.example.com.nddrandroidclient.entity.MessageEvent;
import ddr.example.com.nddrandroidclient.entity.other.NotifyHardState;

public class NotifyHardStateProcessor extends BaseProcessor{
    @Override
    public void process(Context context, BaseCmd.CommonHeader commonHeader, GeneratedMessageLite msg) {
        super.process(context, commonHeader, msg);
        BaseCmd.notifyHardwareStat notifyHardwareStat= (BaseCmd.notifyHardwareStat) msg;
        NotifyHardState notifyHardState=NotifyHardState.getInstance();
        notifyHardState.setHardwareStatItemList(notifyHardwareStat.getDataList());
        EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.updataHardState));
    }
}
