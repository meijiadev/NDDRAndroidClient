package ddr.example.com.ddrandroidclient.protocobuf.processor;

import android.content.Context;

import com.google.protobuf.GeneratedMessageLite;

import DDRCommProto.BaseCmd;
import ddr.example.com.ddrandroidclient.socket.TcpClient;

public class RspHeartBeatProcess extends BaseProcessor {
    @Override
    public void process(Context context, BaseCmd.CommonHeader commonHeader, GeneratedMessageLite msg) {
        super.process(context, commonHeader, msg);
        TcpClient tcpClient=TcpClient.tcpClient;
        if (tcpClient!=null){
            tcpClient.feedDog();
        }
    }
}
