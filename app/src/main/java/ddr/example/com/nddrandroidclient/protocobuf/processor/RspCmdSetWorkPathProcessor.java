package ddr.example.com.nddrandroidclient.protocobuf.processor;

import android.content.Context;

import com.google.protobuf.GeneratedMessageLite;

import org.greenrobot.eventbus.EventBus;

import DDRCommProto.BaseCmd;
import ddr.example.com.nddrandroidclient.entity.MessageEvent;
import ddr.example.com.nddrandroidclient.other.Logger;


public class RspCmdSetWorkPathProcessor extends BaseProcessor {

    @Override
    public void process(Context context, BaseCmd.CommonHeader commonHeader, GeneratedMessageLite msg) {
        super.process(context,commonHeader, msg);
        BaseCmd.rspCmdSetWorkPath rspCmdSetWorkPath= (BaseCmd.rspCmdSetWorkPath) msg;
        Logger.e("----"+rspCmdSetWorkPath.getType());
        switch (rspCmdSetWorkPath.getTypeValue()){
            case 0:
                EventBus.getDefault().postSticky(new MessageEvent(3,true));
                Logger.e("------路径修改成功");
                break;
            case 1:
                Logger.e("------路径修改失败");
                EventBus.getDefault().postSticky(new MessageEvent(3,false));
                break;
        }
    }

}
