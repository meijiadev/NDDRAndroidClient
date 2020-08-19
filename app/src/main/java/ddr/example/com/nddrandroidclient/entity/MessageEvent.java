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
        updateAiPort,          //更新AI端口号
        tcpConnected,         //tcp已连接
        tcpAiConnected,        //AITcp已连接
        receiveServerList,        //获取到地区服务信息
        LoginSuccess,        //登陆成功
        LoginAiSuccess,      //登陆AI成功
        wanLoginSuccess,     //广域网登陆成功
        connectedToRobot,    //连接上机器人
        updateMapList,      //获取地图列表
        updateBaseStatus,  //获取基础信息
        updateDDRVLNMap,  // 更新地图信息
        realTimeDraw,     //实时绘制 机器人当前位置
        addPoiPoint,   //采集过程中添加点
        updateVersion, //获取版本信息
        receiveLiarMap,       //接收雷达栅格图
        receivePointCloud,    //接收点云
        receiveVisionPoints,   //接收视觉点云
        switchTaskSuccess,   //
        updateParameter,//获取参数
        updateHardState,//获取自检参数
        updateSensor,//获取传感器参数
        updateSenesorSea,//查看超声状态

        addNewPoint,    //添加目标点
        addNewPath,     //添加路径
        editMap,        //编辑地图
        setChargePoint,  //设置充电点
        updatePoints,   //添加完，更新目标点列表
        updatePaths,    //添加完，更新路径列表
        updateVirtualWall,  //添加完虚拟墙并通知保存
        updateDenSuccess,    //原图去噪结束
        updateRevamp,   //更新修改之后的地图信息，提醒UI层重新拉地图信息

        touchFloatWindow, // 点击浮窗
        mapOperationalSucceed,  //地图操作成功
        switchMapSucceed,       //切换地图成功

        receiveObstacleInfo,  //接收机器人当前位置的障碍物信息
        enterRelocationMode,  //重新进入重定位模式
        updateRelocationStatus,  //接受重定位结果

        notifyMapGenerateProgress,  //接受地图生成的进度
        updateDetectionLoopStatus,   //接受回环检测结果

        touchSelectPoint,            //触摸选中目标点

        getSwitchTaskSuccess, //获取临时任务结果成功
        getSwitchTaskFailed,//获取临时任务结果失败
        GoToChargingPoint,    //前往充点电
        responseAbPoint,     // 点击ab点回复消息
        getSpecificPoint,   //获取点击AB点的结果
        getSpecificPoint1,
        getSpecificPoint2,
        getSpecificPoint3,
        getSpecificPoint4,
        getSpecificPoint5,
        getSpecificPoint8,
        getSpecificPoint9,
        getSpecificPoint10,
        getSpecificPoint11,

        notifyEditorMapResult,           //返回去噪结果
        notifyTCPDisconnected,
        updateVoice,//获取语音播报结果
        getRobotCoordinates,  //从本地数据库更新数据

        exceptCode_NoLocated,     //当前无定位
        exceptCode_NoChargingPoint,     //无充点电
        exceptCode_GeneralPathFailed,    //生成路径失败

        updateProgress,
        apkDownloadSucceed,
        apkDownloadFailed,
        touchMapWindow,//定位界面触摸屏幕

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
