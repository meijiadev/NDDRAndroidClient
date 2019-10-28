package ddr.example.com.nddrandroidclient.entity;

/**
 * 标记点（基础元素）
 */
public class GaugePoint {
    private String name;
    private float x;
    private float y;
    private int selected=0;  //0: 默认 1：被选择 一个列表中只能有一个被选择的子项
    private int multiSelect=0;  //0：默认状态 1：被选择  一个列表中可以有多个子项被选择
    private int label=4;         // 标签 二进制
    public int type;          // 1:path ; 2:action （元素类型）


    public GaugePoint(String name,float x,float y) {
        this.name=name;
        this.x=x;
        this.y=y;
    }
    public GaugePoint(int type){
        this.type=type;
    }

    public GaugePoint(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }


    public void setSelected(int selected) {
        this.selected = selected;
    }

    public int getSelected() {
        return selected;
    }

    public void setMultiSelect(int multiSelect) {
        this.multiSelect = multiSelect;
    }

    public int getMultiSelect() {
        return multiSelect;
    }

    public int getType() {
        return type;
    }

    public void setLabel(int label) {
        this.label = label;
    }

    public int getLabel() {
        return label;
    }
}
