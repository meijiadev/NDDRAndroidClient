package ddr.example.com.nddrandroidclient.protocobuf.processor;

import android.content.Context;

import com.google.protobuf.GeneratedMessageLite;

import org.greenrobot.eventbus.EventBus;

import DDRCommProto.BaseCmd;
import ddr.example.com.nddrandroidclient.entity.MessageEvent;
import ddr.example.com.nddrandroidclient.other.Logger;

/**
 * time : 2020/2/20
 * desc : 接收地图生成的进度
 */
public class NotifyMapGenStatProcessor extends BaseProcessor {
    private float progress;
    @Override
    public void process(Context context, BaseCmd.CommonHeader commonHeader, GeneratedMessageLite msg) {
        super.process(context, commonHeader, msg);
        BaseCmd.notifyMapGenStat mapGenStat= (BaseCmd.notifyMapGenStat) msg;
        progress=mapGenStat.getProgCB();
        Logger.e("-----地图生成的当前进度；"+progress);
        EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.notifyMapGenerateProgress,progress));

    }
}
