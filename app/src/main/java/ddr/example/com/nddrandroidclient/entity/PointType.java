package ddr.example.com.nddrandroidclient.entity;

/**
 * desc:点的类型
 * time:2020/6/16
 */
public enum PointType {
    eMarkingTypeError("",0),
    eMarkingTypeMustArrive("必到点",1),
    eMarkingTypeProjection("投影点",2),
    eMarkingTypeGate("闸机点",3),
    eMarkingTypeElevator("电梯点",4),
    eMarkingTypeCharging("充电点",5),
    eMarkingTypeQR("QR模式",6),
    eMarkingTypeRotate("旋转点",7),
    eMarkingTypeNormal("普通点",8),
    eMarkingTypeBorn("初始点",9);

    private String name;
    private int typeValue;

    private PointType(String name,int typeValue){
        this.name=name;
        this.typeValue=typeValue;
    }

    public String getName() {
        return name;
    }

    public int getTypeValue() {
        return typeValue;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
