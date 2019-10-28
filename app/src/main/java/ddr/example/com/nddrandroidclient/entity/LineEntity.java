package ddr.example.com.nddrandroidclient.entity;

import java.util.List;

public class LineEntity {
    private List<XyEntity> pointList;

    public LineEntity(List<XyEntity> list) {
        this.pointList=list;
    }

    public List<XyEntity> getPointList() {
        return pointList;
    }

    public void setPointList(List<XyEntity> pointList) {
        this.pointList = pointList;
    }
}
