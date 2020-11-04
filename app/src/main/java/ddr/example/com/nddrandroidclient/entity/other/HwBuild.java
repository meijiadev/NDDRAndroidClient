package ddr.example.com.nddrandroidclient.entity.other;

import java.util.List;

public class HwBuild {
    private String id;
    private String name;
    private List<HwBuild> hwBuilds;
    private String parkId;

    public static HwBuild hwBuild;

    public static HwBuild getInstance(){
        if (hwBuild==null){
            synchronized (HwBuild.class){
                if (hwBuild==null){
                    hwBuild=new HwBuild();
                }
            }
        }
        return hwBuild;
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

    public List<HwBuild> getHwBuilds() {
        return hwBuilds;
    }

    public void setHwBuilds(List<HwBuild> hwBuilds) {
        this.hwBuilds = hwBuilds;
    }

    public String getParkId() {
        return parkId;
    }

    public void setParkId(String parkId) {
        this.parkId = parkId;
    }
}
