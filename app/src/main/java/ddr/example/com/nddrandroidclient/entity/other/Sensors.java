package ddr.example.com.nddrandroidclient.entity.other;

import java.util.ArrayList;
import java.util.List;

public class Sensors {
    public static Sensors sensors;
    private List<Sensor> sensorList=new ArrayList<>();
    public static Sensors getInstance(){
        if(sensors==null){
            synchronized (Sensors.class){
                if (sensors==null){
                    sensors=new Sensors();
                }
            }
        }
        return sensors;
    }

    public List<Sensor> getSensorList() {
        return sensorList;
    }

    public void setSensorList(List<Sensor> sensorList) {
        this.sensorList = sensorList;
    }

}
