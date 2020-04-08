package ddr.example.com.nddrandroidclient.entity.other;

import ddr.example.com.nddrandroidclient.entity.point.XyEntity;

/**
 * desc:保存矩形坐标的基类(坐标单位为世界坐标 m)
 * time:2020/4/2
 */
public class Rectangle {
    private XyEntity firstPoint;
    private XyEntity secondPoint;

    public Rectangle() {
        super();
    }

    public Rectangle(XyEntity firstPoint,XyEntity secondPoint){
        this.firstPoint=firstPoint;
        this.secondPoint=secondPoint;
    }

    public XyEntity getFirstPoint() {
        firstPoint=(firstPoint!=null)?firstPoint:new XyEntity();
        return firstPoint;
    }

    public XyEntity getSecondPoint() {
        secondPoint=(secondPoint!=null)?secondPoint:new XyEntity();
        return secondPoint;
    }
}
