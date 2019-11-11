package ddr.example.com.nddrandroidclient.entity.point;

import java.util.List;

import DDRVLNMapProto.DDRVLNMap;

/**
 * time : 2019/11/9
 * desc : 任务的数据结构
 */
public class TaskMode  {
    private String name;
    private List<BaseMode>baseModes;          //这里面可能是路径也可能是目标点
    private DDRVLNMap.timeItem timeItem;       //运行时间段
    private int runCounts;                    //运行次数

    public TaskMode() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<BaseMode> getBaseModes() {
        return baseModes;
    }

    public void setBaseModes(List<BaseMode> baseModes) {
        this.baseModes = baseModes;
    }

    public DDRVLNMap.timeItem getTimeItem() {
        return timeItem;
    }

    public void setTimeItem(DDRVLNMap.timeItem timeItem) {
        this.timeItem = timeItem;
    }

    public int getRunCounts() {
        return runCounts;
    }

    public void setRunCounts(int runCounts) {
        this.runCounts = runCounts;
    }
}
