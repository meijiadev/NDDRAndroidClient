package ddr.example.com.nddrandroidclient.protocobuf.processor;

import android.content.Context;

import com.google.protobuf.GeneratedMessageLite;

import org.greenrobot.eventbus.EventBus;

import DDRCommProto.BaseCmd;
import ddr.example.com.nddrandroidclient.entity.MessageEvent;

/**
 * 异常通知
 */
public class NotifyExceptCodeProcessor extends BaseProcessor {
    @Override
    public void process(Context context, BaseCmd.CommonHeader commonHeader, GeneratedMessageLite msg) {
        super.process(context, commonHeader, msg);
        BaseCmd.notifyExceptCode notifyExceptCode= (BaseCmd.notifyExceptCode) msg;
        switch (notifyExceptCode.getCode()){
            case enExceptCode_NoLocated:
                EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.exceptCode_NoLocated));
                break;
            case enExceptCode_NoChargingPt:
                EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.exceptCode_NoChargingPoint));
                break;
            case enExceptCode_GeneralPathFailed:
                EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.exceptCode_GeneralPathFailed));
                break;
        }
    }
}
