package ddr.example.com.nddrandroidclient.entity.model;

import org.litepal.crud.LitePalSupport;

/**
 * desc: 机器人位置坐标数据库存储类
 */
public class RobotCoordinates extends LitePalSupport {
    private float x;
    private float y;

    public RobotCoordinates(float x,float y){
        this.x=x;
        this.y=y;
    }

    public RobotCoordinates() {
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
}
