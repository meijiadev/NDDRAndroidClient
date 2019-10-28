package ddr.example.com.nddrandroidclient.entity;

public class ActionPoint extends GaugePoint {
    private String name;
    private String gaugeName;
    private String attr="旋转";
    private int actionType=7;
    private String toWards="1";       //点朝向角度
    private String fileTxt;
    private int itemType;
    private int label;
    private String qrParamete;
    private int parameterVaule;
    private int multiSelect=0;  //0：默认状态 1：被选择  一个列表中可以有多个子项被选择

    public void setGaugeName(String gaugeName) {
        this.gaugeName = gaugeName;
    }

    public String getGaugeName() {
        return gaugeName;
    }

    public ActionPoint(int type) {
        super(type);
    }
    public ActionPoint(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAttr() {
        return attr;
    }

    public void setAttr(String attr) {
        this.attr = attr;
    }

    public String getToWards() {
        return toWards;
    }

    public void setToWards(String toWards) {
        this.toWards = toWards;
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

    public void setFileTxt(String fileTxt) {
        this.fileTxt = fileTxt;
    }

    public String getFileTxt() {
        return fileTxt;
    }

    public void setActionType(int actionType) {
        this.actionType = actionType;
    }

    public int getActionType() {
        return actionType;
    }


    public void setLabel(int label) {
        this.label = label;
    }

    public int getLabel() {
        return label;
    }

    public String getQrParamete(){return qrParamete;}

    public void setQrParamete(String qrParamete){this.qrParamete=qrParamete;}

    public int getParameterVaule(){return parameterVaule;}

    public void setParameterVaule(int parameterVaule){this.parameterVaule=parameterVaule;}
}

