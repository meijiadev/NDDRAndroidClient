package ddr.example.com.nddrandroidclient.entity.other;

import java.util.ArrayList;
import java.util.List;

import ddr.example.com.nddrandroidclient.entity.point.XyEntity;

/**
 * desc:保存矩形坐标的基类(坐标单位为世界坐标 m),因为图片需要旋转所以两个点坐标不再能确定改矩形
 * time:2020/4/2
 */
public class Rectangle {
    private List<XyEntity> rectanglePoints;  //保存四个点
    private XyEntity firstPoint;
    private XyEntity firstPoint_1;         // (right, top)
    private XyEntity secondPoint;
    private XyEntity secondPoint_1;       //  (left, bottom)

    public Rectangle() {
        super();
    }

    public Rectangle(XyEntity firstPoint,XyEntity secondPoint){
        this.firstPoint=firstPoint;
        this.secondPoint=secondPoint;
        firstPoint_1=new XyEntity(secondPoint.getX(),firstPoint.getY());
        secondPoint_1=new XyEntity(firstPoint.getX(),secondPoint.getY());
        rectanglePoints=new ArrayList<>();
        rectanglePoints.add(firstPoint);
        rectanglePoints.add(firstPoint_1);
        rectanglePoints.add(secondPoint);
        rectanglePoints.add(secondPoint_1);
    }

    public XyEntity getFirstPoint() {
        firstPoint=(firstPoint!=null)?firstPoint:new XyEntity();
        return firstPoint;
    }

    public XyEntity getFirstPoint_1() {
        return firstPoint_1;
    }

    public XyEntity getSecondPoint() {
        secondPoint=(secondPoint!=null)?secondPoint:new XyEntity();
        return secondPoint;
    }

    public XyEntity getSecondPoint_1() {
        return secondPoint_1;
    }

    public List<XyEntity> getRectanglePoints() {
        return rectanglePoints;
    }
}
