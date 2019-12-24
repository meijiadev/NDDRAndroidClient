package ddr.example.com.nddrandroidclient.entity;

/**
 * desc: EvenBus的信息传递类
 */
public class MessageEvent {
    private Type type;
    private Object data;
    private String bitmapPath;
    public enum Type{
        updateIPList,          //更新IP列表
        updatePort,           // 更新端口号
        tcpConnected,         //tcp已连接
        LoginSuccess,        //登陆成功
        updateMapList,      //获取地图列表
        updateBaseStatus,  //获取基础信息
        updateDDRVLNMap,  // 更新地图信息
        realTimeDraw,     //实时绘制 机器人当前位置
        addPoiPoint,   //采集过程中添加点
        updateVersion, //获取版本信息
        receivePointCloud,    //接收点云
        switchTaskSuccess,   //

        addNewPoint,    //添加目标点
        addNewPath,     //添加路径
        editMap,        //编辑地图
        updatePoints,   //添加完，更新目标点列表
        updatePaths,    //添加完，更新路径列表
        updateRevamp,   //更新修改之后的地图信息，提醒UI层重新拉地图信息

        touchFloatWindow, // 点击浮窗
        mapOperationalSucceed,  //地图操作成功
        switchMapSucceed,       //切换地图成功


    }

    public MessageEvent(Type type) {
      this.type=type;
    }

    public MessageEvent(Type type, Object object){
        this.type=type;
        this.data=object;
    }

    public Type getType() {
        return type;
    }


    public Object getData(){
        return data;
    }
}
