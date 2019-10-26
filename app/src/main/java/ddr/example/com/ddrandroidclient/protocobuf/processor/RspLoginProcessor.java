package ddr.example.com.ddrandroidclient.protocobuf.processor;

import android.content.Context;

import com.google.protobuf.GeneratedMessageLite;

import org.greenrobot.eventbus.EventBus;

import DDRCommProto.BaseCmd;
import ddr.example.com.ddrandroidclient.entity.MessageEvent;
import ddr.example.com.ddrandroidclient.other.Logger;



public class RspLoginProcessor extends BaseProcessor {
    @Override
    public void process(Context context, BaseCmd.CommonHeader commonHeader, GeneratedMessageLite msg) {
        super.process(context, commonHeader, msg);
        BaseCmd.rspLogin rspLogin= (BaseCmd.rspLogin) msg;
        Logger.e("登陆成功");
        EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.LoginSuccess));

    }
}
