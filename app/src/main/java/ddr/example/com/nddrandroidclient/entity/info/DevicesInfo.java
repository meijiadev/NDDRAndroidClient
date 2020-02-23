package ddr.example.com.nddrandroidclient.entity.info;

import java.util.ArrayList;
import java.util.List;

/**
 * time: 2020/1/16
 * desc: 用于保存设备信息和连接状态
 */
public  class DevicesInfo {
    private List<Device>devices=new ArrayList<>();
    public static DevicesInfo devicesInfo;

    public static DevicesInfo getInstance(){
        if (devicesInfo==null){
            synchronized (DevicesInfo.class){
                if (devicesInfo==null){
                    devicesInfo=new DevicesInfo();
                }
            }
        }
        return devicesInfo;
    }

    private DevicesInfo(){

    }

    public void setDevices(List<Device> devices) {
        this.devices = devices;
    }

    public List<Device> getDevices() {
        return devices;
    }

    public static class Device{
        private String udid;
        private String name;
        private boolean isUsing;

        public Device(String udid,String name,boolean isUsing) {
            this.udid=udid;
            this.name=name;
            this.isUsing=isUsing;
        }

        public String getName() {
            return name;
        }

        public String getUdid() {
            return udid;
        }

        public boolean isUsing() {
            return isUsing;
        }
    }
}
