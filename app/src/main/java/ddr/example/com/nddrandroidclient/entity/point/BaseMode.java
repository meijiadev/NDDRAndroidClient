package ddr.example.com.nddrandroidclient.entity.point;

import java.io.Serializable;

/**
 * 数据模型基类
 */
public class BaseMode implements Serializable {
    private int type;   //type:1 路径  type:2 目标点
    private boolean inTask;       //是否在任务当中
    private boolean isSelected;   // 是否被选中 单选 用于点击效果
    private boolean isMultiple;   // 是否被选中 复选



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

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public void setMultiple(boolean multiple) {
        isMultiple = multiple;
    }

    public boolean isMultiple() {
        return isMultiple;
    }
}
