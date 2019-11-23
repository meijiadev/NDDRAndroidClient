package ddr.example.com.nddrandroidclient.entity;

import java.util.List;

import ddr.example.com.nddrandroidclient.entity.info.MapInfo;
import ddr.example.com.nddrandroidclient.entity.point.BaseMode;

/**
 * desc: EvenBus的信息传递类
 */
public class MessageEvent {
    private Type type;
    private List<MapInfo> mapInfoList;
    private List<Object> datas;
    private Object data;
    private String bitmapPath;
    public enum Type{
        updateIPList,          //更新IP列表
        updatePort,           // 更新端口号
        LoginSuccess,        //登陆成功
        updateMapList,      //获取地图列表
        updateBaseStatus,  //获取基础信息
        updateDDRVLNMap,  // 更新地图信息
        realTimeDraw,
        addPoiPoint,
        updateVersion, //获取版本信息
        receivePointCloud,
        switchTaskSuccess,

        addNewPoint,    //添加目标点
        addNewPath,     //添加路径
        editMap,        //编辑地图
        updatePoints,   //添加完，更新目标点列表
        updatePaths,    //添加完，更新路径列表
        updateRevamp,   //更新修改之后的地图信息，提醒UI层重新拉地图信息

        touchFloatWindow, //

        mapOperationalSucceed,  //地图操作成功
        switchMapSucceed,       //切换地图成功


    }

    public MessageEvent(Type type) {
      this.type=type;
    }

  /*  public MessageEvent(Type type, List<MapInfo>mapInfos){
        this.type=type;
        this.mapInfoList=mapInfos;
    }*/

    public MessageEvent(Type type, Object object){
        this.type=type;
        this.data=object;
    }

    public MessageEvent(Type type, List<Object> datas){
        this.type=type;
        this.datas=datas;
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
