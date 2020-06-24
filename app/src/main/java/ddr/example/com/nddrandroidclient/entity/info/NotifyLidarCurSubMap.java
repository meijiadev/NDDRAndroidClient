package ddr.example.com.nddrandroidclient.entity.info;

import com.google.protobuf.ByteString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import DDRCommProto.BaseCmd;

/**
 * 雷达栅格地图
 */
public class NotifyLidarCurSubMap {
    private static NotifyLidarCurSubMap notifyLidarCurSubMap;
    private byte[] subMap;
    private int width;
    private int height;
    private int compressionType;        //是否压缩 -0-不压缩
    private float posX;
    private float posY;
    private float posDirection;             //机器人方向
    private long times;                    //生成时间
    private BaseCmd.notifyLidarCurSubMap.gridItem gridItem;
    private Map<String,NotifyLidarCurSubMap> dataMap=new HashMap<>();

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

    public void addData(String gridItem,NotifyLidarCurSubMap notifyLidarCurSubMap){
        dataMap.put(gridItem,notifyLidarCurSubMap);
    }
    public Map<String, NotifyLidarCurSubMap> getDataMap() {
        return dataMap;
    }

    public NotifyLidarCurSubMap getValue(String key){
        return dataMap.get(key);
    }


    public void setGridItem(BaseCmd.notifyLidarCurSubMap.gridItem gridItem) {
        this.gridItem = gridItem;
    }

    public BaseCmd.notifyLidarCurSubMap.gridItem getGridItem() {
        return gridItem;
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
