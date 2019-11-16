package ddr.example.com.nddrandroidclient.entity.point;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import DDRVLNMapProto.DDRVLNMap;

/**
 * time : 2019/11/9
 * desc : 任务的数据结构
 */
public class TaskMode implements Serializable {
    private String name;
    private List<BaseMode>baseModes=new ArrayList<>();          //这里面可能是路径也可能是目标点
    private List<String> targetPoints=new ArrayList<>();   // 分离出 目标点列表
    private List<String> pathLines=new ArrayList<>();         // 分离出 路径列表
    private int runCounts;                    //运行次数
    /*** 任务的执行时间*/
    private int startHour;            //开始的时间，时
    private int startMin;             //开始的时间，分
    private int endHour;             // ...
    private int endMin;              // ...

    private int taskState;                    //新增 任务状态  1-等待运行； 2-运行中； 3-暂停； 4-运行完了； 5-终止
    private int type;                        //新增 任务类型  0-不在执行队列中  1-临时任务  2-在队列中  3-临时任务+执行队列中


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
        pathLines.clear();
        targetPoints.clear();
        for (int i=0;i<baseModes.size();i++){
            if (baseModes.get(i).getType()==1){
                PathLine pathLine= (PathLine) baseModes.get(i);
                pathLines.add(pathLine.getName());
            }else if (baseModes.get(i).getType()==2){
                TargetPoint targetPoint= (TargetPoint) baseModes.get(i);
                targetPoints.add(targetPoint.getName());
            }
        }
    }



    public int getRunCounts() {
        return runCounts;
    }

    public void setRunCounts(int runCounts) {
        this.runCounts = runCounts;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setTaskState(int taskState) {
        this.taskState = taskState;
    }

    public int getTaskState() {
        return taskState;
    }

    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }

    public void setEndMin(int endMin) {
        this.endMin = endMin;
    }

    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    public void setStartMin(int startMin) {
        this.startMin = startMin;
    }

    public int getEndHour() {
        return endHour;
    }

    public int getEndMin() {
        return endMin;
    }

    public int getStartHour() {
        return startHour;
    }

    public int getStartMin() {
        return startMin;
    }

    /**
     * 获取任务中的路径列表（只包含路径名）
     * @return
     */
    public List<String> getPathLines() {
        return pathLines;
    }

    /**
     * 获取任务中的目标点列表 （只包含目标点名）
     * @return
     */
    public List<String> getTargetPoints() {
        return targetPoints;
    }
}
