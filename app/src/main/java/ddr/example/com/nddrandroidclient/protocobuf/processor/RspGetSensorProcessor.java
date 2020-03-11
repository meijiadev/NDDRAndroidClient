package ddr.example.com.nddrandroidclient.protocobuf.processor;

import android.content.Context;

import com.google.protobuf.GeneratedMessageLite;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import DDRCommProto.BaseCmd;
import ddr.example.com.nddrandroidclient.entity.MessageEvent;
import ddr.example.com.nddrandroidclient.entity.other.Sensor;
import ddr.example.com.nddrandroidclient.entity.other.Sensors;
import ddr.example.com.nddrandroidclient.other.Logger;

public class RspGetSensorProcessor extends BaseProcessor{
    private Sensors sensors;
    private List<Sensor> sensorList;
    @Override
    public void process(Context context, BaseCmd.CommonHeader commonHeader, GeneratedMessageLite msg) {
        super.process(context, commonHeader, msg);
        BaseCmd.rspSensorConfigOperational rspSensorConfigOperational= (BaseCmd.rspSensorConfigOperational) msg;
        List<BaseCmd.sensorConfigItem> sensorConfigItems=rspSensorConfigOperational.getDataList();
        sensors=Sensors.getInstance();
        sensorList=new ArrayList<>();
        Logger.e("实际数量"+sensorConfigItems.size());
        for (int i=0;i<sensorConfigItems.size();i++){
            Sensor sensor=new Sensor();
            sensor.setKey(sensorConfigItems.get(i).getKey().toStringUtf8());
            sensor.setDydistance(sensorConfigItems.get(i).getDynamicOATriggerDist().toStringUtf8());
            sensor.setStaticdistance(sensorConfigItems.get(i).getStaticOATriggerDist().toStringUtf8());
            sensorList.add(sensor);
            sensors.setSensorList(sensorList);
        }
        Logger.e("数量"+sensorList.size());
        EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.updataSenesor));

    }
}
