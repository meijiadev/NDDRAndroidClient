package ddr.example.com.nddrandroidclient.entity.info;

import com.google.protobuf.ByteString;

import org.opencv.core.Mat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * 雷达栅格地图
 */
public class NotifyLidarCurSubMap {
    private static NotifyLidarCurSubMap notifyLidarCurSubMap;
    private NotifyLidarCurSubMap notifyLidarCurSubMap1;
    private byte[] subMap;
    private int width;
    private int height;
    private int compressionType;        //是否压缩 -0-不压缩
    private float posX;
    private float posY;
    private float posDirection;             //机器人方向
    private long times;                    //生成时间
    private float lidarRange;              //雷达量程 默认19.5m

    private GridItem gridItem;
    private List<NotifyLidarCurSubMap>notifyLidarCurSubMapList;
    private Map<GridItem, Mat> matMap;

    public static NotifyLidarCurSubMap getInstance(){
        if (notifyLidarCurSubMap==null){
            synchronized (NotifyLidarCurSubMap.class){
                if (notifyLidarCurSubMap==null){
                    notifyLidarCurSubMap=new NotifyLidarCurSubMap();
                }
            }
        }
        return notifyLidarCurSubMap;
    }

    public void addData(NotifyLidarCurSubMap notifyLidarCurSubMap){
        if (notifyLidarCurSubMapList==null){
            notifyLidarCurSubMapList=new ArrayList<>();
        }
        notifyLidarCurSubMapList.add(notifyLidarCurSubMap);
    }
   public List<NotifyLidarCurSubMap> getDataList(){
        if (notifyLidarCurSubMapList==null){
            notifyLidarCurSubMapList=new ArrayList<>();
        }
        return notifyLidarCurSubMapList;
   }

   public void addMap(GridItem gridItem,Mat mat){
        if (matMap==null){
            matMap=new HashMap<>();
        }
        matMap.put(gridItem,mat);
   }

   public Map getMap(){
        return (matMap==null?new HashMap():matMap);
   }

    public void setNotifyLidarCurSubMap1(NotifyLidarCurSubMap notifyLidarCurSubMap1) {
        this.notifyLidarCurSubMap1 = notifyLidarCurSubMap1;
    }

    public  NotifyLidarCurSubMap getNotifyLidarCurSubMap1() {
        return notifyLidarCurSubMap1;
    }


    public void setGridItem(GridItem gridItem) {
        this.gridItem = gridItem;
    }

    public GridItem getGridItem() {
        return gridItem;
    }

    public void setLidarRange(float lidarRange) {
        this.lidarRange = lidarRange;
    }

    public float getLidarRange() {
        return lidarRange;
    }

    public byte[] getSubMap() {
        return subMap;
    }

    public void setSubMap(byte[] subMap) {
        this.subMap = subMap;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getCompressionType() {
        return compressionType;
    }

    public void setCompressionType(int compressionType) {
        this.compressionType = compressionType;
    }

    public float getPosX() {
        return posX;
    }

    public void setPosX(float posX) {
        this.posX = posX;
    }

    public float getPosY() {
        return posY;
    }

    public void setPosY(float posY) {
        this.posY = posY;
    }

    public float getPosDirection() {
        return posDirection;
    }

    public void setPosDirection(float posDirection) {
        this.posDirection = posDirection;
    }

    public long getTimes() {
        return times;
    }

    public void setTimes(long times) {
        this.times = times;
    }
}
