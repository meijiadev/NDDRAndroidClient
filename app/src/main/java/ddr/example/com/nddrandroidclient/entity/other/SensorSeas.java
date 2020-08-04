package ddr.example.com.nddrandroidclient.entity.other;

import java.util.ArrayList;
import java.util.List;

public class SensorSeas {

    public static  SensorSeas sensorSeas;
    public List<SensorSea> sensorSeaList = new ArrayList<>();
    public static SensorSeas getInstance(){
        if (sensorSeas==null){
            synchronized (SensorSeas.class){
                if (sensorSeas==null){
                    sensorSeas=new SensorSeas();
                }
            }
        }
        return sensorSeas;
    }

    public List<SensorSea> getSensorSeaList() {
        return sensorSeaList;
    }

    public void setSensorSeaList(List<SensorSea> sensorSeaList) {
        this.sensorSeaList = sensorSeaList;
    }
}
