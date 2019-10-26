package ddr.example.com.ddrandroidclient.socket;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

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

import DDRCommProto.BaseCmd;
import ddr.example.com.ddrandroidclient.helper.ActivityStackManager;
import ddr.example.com.ddrandroidclient.other.Logger;
import ddr.example.com.ddrandroidclient.protocobuf.MessageRoute;
import ddr.example.com.ddrandroidclient.protocobuf.dispatcher.BaseMessageDispatcher;


/**
 * 基于OkSocket库的TCP客户端
 * create by ezreal.mei 2019/10/16
 */
public class TcpClient extends BaseSocketConnection {
    public static TcpClient tcpClient;
    private ConnectionInfo info;
    public IConnectionManager manager;
    private boolean isConnected; //是否连接
    private SocketCallBack socketCallBack;
    private byte[] heads=new byte[4];  //存储头部长度信息的字节数组
    private byte [] bodyLenths=new byte[4];        //存储body体的信息长度
    //private CustomDialog customDialog;    //断开连接的弹窗


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
                    //showDialog(activity);
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

/*    *//**
     * 显示Dialog对话框
     * @param activity
     *//*
    private void showDialog(final Activity activity){
       activity.runOnUiThread(new Runnable() {
           @Override
           public void run() {
               customDialog=new CustomDialog(activity, "", 2,"", new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       customDialog.dismiss();
                       Intent intent_login = new Intent();
                       intent_login.setClass(activity,LoginActivity.class);
                       intent_login.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //关键的一句，将新的activity置为栈顶
                       activity.startActivity(intent_login);
                       activity.finish();
                   }
               }, "", new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       customDialog.dismiss();
                   }
               });
               customDialog.setCancelable(false);
               customDialog.show();
           }
       });

    }*/

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







}
