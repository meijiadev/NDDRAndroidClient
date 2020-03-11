package ddr.example.com.nddrandroidclient.protocobuf.processor;

import android.content.Context;

import com.google.protobuf.GeneratedMessageLite;

import org.greenrobot.eventbus.EventBus;

import DDRCommProto.BaseCmd;
import ddr.example.com.nddrandroidclient.entity.MessageEvent;
import ddr.example.com.nddrandroidclient.entity.info.NotifyBaseStatusEx;


public class NotifyBaseStatusExProcessor extends BaseProcessor {
    @Override
    public void process(Context context, BaseCmd.CommonHeader commonHeader, GeneratedMessageLite msg) {
        super.process(context, commonHeader, msg);
        BaseCmd.notifyBaseStatusEx notifyBaseStatusEx= (BaseCmd.notifyBaseStatusEx) msg;
        NotifyBaseStatusEx notifyBaseStatusEx1=NotifyBaseStatusEx.getInstance();
        notifyBaseStatusEx1.setCurroute(notifyBaseStatusEx.getCurrroute().toStringUtf8());
        notifyBaseStatusEx1.setCurrpath(notifyBaseStatusEx.getCurrpath().toStringUtf8());
        notifyBaseStatusEx1.setMode(notifyBaseStatusEx.getModeValue());
        notifyBaseStatusEx1.setSonMode(notifyBaseStatusEx.getSonmodeValue());
        notifyBaseStatusEx1.seteDynamicOAStatus(notifyBaseStatusEx.getDynamicoaValue());
        notifyBaseStatusEx1.setStopStat(notifyBaseStatusEx.getStopstat());
        notifyBaseStatusEx1.setPosAngulauspeed(notifyBaseStatusEx.getPosangulauspeed());
        notifyBaseStatusEx1.setPosDirection(notifyBaseStatusEx.getPosdirection());
        notifyBaseStatusEx1.setPosLinespeed(notifyBaseStatusEx.getPoslinespeed());
        notifyBaseStatusEx1.setPosX(notifyBaseStatusEx.getPosx());
        notifyBaseStatusEx1.setPosY(notifyBaseStatusEx.getPosy());
        notifyBaseStatusEx1.seteSelfCalibStatus(notifyBaseStatusEx.getSelfcalibstatusValue());
        notifyBaseStatusEx1.setChargingStatus(notifyBaseStatusEx.getChargingStatus());
        notifyBaseStatusEx1.setTaskCount(notifyBaseStatusEx.getTaskCount());
        notifyBaseStatusEx1.setTaskDuration(notifyBaseStatusEx.getTaskDuration());
        notifyBaseStatusEx1.setExceptionValue(notifyBaseStatusEx.getRobotexceptionValue());
        notifyBaseStatusEx1.setLocationed(notifyBaseStatusEx.getBLocated());
        notifyBaseStatusEx1.setChargingType(notifyBaseStatusEx.getChargingTypeValue());
        EventBus.getDefault().postSticky(new MessageEvent(MessageEvent.Type.updateBaseStatus));
    }
}
