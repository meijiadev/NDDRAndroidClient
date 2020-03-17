package ddr.example.com.nddrandroidclient.protocobuf.processor;

import android.content.Context;

import com.google.protobuf.GeneratedMessageLite;

import DDRCommProto.BaseCmd;
import ddr.example.com.nddrandroidclient.other.Logger;
import ddr.example.com.nddrandroidclient.socket.TcpClient;

public class RspHeartBeatProcess extends BaseProcessor {
    @Override
    public void process(Context context, BaseCmd.CommonHeader commonHeader, GeneratedMessageLite msg) {
        super.process(context, commonHeader, msg);
        TcpClient tcpClient=TcpClient.tcpClient;
        //Logger.e("--------接收心跳");
        if (tcpClient!=null){
            tcpClient.feedDog();
        }
    }
}
