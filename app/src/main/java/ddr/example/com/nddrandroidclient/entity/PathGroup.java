package ddr.example.com.nddrandroidclient.entity;

import java.util.List;

/**
 * 动作点+路径组合
 */
public class PathGroup {
    private String name;
    private List<GaugePoint> pathGroup;
    private int label;
    private int selected;     //单选 0 默认 1选中

    public void setLabel(int label) {
        this.label = label;
    }

    public int getLabel() {
        return label;
    }

    public List<GaugePoint> getPathGroup() {
        return pathGroup;
    }

    public void setPathGroup(List<GaugePoint> pathGroup) {
        this.pathGroup = pathGroup;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSelected(int selected) {
        this.selected = selected;
    }

    public int getSelected() {
        return selected;
    }
}
