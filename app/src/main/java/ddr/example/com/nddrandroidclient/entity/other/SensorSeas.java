package ddr.example.com.nddrandroidclient.entity.other;

import java.util.ArrayList;
import java.util.List;

public class SensorSeas {

    public static  SensorSeas sensorSeas;
    public List<SensorSea> sensorSeaList = new ArrayList<>();
    private List<SensorSea> sensorListP=new ArrayList<>();
    private List<SensorSea> sensorListD= new ArrayList<>();
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

    public List<SensorSea> getSensorListP() {
        return sensorListP;
    }

    public void setSensorListP(List<SensorSea> sensorListP) {
        this.sensorListP = sensorListP;
    }

    public List<SensorSea> getSensorListD() {
        return sensorListD;
    }

    public void setSensorListD(List<SensorSea> sensorListD) {
        this.sensorListD = sensorListD;
    }
}
