package ddr.example.com.nddrandroidclient.entity.info;

import java.util.List;

import DDRCommProto.BaseCmd;

/**
 * 承载点云数据
 */
public class NotifyLidarPtsEntity {
    public static NotifyLidarPtsEntity notifyLidarPtsEntity;
    private float posX;
    private float posY;
    private float posdirection;
    private List<BaseCmd.notifyLidarPts.Position> positionList;

    public static NotifyLidarPtsEntity getInstance(){
        if (notifyLidarPtsEntity==null){
            synchronized (NotifyLidarPtsEntity.class){
                if (notifyLidarPtsEntity==null){
                    notifyLidarPtsEntity=new NotifyLidarPtsEntity();
                }
            }
        }
        return notifyLidarPtsEntity;
    }

    public NotifyLidarPtsEntity() {

    }

    public void setPosX(float posX) {
        this.posX = posX;
    }

    public float getPosX() {
        return posX;
    }

    public void setPosY(float posY) {
        this.posY = posY;
    }

    public float getPosY() {
        return posY;
    }

    public void setPosdirection(float posdirection) {
        this.posdirection = posdirection;
    }

    public float getPosdirection() {
        return posdirection;
    }

    public void setPositionList(List<BaseCmd.notifyLidarPts.Position> positionList) {
        this.positionList = positionList;
    }

    public List<BaseCmd.notifyLidarPts.Position> getPositionList() {
        return positionList;
    }

    public void setNull(){
        notifyLidarPtsEntity=null;
    }

}
