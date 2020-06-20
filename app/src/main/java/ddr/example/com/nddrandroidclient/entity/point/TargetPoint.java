package ddr.example.com.nddrandroidclient.entity.point;

import java.io.Serializable;

import ddr.example.com.nddrandroidclient.entity.PointType;

/**
 * time : 2019/11/7
 * desc : 目标点结构
 */
public class TargetPoint extends BaseMode implements Serializable {
    private String name="";
    private float x;
    private float y;
    private float theta;  //朝向 单位：度 【-180,180】
    private PointType pointType;           //点的类型


    public PointType getPointType() {
        return pointType;
    }


    public void setPointType(PointType pointType) {
        this.pointType = pointType;
    }


    public TargetPoint(int type) {
        super(type);
    }

    public TargetPoint() {
        super();
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

    public float getTheta() {
        return theta;
    }

    public void setTheta(float theta) {
        this.theta = theta;
    }



}
