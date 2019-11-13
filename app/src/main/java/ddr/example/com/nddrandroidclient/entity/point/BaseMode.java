package ddr.example.com.nddrandroidclient.entity.point;

/**
 * 数据模型基类
 */
public class BaseMode {
    private int type;   //type:1 路径  type:2 目标点
    private boolean inTask;       //是否在任务当中

    public BaseMode(int type) {
        this.type=type;
    }

    public BaseMode(){

    }


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isInTask() {
        return inTask;
    }

    public void setInTask(boolean inTask) {
        this.inTask = inTask;
    }
}
