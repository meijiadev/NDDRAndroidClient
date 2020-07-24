package ddr.example.com.nddrandroidclient.entity.model;

import org.litepal.crud.LitePalSupport;

/**
 * desc:保存采集过程中标记的点到本地数据库
 * time:2020/07/24
 */
public class PoiPoint extends LitePalSupport {
    private float x;
    private float y;


    public PoiPoint(){

    }

    public PoiPoint(float x,float y){
        this.x=x;
        this.y=y;
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
