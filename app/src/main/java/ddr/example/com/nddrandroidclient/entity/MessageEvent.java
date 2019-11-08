package ddr.example.com.nddrandroidclient.entity;

import java.util.List;

import ddr.example.com.nddrandroidclient.entity.info.MapInfo;

public class MessageEvent {
    private int index;
    private boolean isWhat;
    private Type type;
    private List<MapInfo> mapInfoList;
    private Object data;
    public enum Type{
        updateIPList,          //更新IP列表
        updatePort,           // 更新端口号
        LoginSuccess,        //登陆成功
        updateMapList,      //获取地图列
        updateBaseStatus,  //获取基础信息
        updateMap,
        updateDDRVLNMap,
        updatePng,
        readFile,
        realTimeDraw,
        addPoiPoint,
        updateVersion, //获取版本信息
        receivePointCloud,

    }

    public MessageEvent(Type type) {
      this.type=type;
    }

    public MessageEvent(Type type, List<MapInfo>mapInfos){
        this.type=type;
        this.mapInfoList=mapInfos;
    }

    public MessageEvent(Type type, Object object){
        this.type=type;
        this.data=object;
    }

    public MessageEvent(int index, boolean isWhat){
        this.index=index;
        this.isWhat=isWhat;
    }

    public int getIndex()
    {
        return index;
    }

    public boolean getIsWhat() {
        return isWhat;
    }

    public Type getType() {
        return type;
    }

    public List<MapInfo> getMapInfoList() {
        return mapInfoList;
    }

    public Object getData(){
        return data;
    }
}
