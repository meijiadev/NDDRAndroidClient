package ddr.example.com.nddrandroidclient.entity.other;

import java.util.ArrayList;
import java.util.List;

import DDRCommProto.BaseCmd;

public class NotifyHardState {
    public static NotifyHardState notifyHardState;
    private List<BaseCmd.notifyHardwareStat.HardwareStatItem> hardwareStatItemList=new ArrayList<>();

    public static NotifyHardState getInstance(){
        if (notifyHardState==null){
            synchronized (NotifyHardState.class){
                if (notifyHardState==null){
                    notifyHardState=new NotifyHardState();
                }
            }
        }
        return notifyHardState;
    }

    public List<BaseCmd.notifyHardwareStat.HardwareStatItem> getHardwareStatItemList() {
        return hardwareStatItemList;
    }

    public void setHardwareStatItemList(List<BaseCmd.notifyHardwareStat.HardwareStatItem> hardwareStatItemList) {
        this.hardwareStatItemList = hardwareStatItemList;
    }
}
