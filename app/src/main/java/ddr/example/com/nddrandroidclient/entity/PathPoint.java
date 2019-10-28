package ddr.example.com.nddrandroidclient.entity;


import com.google.protobuf.ByteString;

import java.util.List;

/**
 * 路径
 */
public class PathPoint extends GaugePoint {
    private String name;
    private List<ByteString> gaugeNames;
    private List<GaugePoint>gaugePoints;
    private String modeName="动态避障";
    private int model=64;       // 0, 64-动态避障 65-静态避障 66-贴边行驶路径(牛棚)      默认动态避障
    private String speed="0.4";
    private String text="";    //文本
    private int itemType=0;
    private int label=4;
    private int selected;        //单选被选中
    private int multiSelect=0;  //0：默认状态 1：被选择  一个列表中可以有多个子项被选择
    private int distance=20;
    private String direction="1";//左右 1 左 0 右


    public PathPoint(int type) {
        super(type);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public int getModel() {
        return model;
    }

    public void setModel(int model) {
        this.model = model;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public int getItemType() {
        return itemType;
    }

    @Override
    public void setMultiSelect(int multiSelect) {
        this.multiSelect = multiSelect;
    }

    @Override
    public int getMultiSelect() {
        return multiSelect;
    }

    public List<ByteString> getGaugeNames() {
        return gaugeNames;
    }

    public void setGaugeNames(List<ByteString> gaugeNames) {
        this.gaugeNames = gaugeNames;
    }


    public void setLabel(int label) {
        this.label = label;
    }

    public int getLabel() {
        return label;
    }

    public void setDistance(int distance){this.distance=distance;}

    public int getDistance(){return distance;}

    public void setModeName(String modeName){this.modeName=modeName;}

    public String getModeName(){return modeName;}

    public void setDirection(String direction){this.direction=direction;}

    public String getDirection(){return direction;}

    @Override
    public void setSelected(int selected) {
        this.selected = selected;
    }

    @Override
    public int getSelected() {
        return selected;
    }

    public void setGaugePoints(List<GaugePoint> gaugePoints) {
        this.gaugePoints = gaugePoints;
    }

    public List<GaugePoint> getGaugePoints() {
        return gaugePoints;
    }
}
