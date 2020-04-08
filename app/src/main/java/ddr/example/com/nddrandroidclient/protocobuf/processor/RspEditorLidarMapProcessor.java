package ddr.example.com.nddrandroidclient.protocobuf.processor;

import android.content.Context;

import com.google.protobuf.GeneratedMessageLite;

import org.greenrobot.eventbus.EventBus;

import DDRCommProto.BaseCmd;
import ddr.example.com.nddrandroidclient.entity.MessageEvent;

/**
 * time: 2020/4/3
 * desc: 原图去噪的返回结果
 */
public class RspEditorLidarMapProcessor extends BaseProcessor {
    @Override
    public void process(Context context, BaseCmd.CommonHeader commonHeader, GeneratedMessageLite msg) {
        super.process(context, commonHeader, msg);
        BaseCmd.rspEditorLidarMap rspEditorLidarMap= (BaseCmd.rspEditorLidarMap) msg;
        EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.notifyEditorMapResult,rspEditorLidarMap));
    }
}
