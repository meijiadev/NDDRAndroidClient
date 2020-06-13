package ddr.example.com.nddrandroidclient.protocobuf.processor;

import android.content.Context;

import com.google.protobuf.GeneratedMessageLite;

import org.greenrobot.eventbus.EventBus;

import DDRCommProto.BaseCmd;
import ddr.example.com.nddrandroidclient.entity.MessageEvent;

/**
 * time： 2020/02/20
 * desc: 接收回环检测的结果
 */
public class RspDetecLoopProcessor extends BaseProcessor {
    private int loopStatus;
    @Override
    public void process(Context context, BaseCmd.CommonHeader commonHeader, GeneratedMessageLite msg) {
        super.process(context, commonHeader, msg);
        BaseCmd.rspDetectLoop rspDetectLoop= (BaseCmd.rspDetectLoop) msg;
        int type=rspDetectLoop.getTypeValue();
        if (type==1){
            loopStatus=3;
        }else if (type==0){
            loopStatus=rspDetectLoop.getLoopStat();
        }
        EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.updateDetectionLoopStatus,loopStatus));
    }
}
