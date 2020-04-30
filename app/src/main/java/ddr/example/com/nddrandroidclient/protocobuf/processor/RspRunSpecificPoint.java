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
                    EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.getSpecificPoint));
                    Logger.e("添加成功");
                }else {
                    Logger.e("其它");
                }
                break;
            case en_RunSpecificPtAddTask:
                if (rspRunSpecificPoint.getClientdata().getOptType().getNumber()==1){
                    EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.getSpecificPoint1));
                    Logger.e("添加成功到队列");
                }else {
                    Logger.e("其它");
                }
                break;
            case en_RunSpecificUnknowError:
                Logger.e("未知错误");
                EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.getSpecificPoint2));
                break;
            case en_RunSpecificPtCurrNoLocated:
                Logger.e("当前没有定位");
                EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.getSpecificPoint3));
                break;
            case en_RunSpecificPtGenPathFailed:
                Logger.e("生成路径失败");
                EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.getSpecificPoint4));
                break;
            case en_RunSpecificPtInSelfCalib:
                Logger.e("当前处于自标定");
                EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.getSpecificPoint5));
                break;
            case en_RunSpecificPtResume_GoIdlePt:
                Logger.e("返回待机点");
                EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.getSpecificPoint8));
                break;
            case en_RunSpecificPtResume_RunTimeTask:
                Logger.e("运行时段任务");
                EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.getSpecificPoint9));
                break;
            case en_RunSpecificPtResume_StandingBy:
                Logger.e("原地待命");
                EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.getSpecificPoint10));
                break;
            case en_RunSpecificPtResume_GoNextPt:
                Logger.e("开始下一个点");
                EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.getSpecificPoint11));
                break;
        }
        EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.responseAbPoint));
    }
}
