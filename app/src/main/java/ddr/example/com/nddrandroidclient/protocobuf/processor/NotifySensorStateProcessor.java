package ddr.example.com.nddrandroidclient.protocobuf.processor;

import android.content.Context;

import com.google.protobuf.GeneratedMessageLite;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import DDRCommProto.BaseCmd;
import ddr.example.com.nddrandroidclient.entity.MessageEvent;
import ddr.example.com.nddrandroidclient.entity.other.Sensor;
import ddr.example.com.nddrandroidclient.entity.other.SensorSea;
import ddr.example.com.nddrandroidclient.entity.other.SensorSeas;
import ddr.example.com.nddrandroidclient.entity.other.Sensors;
import ddr.example.com.nddrandroidclient.other.Logger;

public class NotifySensorStateProcessor extends BaseProcessor{
    @Override
    public void process(Context context, BaseCmd.CommonHeader commonHeader, GeneratedMessageLite msg) {
        super.process(context, commonHeader, msg);
        BaseCmd.notifySensorState notifySensorState= (BaseCmd.notifySensorState) msg;
        SensorSeas sensorSeas = SensorSeas.getInstance();
        Sensors sensors=Sensors.getInstance();
        List<Sensor> sensorList = sensors.getSensorList();
        List<BaseCmd.notifySensorState.sensorStateItem> sensorStateItemList=notifySensorState.getDataList();
        //Logger.e("大小"+sensorStateItemList.size()+"----"+sensorList.size());
        List<SensorSea> sensorSeaList = new ArrayList<>();
        for (int i=0;i<sensorStateItemList.size();i++){
            SensorSea sensorSea=new SensorSea();
            sensorSea.setID(sensorStateItemList.get(i).getSensorID());
            sensorSea.setTriggerStat(sensorStateItemList.get(i).getTriggerStat());
            if (sensorStateItemList.get(i).getDist()>65534){
                String v = "-1";
                sensorSea.setValue(Float.parseFloat(v));
            }else {
                sensorSea.setValue(sensorStateItemList.get(i).getDist());
            }
            if (i< sensorList.size()){
                sensorSeaList.add(sensorSea);
            }else {
                Logger.d("超声无效数据");
            }
            sensorSeas.setSensorSeaList(sensorSeaList);
        }
       // Logger.e("数量"+sensorSeaList.size());
//        Logger.e("动态"+sensorSeaList.get(1).getValue());
        EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.updateSenesorSea));
    }
}
