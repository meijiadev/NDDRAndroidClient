package ddr.example.com.nddrandroidclient.entity.info;

import java.util.ArrayList;
import java.util.List;

import DDRVLNMapProto.DDRVLNMap;

/**
 * 用于保存服务端的列表
 *
 */
public class MapFileStatus {
    public static MapFileStatus mapFileStatus;
    private List<String> mapNames=new ArrayList<>();                    // 服务端返回的地图名字列表
    private List<String> pictureUrls;                 //激光地图的http连接列表
    private DDRVLNMap.rspGetDDRVLNMapEx rspGetDDRVLNMapEx;     //获取指定某一地图的相关信息
    private List<MapInfo> mapInfos=new ArrayList<>();
    /**
     * 单例模式，用于保存地图相关信息
     * @return
     */
    public static MapFileStatus getInstance(){
        if (mapFileStatus==null){
            synchronized (MapFileStatus.class){
                if (mapFileStatus==null){
                    mapFileStatus=new MapFileStatus();
                }
            }
        }
        return mapFileStatus;
    }

    public List<String> getMapNames() {
       // Logger.e("------:"+mapNames.size());
        return mapNames;
    }

    public void setMapNames(List<String> mapNames) {
        this.mapNames = mapNames;
    }

    public List<String> getPictureUrls() {
        return pictureUrls;
    }

    public void setPictureUrls(List<String> pictureUrls) {
        this.pictureUrls = pictureUrls;
       // Logger.e("连接数量："+pictureUrls.size());
    }

    public void setRspGetDDRVLNMap(DDRVLNMap.rspGetDDRVLNMapEx rspGetDDRVLNMapEx) {
        this.rspGetDDRVLNMapEx = rspGetDDRVLNMapEx;
    }

    public DDRVLNMap.rspGetDDRVLNMapEx getRspGetDDRVLNMapEx() {
        return rspGetDDRVLNMapEx;
    }

    public void setMapInfos(List<MapInfo> mapInfos) {
        this.mapInfos = mapInfos;
    }

    public List<MapInfo> getMapInfos() {
        return mapInfos;
    }
}
