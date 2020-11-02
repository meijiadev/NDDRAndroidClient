package ddr.example.com.nddrandroidclient.entity.other;

import java.util.List;

public class HwParkID {
    private String id;
    private String name;
    private List<HwParkID> hwBuilds;
    private String HwID;
    private String HwAppKey;
    private String sectorID;

    public static HwParkID HwParkID;

    public static HwParkID getInstance(){
        if (HwParkID==null){
            synchronized (HwParkID.class){
                if (HwParkID==null){
                    HwParkID=new HwParkID();
                }
            }
        }
        return HwParkID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ddr.example.com.nddrandroidclient.entity.other.HwParkID> getHwBuilds() {
        return hwBuilds;
    }

    public void setHwBuilds(List<ddr.example.com.nddrandroidclient.entity.other.HwParkID> hwBuilds) {
        this.hwBuilds = hwBuilds;
    }

    public String getHwID() {
        return HwID;
    }

    public void setHwID(String hwID) {
        HwID = hwID;
    }

    public String getHwAppKey() {
        return HwAppKey;
    }

    public void setHwAppKey(String hwAppKey) {
        HwAppKey = hwAppKey;
    }

    public String getSectorID() {
        return sectorID;
    }

    public void setSectorID(String sectorID) {
        this.sectorID = sectorID;
    }
}
