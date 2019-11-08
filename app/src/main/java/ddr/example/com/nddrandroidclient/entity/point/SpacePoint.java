package ddr.example.com.nddrandroidclient.entity.point;

/**
 * 基础点
 */
public class SpacePoint {
    private float x;
    private float y;
    private float ang;

    public SpacePoint(float x,float y,float ang) {
        this.x=x;
        this.y=y;
        this.ang=ang;
    }

    public float getAng() {
        return ang;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
