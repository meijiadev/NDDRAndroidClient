package ddr.example.com.nddrandroidclient.entity.other;

import java.util.List;

public class HwFloor {
    private String id;
    private String buildId;
    private String name;
    private List<HwFloor> hwFloors;

    public static HwFloor hwFloor;

    public static HwFloor getInstance(){
        if (hwFloor==null){
            synchronized (HwBuild.class){
                if (hwFloor==null){
                    hwFloor=new HwFloor();
                }
            }
        }
        return hwFloor;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBuildId() {
        return buildId;
    }

    public void setBuildId(String buildId) {
        this.buildId = buildId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<HwFloor> getHwFloors() {
        return hwFloors;
    }

    public void setHwFloors(List<HwFloor> hwFloors) {
        this.hwFloors = hwFloors;
    }
}
