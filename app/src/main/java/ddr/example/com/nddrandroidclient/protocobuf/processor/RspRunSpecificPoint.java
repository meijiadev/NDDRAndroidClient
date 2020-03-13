package ddr.example.com.nddrandroidclient.protocobuf.processor;

import android.content.Context;

import com.google.protobuf.GeneratedMessageLite;

import org.greenrobot.eventbus.EventBus;

import DDRCommProto.BaseCmd;
import DDRVLNMapProto.DDRVLNMap;
import ddr.example.com.nddrandroidclient.entity.MessageEvent;
import ddr.example.com.nddrandroidclient.other.Logger;

public class RspRunSpecificPoint  extends BaseProcessor{
    @Override
    public void process(Context context, BaseCmd.CommonHeader commonHeader, GeneratedMessageLite msg) {
        super.process(context, commonHeader, msg);
        DDRVLNMap.rspRunSpecificPoint rspRunSpecificPoint= (DDRVLNMap.rspRunSpecificPoint) msg;
        switch (rspRunSpecificPoint.getErrorCode()){
            case en_RunSpecificPtOk:
                if (rspRunSpecificPoint.getClientdata().getOptType().getNumber()==1){
                    EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.getSpecifiPoint));
                    Logger.e("添加成功");
                }else {
                    EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.getSpecifiPoint6));
                    Logger.e("其它");
                }
                break;
            case en_RunSpecificPtAddTask:
                if (rspRunSpecificPoint.getClientdata().getOptType().getNumber()==1){
                    EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.getSpecifiPoint1));
                    Logger.e("添加成功到队列");
                }else {
                    EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.getSpecifiPoint7));
                    Logger.e("其它");
                }
                break;
            case en_RunSpecificUnknowError:
                Logger.e("未知错误");
                EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.getSpecifiPoint2));
                break;
            case en_RunSpecificPtCurrNoLocated:
                Logger.e("生成路径失败");
                EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.getSpecifiPoint3));
                break;
            case en_RunSpecificPtGenPathFailed:
                Logger.e("当前处于自标定");
                EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.getSpecifiPoint4));
                break;
            case en_RunSpecificPtInSelfCalib:
                Logger.e("当前没有定位");
                EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.getSpecifiPoint5));
                break;

        }
    }
}
