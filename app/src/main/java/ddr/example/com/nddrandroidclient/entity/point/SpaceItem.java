package ddr.example.com.nddrandroidclient.entity.point;

import java.util.ArrayList;
import java.util.List;

import DDRVLNMapProto.DDRVLNMap;

/**
 * 控件区域
 */
public class SpaceItem {
    private String name;
    private int type;                 // 0: default ; 1:虚拟墙 2：高亮区  3：迷雾区
    private List<DDRVLNMap.space_pointEx> lines=new ArrayList<>();       //线段
    private List<DDRVLNMap.space_pointEx> polygons=new ArrayList<>();    //多边形
    private Circle circle;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<DDRVLNMap.space_pointEx> getLines() {
        return lines;
    }

    public void setLines(List<DDRVLNMap.space_pointEx> lines) {
        this.lines = lines;
    }

    public List<DDRVLNMap.space_pointEx> getPolygons() {
        return polygons;
    }

    public void setPolygons(List<DDRVLNMap.space_pointEx> polygons) {
        this.polygons = polygons;
    }

    public Circle getCircle() {
        return circle;
    }

    public void setCircle(Circle circle) {
        this.circle = circle;
    }


    /**
     * 圆心
     */
    public static class Circle{
        float x;
        float y;
        float radius;

        public Circle(float x,float y,float radius) {
            this.x=x;
            this.y=y;
            this.radius=radius;
        }

        public float getY() {
            return y;
        }

        public float getX() {
            return x;
        }

        public float getRadius() {
            return radius;
        }
    }
}
