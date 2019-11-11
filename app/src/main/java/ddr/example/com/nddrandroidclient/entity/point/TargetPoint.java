package ddr.example.com.nddrandroidclient.entity.point;

/**
 * time : 2019/11/7
 * desc : 目标点结构
 */
public class TargetPoint extends BaseMode {
    private String name;
    private float x;
    private float y;
    private float theta;  //朝向 单位：度 【-180,180】
    private boolean isSelected;     //是否被选中

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

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
