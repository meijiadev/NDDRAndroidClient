package ddr.example.com.nddrandroidclient.socket;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.google.protobuf.ByteString;
import com.google.protobuf.GeneratedMessageLite;
import com.google.protobuf.InvalidProtocolBufferException;
import com.xuhao.didi.core.iocore.interfaces.ISendable;
import com.xuhao.didi.core.pojo.OriginalData;
import com.xuhao.didi.core.protocol.IReaderProtocol;
import com.xuhao.didi.socket.client.sdk.OkSocket;
import com.xuhao.didi.socket.client.sdk.client.ConnectionInfo;
import com.xuhao.didi.socket.client.sdk.client.OkSocketOptions;
import com.xuhao.didi.socket.client.sdk.client.action.SocketActionAdapter;
import com.xuhao.didi.socket.client.sdk.client.connection.IConnectionManager;

import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;

import DDRCommProto.BaseCmd;
import DDRVLNMapProto.DDRVLNMap;
import androidx.fragment.app.FragmentActivity;
import ddr.example.com.nddrandroidclient.base.BaseDialog;
import ddr.example.com.nddrandroidclient.entity.info.MapFileStatus;
import ddr.example.com.nddrandroidclient.entity.point.BaseMode;
import ddr.example.com.nddrandroidclient.entity.point.PathLine;
import ddr.example.com.nddrandroidclient.entity.point.SpaceItem;
import ddr.example.com.nddrandroidclient.entity.point.TargetPoint;
import ddr.example.com.nddrandroidclient.entity.point.TaskMode;
import ddr.example.com.nddrandroidclient.helper.ActivityStackManager;
import ddr.example.com.nddrandroidclient.other.Logger;
import ddr.example.com.nddrandroidclient.protocobuf.MessageRoute;
import ddr.example.com.nddrandroidclient.protocobuf.dispatcher.BaseMessageDispatcher;
import ddr.example.com.nddrandroidclient.ui.activity.HomeActivity;
import ddr.example.com.nddrandroidclient.ui.dialog.WaitDialog;


/**
 * 基于OkSocket库的TCP客户端
 * create by ezreal.mei 2019/10/16
 */
public class TcpClient extends BaseSocketConnection {
    public Context context;
    public static TcpClient tcpClient;
    private ConnectionInfo info;
    public IConnectionManager manager;
    private boolean isConnected; //是否连接
    private SocketCallBack socketCallBack;
    private byte[] heads=new byte[4];  //存储头部长度信息的字节数组
    private byte [] bodyLenths=new byte[4];        //存储body体的信息长度



    /**
     * 获取客户端
     * @param context
     * @param baseMessageDispatcher
     * @return
     */
    public static TcpClient getInstance(Context context, BaseMessageDispatcher baseMessageDispatcher){
        if (tcpClient==null){
            synchronized (TcpClient.class){
                if (tcpClient==null){
                    tcpClient=new TcpClient(context,baseMessageDispatcher);
                }
            }
        }
        return tcpClient;
    }

    private TcpClient(Context context, BaseMessageDispatcher baseMessageDispatcher) {
        this.context=context;
        m_MessageRoute=new MessageRoute(context,this,baseMessageDispatcher);
    }

    /**
     * 创建连接通道
     * @param ip
     * @param port
     */
    public void creatConnect(String ip, int port){
        info=new ConnectionInfo(ip,port);
        manager=OkSocket.open(info);
        OkSocketOptions.Builder clientOptions=new OkSocketOptions.Builder();
        clientOptions.setReaderProtocol(new ReaderProtocol());
        manager.option(clientOptions.build());
        socketCallBack=new SocketCallBack();
        manager.registerReceiver(socketCallBack);
        manager.connect();
    }


    /**
     * 连接的状态信息
     */
    public class SocketCallBack extends SocketActionAdapter{
        private BaseDialog waitDialog;

        public SocketCallBack() {
            super();
        }

        /**
         * 当客户端连接成功会回调这个方法
         * @param info
         * @param action
         */
        @Override
        public void onSocketConnectionSuccess(ConnectionInfo info, String action) {
            setConnected(true);
            Activity activity=ActivityStackManager.getInstance().getTopActivity();
            Logger.e("--------:"+activity.getLocalClassName());
            if (activity.getLocalClassName().contains("LoginActivity")){
                showToast(activity,"连接成功",Toast.LENGTH_SHORT);
            }
            sendHeartBeat();
            if (waitDialog.isShowing()){
                waitDialog.dismiss();
            }
        }

        /**
         * 当客户端连接失败会调用
         * @param info
         * @param action
         * @param e
         */
        @Override
        public void onSocketConnectionFailed(ConnectionInfo info, String action, Exception e) {
            setConnected(false);
        }

        /**
         * 当连接断开时会调用此方法
         * @param info
         * @param action
         * @param e
         */
        @Override
        public void onSocketDisconnection(ConnectionInfo info, String action, Exception e) {
            setConnected(false);
            Activity activity=ActivityStackManager.getInstance().getTopActivity();
            if (activity!=null){
                if (activity.getLocalClassName().contains("LoginActivity")){
                    showToast(activity,"连接已断开，请重现连接",Toast.LENGTH_LONG);
                }else {
                    Logger.e("-------断开连接的页面："+activity.getLocalClassName());
                    showDialog(activity );
                }
            }
        }

        /**
         * 当接收tcp服务端数据时调用此方法
         * @param info
         * @param action
         * @param data
         */
        @Override
        public void onSocketReadResponse(ConnectionInfo info, String action, OriginalData data) {
            byte[] headBytes=data.getHeadBytes();
            System.arraycopy(headBytes,8,heads,0,4);
            int headLength=bytesToIntLittle(heads,0);
            try {
                m_MessageRoute.parseBody(data.getBodyBytes(),headLength);
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onSocketWriteResponse(ConnectionInfo info, String action, ISendable data) {

        }

        /**
         * 断连弹窗
         * @param activity
         */
        private void showDialog(Activity activity){
            activity.runOnUiThread(()->{
                waitDialog=new WaitDialog.Builder((FragmentActivity) activity).setMessage("网络正在连接...").setCancelable(true).show();
            });
            if (activity.getLocalClassName().contains("HomeActivity")){
                HomeActivity fragmentActivity= (HomeActivity) activity;
                fragmentActivity.postDelayed(()->{
                    if (waitDialog.isShowing()){
                        fragmentActivity.toast("网络无法连接，请退出重连！");
                    }
                },6000);
            }

        }

    }

    /**
     * 自定义解析头
     */
    public class ReaderProtocol implements IReaderProtocol{

        /**
         * 返回固定的头部长度
         * @return
         */
        @Override
        public int getHeaderLength() {
            return 12;
        }

        /**
         * 返回不固定长的body包长度
         * @param header
         * @param byteOrder
         * @return
         */
        @Override
        public int getBodyLength(byte[] header, ByteOrder byteOrder) {
            if (header == null || header.length < getHeaderLength()) {
                return 0;
            }
            System.arraycopy(header,4,bodyLenths,0,4);
            int bodyLength=bytesToIntLittle(bodyLenths,0)-8;
            return bodyLength;
        }
    }

    /**
     * 显示Toast提醒
     * @param activity
     * @param message
     * @param showTime
     */
    private void showToast(final Activity activity, final String message, final int showTime){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity,message,showTime).show();
            }
        });
    }



    /**
     * 以小端模式将byte[]转成int
     */
    public int bytesToIntLittle(byte[] src, int offset) {
        int value;
        value = (int) ((src[offset] & 0xFF)
                | ((src[offset + 1] & 0xFF) << 8)
                | ((src[offset + 2] & 0xFF) << 16)
                | ((src[offset + 3] & 0xFF) << 24));
        return value;
    }

    /**
     * 喂狗操作，否则当超过一定次数的心跳发送,未得到喂狗操作后,狗将会将此次连接断开重连.
     */
    public void feedDog(){
        if (manager!=null){
            manager.getPulseManager().feed();
          // Logger.e("---喂狗");
        }
    }

    /**
     * 断开连接
     */
    public void disConnect(){
        if (manager!=null){
            manager.unRegisterReceiver(socketCallBack);
            manager.disconnect();
            setConnected(false);
        }
    }

    public void onDestroy(){
        tcpClient=null;
        context=null;
    }

    /**
     * 发送消息
     * @param commonHeader
     * @param message
     */
    public void sendData(BaseCmd.CommonHeader commonHeader, GeneratedMessageLite message){
        if (manager!=null){
            byte[] data=m_MessageRoute.serialize(commonHeader,message);
            manager.send(new SendData(data));
        }
    }


    /**
     * 持续发送心跳
     */
    public void sendHeartBeat(){
        final BaseCmd.HeartBeat hb=BaseCmd.HeartBeat.newBuilder()
                .setWhatever("hb")
                .build();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isConnected&&manager!=null){
                    try {
                        manager.getPulseManager().setPulseSendable(new PulseData(m_MessageRoute.serialize(null,hb))).pulse();
                        Thread.sleep(2000);
                    }catch (NullPointerException e){
                        e.printStackTrace();
                    }catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }


    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    public boolean isConnected() {
        return isConnected;
    }


    /**
     * 获得某个地图下的信息
     */
    public void getMapInfo(ByteString routeName){
        DDRVLNMap.reqGetDDRVLNMapEx reqGetDDRVLNMapEx=DDRVLNMap.reqGetDDRVLNMapEx.newBuilder()
                .setOnerouteName(routeName)
                .build();
        BaseCmd.CommonHeader commonHeader = BaseCmd.CommonHeader.newBuilder()
                .setFromCltType(BaseCmd.eCltType.eLocalAndroidClient)
                .setToCltType(BaseCmd.eCltType.eLSMSlamNavigation)
                .addFlowDirection(BaseCmd.CommonHeader.eFlowDir.Forward)
                .build();
        tcpClient.sendData(commonHeader,reqGetDDRVLNMapEx);
        Logger.e("请求地图信息");
    }





    /**
     * 请求文件（txt、png) 刷新文件列表
     */
    public void requestFile() {
        //final ByteString currentFile = ByteString.copyFromUtf8("OneRoute_*" + "/bkPic.png");
        BaseCmd.reqClientGetMapInfo reqClientGetMapInfo=BaseCmd.reqClientGetMapInfo.newBuilder()
                .build();
        tcpClient.sendData(null,reqClientGetMapInfo);
        Logger.e("请求文件中....");
    }


    /**
     * 发送线速度，角速度
     * @param lineSpeed
     * @param palstance
     */
    public void sendSpeed(final float lineSpeed, final float palstance) {
        BaseCmd.reqCmdMove reqCmdMove = BaseCmd.reqCmdMove.newBuilder()
                .setLineSpeed(lineSpeed)
                .setAngulauSpeed(palstance)
                .build();
        BaseCmd.CommonHeader commonHeader = BaseCmd.CommonHeader.newBuilder()
                .setFromCltType(BaseCmd.eCltType.eLocalAndroidClient)
                .setToCltType(BaseCmd.eCltType.eLSMSlamNavigation)
                .addFlowDirection(BaseCmd.CommonHeader.eFlowDir.Forward)
                .build();
        tcpClient.sendData(commonHeader, reqCmdMove);

    }


    /**
     * 退出当前模式
     */
    public void exitModel() {
        BaseCmd.reqCmdEndActionMode reqCmdEndActionMode = BaseCmd.reqCmdEndActionMode.newBuilder()
                .setError("noError")
                .build();
        BaseCmd.CommonHeader commonHeader = BaseCmd.CommonHeader.newBuilder()
                .setFromCltType(BaseCmd.eCltType.eLocalAndroidClient)
                .setToCltType(BaseCmd.eCltType.eLSMSlamNavigation)
                .addFlowDirection(BaseCmd.CommonHeader.eFlowDir.Forward)
                .build();
        tcpClient.sendData(commonHeader, reqCmdEndActionMode);
    }

    /**
     * 只保存已修改的目标点到服务器
     */
    public void savePointData(DDRVLNMap.reqDDRVLNMapEx reqDDRVLNMapEx,List<TargetPoint> targetPoints){
        List<DDRVLNMap.targetPtItem> targetPtItems=new ArrayList<>();
        for (int i=0;i<targetPoints.size();i++){
            TargetPoint targetPoint=targetPoints.get(i);
            DDRVLNMap.space_pointEx space_pointEx=DDRVLNMap.space_pointEx.newBuilder()
                    .setX(targetPoint.getX())
                    .setY(targetPoint.getY())
                    .setTheta(targetPoint.getTheta())
                    .build();
            DDRVLNMap.targetPtItem targetPtItem=DDRVLNMap.targetPtItem.newBuilder()
                    .setPtName(ByteString.copyFromUtf8(targetPoint.getName()))
                    .setPtData(space_pointEx).build();
            targetPtItems.add(targetPtItem);
        }
    }

    /**
     * 保存已修改路径数据到服务
     * @param reqDDRVLNMapEx
     * @param pathLines
     */
    public void savePathData(DDRVLNMap.reqDDRVLNMapEx reqDDRVLNMapEx,List<PathLine> pathLines){
        List<DDRVLNMap.path_line_itemEx> pathLineItemExes=new ArrayList<>();
        for (int i=0;i<pathLines.size();i++){
            PathLine pathLine=pathLines.get(i);
            DDRVLNMap.path_line_config path_line_config=DDRVLNMap.path_line_config.newBuilder()
                    .setConfig(ByteString.copyFromUtf8(pathLine.getConfig())).build();
            List<DDRVLNMap.path_line_itemEx.path_lint_pt_Item> pathLintPtItems=new ArrayList<>();
            List<PathLine.PathPoint> pathPoints=pathLine.getPathPoints();
            for (int j=0;j<pathPoints.size();j++){
                DDRVLNMap.space_pointEx spacePointEx=DDRVLNMap.space_pointEx.newBuilder()
                        .setX(pathPoints.get(j).getX())
                        .setY(pathPoints.get(j).getY())
                        .build();
                DDRVLNMap.path_line_itemEx.path_lint_pt_Item path_lint_pt_item=DDRVLNMap.path_line_itemEx.path_lint_pt_Item.newBuilder()
                        .setPt(spacePointEx)
                        .setRotationangle(pathPoints.get(j).getRotationAngle())
                        .setTypeValue(pathPoints.get(j).getPointType())
                        .build();
                pathLintPtItems.add(path_lint_pt_item);
            }
            DDRVLNMap.path_line_itemEx path_line_itemEx=DDRVLNMap.path_line_itemEx.newBuilder()
                    .setName(ByteString.copyFromUtf8(pathLine.getName()))
                    .setModeValue(pathLine.getPathModel())
                    .setVelocity(pathLine.getVelocity())
                    .setConfig(path_line_config)
                    .setVelocity(pathLine.getVelocity())
                    .addAllPointSet(pathLintPtItems)
                    .build();
            pathLineItemExes.add(path_line_itemEx);
        }
    }

    /**
     * 保存已修改任务列表到服务
     * @param reqDDRVLNMapEx
     * @param taskModes
     */
    public void saveTaskData(DDRVLNMap.reqDDRVLNMapEx reqDDRVLNMapEx,List<TaskMode> taskModes) throws NullPointerException{
        List<DDRVLNMap.task_itemEx> taskItemExes=new ArrayList<>();
        for (int i=0;i<taskModes.size();i++){
            TaskMode taskMode=taskModes.get(i);
            List<BaseMode> baseModes=taskMode.getBaseModes();
            List<DDRVLNMap.path_elementEx> path_elementExes=new ArrayList<>();
            for (int j=0;j<baseModes.size();j++){
                DDRVLNMap.path_elementEx path_elementEx;
                String name="";
                int typeValue=0;
                if (baseModes.get(j).getType()==1){
                    PathLine pathLine= (PathLine) baseModes.get(j);
                    name=pathLine.getName();
                    typeValue=1;
                }else if (baseModes.get(j).getType()==2){
                    TargetPoint targetPoint= (TargetPoint) baseModes.get(j);
                    name=targetPoint.getName();
                    typeValue=2;
                }
                path_elementEx=DDRVLNMap.path_elementEx.newBuilder()
                        .setName(ByteString.copyFromUtf8(name))
                        .setTypeValue(typeValue)
                        .build();
                path_elementExes.add(path_elementEx);
            }
            DDRVLNMap.timeItem timeItem=DDRVLNMap.timeItem.newBuilder()
                    .setStartHour(taskMode.getStartHour())
                    .setStartMin(taskMode.getStartMin())
                    .setEndHour(taskMode.getEndHour())
                    .setEndMin(taskMode.getEndMin())
                    .build();
            DDRVLNMap.task_itemEx task_itemEx=DDRVLNMap.task_itemEx.newBuilder()
                    .setName(ByteString.copyFromUtf8(taskMode.getName()))
                    .setRunCount(taskMode.getRunCounts())
                    .setStateValue(taskMode.getTaskState())
                    .setTypeValue(taskMode.getType())
                    .setTimeSet(timeItem)
                    .addAllPathSet(path_elementExes)
                    .build();
            taskItemExes.add(task_itemEx);
        }
        DDRVLNMap.reqDDRVLNMapEx reqDDRVLNMapEx1=DDRVLNMap.reqDDRVLNMapEx.newBuilder()
                .setBasedata(reqDDRVLNMapEx.getBasedata())
                .setSpacedata(reqDDRVLNMapEx.getSpacedata())
                .setTargetPtdata(reqDDRVLNMapEx.getTargetPtdata())
                .addAllTaskSet(taskItemExes)
                .setPathSet(reqDDRVLNMapEx.getPathSet())
                .build();
        tcpClient.sendData(null,reqDDRVLNMapEx1);

    }


    /**
     * 所有数据保存到服务端
     */
    public void saveDataToServer(DDRVLNMap.reqDDRVLNMapEx reqDDRVLNMapEx,List<TargetPoint> targetPoints,List<PathLine> pathLines,List<TaskMode> taskModes){
        /*****************************保存到服务的目标点数据********************************************/
        List<DDRVLNMap.targetPtItem> targetPtItems=new ArrayList<>();
        for (int i=0;i<targetPoints.size();i++){
            TargetPoint targetPoint=targetPoints.get(i);
            DDRVLNMap.space_pointEx space_pointEx=DDRVLNMap.space_pointEx.newBuilder()
                    .setX(targetPoint.getX())
                    .setY(targetPoint.getY())
                    .setTheta(targetPoint.getTheta())
                    .build();
            DDRVLNMap.targetPtItem targetPtItem=DDRVLNMap.targetPtItem.newBuilder()
                    .setPtName(ByteString.copyFromUtf8(targetPoint.getName()))
                    .setPtData(space_pointEx).build();
            targetPtItems.add(targetPtItem);
        }
        DDRVLNMap.DDRMapTargetPointData targetPointData=DDRVLNMap.DDRMapTargetPointData.newBuilder()
                .addAllTargetPt(targetPtItems)
                .build();

        /****************************保存到服务的路径数据**********************************************/
        List<DDRVLNMap.path_line_itemEx> pathLineItemExes=new ArrayList<>();
        for (int i=0;i<pathLines.size();i++){
            PathLine pathLine=pathLines.get(i);
            DDRVLNMap.path_line_config path_line_config=DDRVLNMap.path_line_config.newBuilder()
                    .setConfig(ByteString.copyFromUtf8(pathLine.getConfig())).build();
            List<DDRVLNMap.path_line_itemEx.path_lint_pt_Item> pathLintPtItems=new ArrayList<>();
            List<PathLine.PathPoint> pathPoints=pathLine.getPathPoints();
            for (int j=0;j<pathPoints.size();j++){
                DDRVLNMap.space_pointEx spacePointEx=DDRVLNMap.space_pointEx.newBuilder()
                        .setX(pathPoints.get(j).getX())
                        .setY(pathPoints.get(j).getY())
                        .build();
                DDRVLNMap.path_line_itemEx.path_lint_pt_Item path_lint_pt_item=DDRVLNMap.path_line_itemEx.path_lint_pt_Item.newBuilder()
                        .setPt(spacePointEx)
                        .setRotationangle(pathPoints.get(j).getRotationAngle())
                        .setTypeValue(pathPoints.get(j).getPointType())
                        .build();
                pathLintPtItems.add(path_lint_pt_item);
            }
            DDRVLNMap.path_line_itemEx path_line_itemEx=DDRVLNMap.path_line_itemEx.newBuilder()
                    .setName(ByteString.copyFromUtf8(pathLine.getName()))
                    .setModeValue(pathLine.getPathModel())
                    .setVelocity(pathLine.getVelocity())
                    .setConfig(path_line_config)
                    .setVelocity(pathLine.getVelocity())
                    .addAllPointSet(pathLintPtItems)
                    .build();
            pathLineItemExes.add(path_line_itemEx);
        }
        DDRVLNMap.DDRMapPathDataEx ddrMapPathDataEx=DDRVLNMap.DDRMapPathDataEx.newBuilder()
                .addAllPathLineData(pathLineItemExes)
                .build();
        /**********************************************保存到服务的任务数据***************************************/
        List<DDRVLNMap.task_itemEx> taskItemExes=new ArrayList<>();
        for (int i=0;i<taskModes.size();i++){
            TaskMode taskMode=taskModes.get(i);
            List<BaseMode> baseModes=taskMode.getBaseModes();
            List<DDRVLNMap.path_elementEx> path_elementExes=new ArrayList<>();
            for (int j=0;j<baseModes.size();j++){
                DDRVLNMap.path_elementEx path_elementEx;
                String name="";
                int typeValue=0;
                if (baseModes.get(j).getType()==1){
                    PathLine pathLine= (PathLine) baseModes.get(j);
                    name=pathLine.getName();
                    typeValue=1;
                }else if (baseModes.get(j).getType()==2){
                    TargetPoint targetPoint= (TargetPoint) baseModes.get(j);
                    name=targetPoint.getName();
                    typeValue=2;
                }
                path_elementEx=DDRVLNMap.path_elementEx.newBuilder()
                        .setName(ByteString.copyFromUtf8(name))
                        .setTypeValue(typeValue)
                        .build();
                path_elementExes.add(path_elementEx);
            }
            DDRVLNMap.timeItem timeItem=DDRVLNMap.timeItem.newBuilder()
                    .setStartHour(taskMode.getStartHour())
                    .setStartMin(taskMode.getStartMin())
                    .setEndHour(taskMode.getEndHour())
                    .setEndMin(taskMode.getEndMin())
                    .build();
            DDRVLNMap.task_itemEx task_itemEx=DDRVLNMap.task_itemEx.newBuilder()
                    .setName(ByteString.copyFromUtf8(taskMode.getName()))
                    .setRunCount(taskMode.getRunCounts())
                    .setStateValue(taskMode.getTaskState())
                    .setTypeValue(taskMode.getType())
                    .setTimeSet(timeItem)
                    .addAllPathSet(path_elementExes)
                    .build();
            taskItemExes.add(task_itemEx);
        }
        Logger.e("-------------保存到服务端的任务:"+taskItemExes.size());
        /***************************************保存到服务的空间信息******************************************/
        List<DDRVLNMap.space_item> space_items=new ArrayList<>();           //接收到的空间数据
        List<SpaceItem> spaceItems=MapFileStatus.getInstance().getSpaceItems();
        Logger.e("-------空间信息:"+spaceItems.size());
        for (int i=0;i<spaceItems.size();i++){
            DDRVLNMap.line line=DDRVLNMap.line.newBuilder()
                    .addAllPointset(spaceItems.get(i).getLines())
                    .build();
            SpaceItem.Circle circle1=spaceItems.get(i).getCircle();
            DDRVLNMap.circle circle;
            if (circle1!=null){
                circle=DDRVLNMap.circle.newBuilder()
                        .setCenter(DDRVLNMap.space_pointEx.newBuilder().setX(circle1.getX()).setY(circle1.getY()).build())
                        .setRadius(circle1.getRadius())
                        .build();
            }else {
                circle=DDRVLNMap.circle.newBuilder().build();
            }
            DDRVLNMap.polygon polygon=DDRVLNMap.polygon.newBuilder()
                    .addAllPointset(spaceItems.get(i).getPolygons())
                    .build();
            DDRVLNMap.space_item space_item=DDRVLNMap.space_item.newBuilder()
                    .setName(ByteString.copyFromUtf8(spaceItems.get(i).getName()))
                    .setTypeValue(spaceItems.get(i).getType())
                    .setLinedata(line)
                    .setCircledata(circle)
                    .setPolygondata(polygon)
                    .build();
            space_items.add(space_item);
        }
        DDRVLNMap.DDRMapSpaceData spaceData=DDRVLNMap.DDRMapSpaceData.newBuilder()
                .addAllSpaceSet(space_items)
                .build();
        DDRVLNMap.reqDDRVLNMapEx reqDDRVLNMapEx1=DDRVLNMap.reqDDRVLNMapEx.newBuilder()
                .setBasedata(reqDDRVLNMapEx.getBasedata())
                .setSpacedata(spaceData)
                .setTargetPtdata(targetPointData)
                .addAllTaskSet(taskItemExes)
                .setPathSet(ddrMapPathDataEx)
                .build();
        tcpClient.sendData(null,reqDDRVLNMapEx1);
    }

    /**
     * 保存空间信息到服务
     * @param
     */
    public void saveSpaceToServer(){
        DDRVLNMap.reqDDRVLNMapEx reqDDRVLNMapEx=MapFileStatus.getInstance().getReqDDRVLNMapEx();
        List<DDRVLNMap.space_item> space_items=new ArrayList<>();           //接收到的空间数据
        List<SpaceItem> spaceItems=MapFileStatus.getInstance().getSpaceItems();
        for (int i=0;i<spaceItems.size();i++){
            DDRVLNMap.line line=DDRVLNMap.line.newBuilder()
                    .addAllPointset(spaceItems.get(i).getLines())
                    .build();
            DDRVLNMap.circle circle=DDRVLNMap.circle.newBuilder()
                    .setCenter(DDRVLNMap.space_pointEx.newBuilder().setX(spaceItems.get(i).getCircle().getX()).setY(spaceItems.get(i).getCircle().getY()).build())
                    .setRadius(spaceItems.get(i).getCircle().getRadius())
                    .build();
            DDRVLNMap.polygon polygon=DDRVLNMap.polygon.newBuilder()
                    .addAllPointset(spaceItems.get(i).getPolygons())
                    .build();
            DDRVLNMap.space_item space_item=DDRVLNMap.space_item.newBuilder()
                    .setName(ByteString.copyFromUtf8(spaceItems.get(i).getName()))
                    .setTypeValue(spaceItems.get(i).getType())
                    .setLinedata(line)
                    .setCircledata(circle)
                    .setPolygondata(polygon)
                    .build();
            space_items.add(space_item);
        }
        DDRVLNMap.DDRMapSpaceData spaceData=DDRVLNMap.DDRMapSpaceData.newBuilder()
                .addAllSpaceSet(space_items)
                .build();
        DDRVLNMap.reqDDRVLNMapEx reqDDRVLNMapEx1=DDRVLNMap.reqDDRVLNMapEx.newBuilder()
                .setBasedata(reqDDRVLNMapEx.getBasedata())
                .setSpacedata(spaceData)
                .setTargetPtdata(reqDDRVLNMapEx.getTargetPtdata())
                .addAllTaskSet(reqDDRVLNMapEx.getTaskSetList())
                .setPathSet(reqDDRVLNMapEx.getPathSet())
                .build();
        tcpClient.sendData(null,reqDDRVLNMapEx1);
    }


    /**
     * 对地图进行操作
     */
    public void reqMapOperational(List<DDRVLNMap.reqMapOperational.OptItem> optItems){
        DDRVLNMap.reqMapOperational reqMapOperational=DDRVLNMap.reqMapOperational.newBuilder()
                .addAllOptSet(optItems)
                .build();
        tcpClient.sendData(null,reqMapOperational);
    }

    /**
     * 切换地图
     * @param mapName
     */
    public void reqRunControlEx(String mapName){
        DDRVLNMap.reqRunControlEx reqRunControlEx=DDRVLNMap.reqRunControlEx.newBuilder()
                .setOnerouteName(ByteString.copyFromUtf8(mapName))
                .build();
        tcpClient.sendData(null,reqRunControlEx);
    }





}
